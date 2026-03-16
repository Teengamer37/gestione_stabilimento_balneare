package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.Pricing;
import com.example.s_balneare.domain.beach.Season;
import com.example.s_balneare.domain.beach.ZoneTariff;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO che permette la manipolazione facilitata delle tabelle seasons, pricings e zone_tariffs nel Database attraverso JDBC.<br>
 * Essa è in stretta collaborazione con JdbcBeachRepository e JdbcZoneDao.
 *
 * @see JdbcBeachRepository JdbcBeachRepository
 * @see JdbcZoneDao JdbcZoneDao
 */
class JdbcSeasonDao {
    private final JdbcZoneDao zoneDao;

    public JdbcSeasonDao(JdbcZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    /**
     * Sincronizzo tutto il grafo di una lista di stagioni:<br>
     * Creazione pricing -> season -> zone -> zoneTariff
     *
     * @param beachId    ID della spiaggia
     * @param seasons    Lista di stagioni da sincronizzare
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    void syncSeasons(Integer beachId, List<Season> seasons, Connection connection) throws SQLException {
        //check validità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (seasons == null || seasons.isEmpty())
            throw new IllegalArgumentException("ERROR: at least one season must be set");

        //passo 1: trovo i nomi delle stagioni salvate attualmente nel DB
        List<String> dbSeasonNames = getSeasonNamesForBeach(beachId, connection);

        //passo 2: creo una lista dei nomi delle stagioni passate da Java
        List<String> javaSeasonNames = new ArrayList<>();
        for (Season s : seasons) javaSeasonNames.add(s.name());

        //passo 3: se una stagione è nel DB ma non in Java, la elimino
        for (String dbSeason : dbSeasonNames) {
            if (!javaSeasonNames.contains(dbSeason)) {
                deleteSeason(beachId, dbSeason, connection);
            }
        }
        //se alla fine vengono cancellate tutte le stagioni, mi fermo
        if (seasons.isEmpty()) return;

        //passo 4: prendo tutti i nomi delle zone
        List<String> zoneNames = new ArrayList<>();
        for (Season s : seasons) {
            if (s.zoneTariffs() != null) {
                for (ZoneTariff zt : s.zoneTariffs()) {
                    if (!zoneNames.contains(zt.zoneName())) zoneNames.add(zt.zoneName());
                }
            }
        }
        zoneDao.ensureZonesExist(beachId, zoneNames, connection);

        //passo 5: aggiorno le stagioni una ad una
        for (Season season : seasons) {
            int pricingId = upsertPricing(season.pricing(), connection);
            upsertSeasonRow(beachId, season, pricingId, connection);
            upsertZonesAndTariffs(beachId, season, connection);

            //passo 5b: elimino tariffe rimosse dalla stagione
            deleteMissingTariffs(beachId, season, connection);
        }
    }

    //HELPERS DI SYNC

    /**
     * Update/Insert di un oggetto Pricing nel DB.
     *
     * @param p          oggetto Pricing
     * @param connection Connessione JDBC
     * @return ID generato dal DB all'inserimento; stesso ID di pricing passato se già presente nel DB
     * @throws SQLException se ci sono problemi col Database
     */
    private int upsertPricing(Pricing p, Connection connection) throws SQLException {
        //caso 1: Pricing già presente -> UPDATE
        if (p.id() != null && p.id() > 0) {
            String updateSql = "UPDATE pricings " +
                    "SET priceLettino=?, priceSdraio=?, priceSedia=?, priceParking=?, priceCamerino=? " +
                    "WHERE id=?";

            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setDouble(1, p.priceLettino());
                ps.setDouble(2, p.priceSdraio());
                ps.setDouble(3, p.priceSedia());
                ps.setDouble(4, p.priceParking());
                ps.setDouble(5, p.priceCamerino());
                ps.setInt(6, p.id());
                ps.executeUpdate();

                return p.id();
            }
        }

        //caso 2: Pricing nuovo -> INSERT
        String insertSql = "INSERT INTO pricings (priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, p.priceLettino());
            ps.setDouble(2, p.priceSdraio());
            ps.setDouble(3, p.priceSedia());
            ps.setDouble(4, p.priceParking());
            ps.setDouble(5, p.priceCamerino());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    //ritorna ID generato
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: creation of pricing failed: no ID obtained");
                }
            }
        }
    }

    /**
     * Update/Insert di una singola stagione nel DB.
     *
     * @param beachId    ID della spiaggia
     * @param s          oggetto Season
     * @param pricingId  ID del Pricing associato alla stagione
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void upsertSeasonRow(Integer beachId, Season s, int pricingId, Connection connection) throws SQLException {
        //uso ON DUPLICATE KEY UPDATE basato sulla chiave univoca (beachId, name)
        //se la stagione "Estate 2026" esiste già, aggiorno solo startDate, endDate e pricing
        String sql = "INSERT INTO seasons (beachId, name, startDate, endDate, pricingsId) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE startDate = ?, endDate = ?, pricingsId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            //parametri INSERT
            ps.setInt(1, beachId);
            ps.setString(2, s.name());
            ps.setDate(3, java.sql.Date.valueOf(s.startDate()));
            ps.setDate(4, java.sql.Date.valueOf(s.endDate()));
            ps.setInt(5, pricingId);

            //parametri UPDATE
            ps.setDate(6, java.sql.Date.valueOf(s.startDate()));
            ps.setDate(7, java.sql.Date.valueOf(s.endDate()));
            ps.setInt(8, pricingId);

            ps.executeUpdate();
        }
    }

    /**
     * Update/Insert di Zone e ZoneTariff nel DB.
     *
     * @param beachId    ID della spiaggia
     * @param s          oggetto Season
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    private void upsertZonesAndTariffs(Integer beachId, Season s, Connection connection) throws SQLException {
        //check validità parametri
        if (s.zoneTariffs() == null || s.zoneTariffs().isEmpty())
            throw new IllegalArgumentException("ERROR: at least one zoneTariff must be set for season for update");

        //inserisco/aggiorno la ZoneTariff per quella Zone in questa Season
        //stessa logica di prima: se esiste già, aggiorno solo alcuni parametri
        String upsertTariff = "INSERT INTO zone_tariffs (seasonName, beachId, zoneName, priceOmbrellone, priceTenda) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE priceOmbrellone = ?, priceTenda = ?";

        try (PreparedStatement ps = connection.prepareStatement(upsertTariff)) {
            for (ZoneTariff zt : s.zoneTariffs()) {
                //parametri INSERT
                ps.setString(1, s.name());
                ps.setInt(2, beachId);
                ps.setString(3, zt.zoneName());
                ps.setDouble(4, zt.priceOmbrellone());
                ps.setDouble(5, zt.priceTenda());

                //parametri UPDATE
                ps.setDouble(6, zt.priceOmbrellone());
                ps.setDouble(7, zt.priceTenda());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Ricavo tutti i nomi delle stagioni associate ad una spiaggia.
     *
     * @param beachId ID della spiaggia
     * @param conn    Connessione JDBC
     * @return lista di nomi delle stagioni appartenenti a quella spiaggia
     * @throws SQLException se ci sono problemi col Database
     */
    private List<String> getSeasonNamesForBeach(Integer beachId, Connection conn) throws SQLException {
        List<String> names = new ArrayList<>();

        String sql = "SELECT name FROM seasons WHERE beachId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) names.add(rs.getString("name"));
            }
        }
        return names;
    }

    /**
     * Elimina le tariffe che non esistono più nell'oggetto Java.
     *
     * @param beachId ID della spiaggia
     * @param season  stagione da sincronizzare
     * @param conn    Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deleteMissingTariffs(Integer beachId, Season season, Connection conn) throws SQLException {
        //passo 1: creo una lista di tutte le tariffe salvate nel mio oggetto Java
        List<String> javaTariffZones = new ArrayList<>();
        if (season.zoneTariffs() != null) {
            for (ZoneTariff zt : season.zoneTariffs()) javaTariffZones.add(zt.zoneName());
        }

        String selectSql = "SELECT zoneName FROM zone_tariffs WHERE beachId = ? AND seasonName = ?";
        String deleteSql = "DELETE FROM zone_tariffs WHERE beachId = ? AND seasonName = ? AND zoneName = ?";

        try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
             PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
            selectPs.setInt(1, beachId);
            selectPs.setString(2, season.name());

            try (ResultSet rs = selectPs.executeQuery()) {
                while (rs.next()) {
                    String dbZoneName = rs.getString("zoneName");

                    //se c'è nel DB ma non nell'oggetto Java, elimino dal DB
                    if (!javaTariffZones.contains(dbZoneName)) {
                        deletePs.setInt(1, beachId);
                        deletePs.setString(2, season.name());
                        deletePs.setString(3, dbZoneName);
                        deletePs.addBatch();
                    }
                }
            }
            deletePs.executeBatch();
        }
    }


    /**
     * Elimina tutte le stagioni di una spiaggia (usato quando si elimina l'intera Beach).<br>
     * NOTA: non vado a eliminare le zone; usare metodo di JdbcZoneDao se si vuole procedere anche con l’eliminazione delle zone.
     *
     * @param beachId    ID della spiaggia
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @see JdbcZoneDao#deleteAllZones(Integer, Connection) JdbcZoneDao.deleteAllZones()
     */
    void deleteAllSeasons(Integer beachId, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");

        //passo 1: trovo tutti gli ID dei Pricing associati alla Beach
        List<Integer> pricingIds = getPricingIdsForBeach(beachId, connection);

        //passo 2: elimino le dipendenze (ZoneTariff)
        deleteAllZoneTariffs(beachId, connection);

        //passo 3: elimino le Season
        deleteSeasonsRow(beachId, connection);

        //passo 4: elimino i Pricing
        deletePricings(pricingIds, connection);
    }

    /**
     * Elimina una determinata stagione di una spiaggia.<br>
     * NOTA: non vado a eliminare le zone; usare metodo di JdbcZoneDao se si vuole procedere con anche l’eliminazione delle zone.
     *
     * @param beachId    ID della spiaggia
     * @param seasonName Nome della stagione da eliminare
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcZoneDao#deleteZone(Integer, String, Connection) JdbcZoneDao.deleteZone()
     */
    void deleteSeason(Integer beachId, String seasonName, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (seasonName == null || seasonName.isEmpty())
            throw new IllegalArgumentException("ERROR: seasonName not valid");

        //passo 1: trovo l'ID Pricing per questa Season
        Integer pricingId = getPricingIdForSeason(beachId, seasonName, connection);

        //passo 2: elimino le ZoneTariff associate a questa Season
        deleteZoneTariffsForSeason(beachId, seasonName, connection);

        //passo 3: elimino Season
        deleteSingleSeasonRow(beachId, seasonName, connection);

        //passo 4: elimino Pricing
        if (pricingId != null) {
            deletePricings(List.of(pricingId), connection);
        }
    }


    //HELPERS DI DELETE

    /**
     * Genera lista di ID di pricings associati ad una determinata Beach.
     *
     * @param beachId ID della spiaggia
     * @param conn    Connessione JDBC
     * @return lista di ID di pricings associati ad una determinata Beach
     * @throws SQLException se ci sono problemi col Database
     */
    private List<Integer> getPricingIdsForBeach(Integer beachId, Connection conn) throws SQLException {
        List<Integer> ids = new ArrayList<>();

        String sql = "SELECT pricingsId FROM seasons WHERE beachId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }

    /**
     * Ritorna ID di un record di pricings associato ad una determinata Season.
     *
     * @param beachId    ID della spiaggia
     * @param seasonName Nome della stagione
     * @param conn       Connessione JDBC
     * @return ID di un record di pricings associato ad una determinata Season
     * @throws SQLException se ci sono problemi col Database
     */
    private Integer getPricingIdForSeason(Integer beachId, String seasonName, Connection conn) throws SQLException {
        String sql = "SELECT pricingsId FROM seasons WHERE beachId = ? AND name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setString(2, seasonName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        //se qualcosa va storto, return null
        return null;
    }

    /**
     * Cancella tutte le ZoneTariff di tutte le Zone di una determinata Beach.
     *
     * @param beachId ID della spiaggia
     * @param conn    Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deleteAllZoneTariffs(Integer beachId, Connection conn) throws SQLException {
        String sql = "DELETE FROM zone_tariffs WHERE beachId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.executeUpdate();
        }
    }

    /**
     * Cancella tutte le ZoneTariff di tutte le Zone di una determinata Season.
     *
     * @param beachId    ID della spiaggia
     * @param seasonName Nome della stagione dalla quale eliminare le ZoneTariff
     * @param conn       Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deleteZoneTariffsForSeason(Integer beachId, String seasonName, Connection conn) throws SQLException {
        String sql = "DELETE FROM zone_tariffs WHERE beachId = ? AND seasonName = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setString(2, seasonName);
            ps.executeUpdate();
        }
    }

    /**
     * Cancella tutte le Season di una determinata Beach.
     *
     * @param beachId ID della spiaggia
     * @param conn    Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deleteSeasonsRow(Integer beachId, Connection conn) throws SQLException {
        String sql = "DELETE FROM seasons WHERE beachId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.executeUpdate();
        }
    }

    /**
     * Cancella una determinata Season di una determinata Beach.
     *
     * @param beachId    ID della spiaggia
     * @param seasonName Nome della stagione da eliminare
     * @param conn       Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deleteSingleSeasonRow(Integer beachId, String seasonName, Connection conn) throws SQLException {
        String sql = "DELETE FROM seasons WHERE beachId = ? AND name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setString(2, seasonName);
            ps.executeUpdate();
        }
    }

    /**
     * Cancella varie righe di pricings.
     *
     * @param pricingIds Lista di ID di pricings
     * @param conn       Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deletePricings(List<Integer> pricingIds, Connection conn) throws SQLException {
        if (pricingIds == null || pricingIds.isEmpty()) return;

        String sql = "DELETE FROM pricings WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer id : pricingIds) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    /**
     * Cerca le stagioni di una determinata Beach.
     *
     * @param beachId ID della spiaggia
     * @param conn    Connessione JDBC
     * @return lista di stagioni di una determinata spiaggia
     * @throws SQLException se ci sono problemi col Database
     */
    public List<Season> findSeasonsByBeachId(Integer beachId, Connection conn) throws SQLException {
        Map<String, Season.Builder> seasonBuilders = new HashMap<>();

        //leggo Season e Pricing
        String sqlSeasons = "SELECT s.name AS seasonName, s.startDate, s.endDate, " +
                "p.id AS pricingId, p.priceLettino, p.priceSdraio, p.priceSedia, p.priceParking, p.priceCamerino " +
                "FROM seasons s " +
                "JOIN pricings p ON s.pricingsId = p.id " +
                "WHERE s.beachId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSeasons)) {
            ps.setInt(1, beachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String seasonName = rs.getString("seasonName");

                    //creo oggetto Pricing
                    Pricing pricing = new Pricing(
                            rs.getInt("pricingId"),
                            rs.getDouble("priceLettino"),
                            rs.getDouble("priceSdraio"),
                            rs.getDouble("priceSedia"),
                            rs.getDouble("priceParking"),
                            rs.getDouble("priceCamerino")
                    );

                    //creo oggetto Season
                    Season.Builder builder = Season.builder()
                            .name(seasonName)
                            .startDate(rs.getDate("startDate").toLocalDate())
                            .endDate(rs.getDate("endDate").toLocalDate())
                            .pricing(pricing)
                            //inizializzo zoneTariffs
                            .zoneTariffs(new ArrayList<>());

                    seasonBuilders.put(seasonName, builder);
                }
            }
        }

        //leggo le ZoneTariffs e le aggiungo ai rispettivi Builder
        String sqlTariffs = "SELECT seasonName, zoneName, priceOmbrellone, priceTenda FROM zone_tariffs WHERE beachId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlTariffs)) {
            ps.setInt(1, beachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String seasonName = rs.getString("seasonName");
                    //creo oggetto ZoneTariff
                    ZoneTariff tariff = new ZoneTariff(
                            rs.getString("zoneName"),
                            rs.getDouble("priceOmbrellone"),
                            rs.getDouble("priceTenda")
                    );

                    //cerco il builder corrispondente alla Season
                    Season.Builder builder = seasonBuilders.get(seasonName);
                    if (builder != null) {
                        //aggiungo la ZoneTariff
                        //prendo la List di zoneTariff, lo copio in un oggetto, aggiungo la nuova ZoneTariff, salvo dentro il builder
                        List<ZoneTariff> currentTariffs = builder.build().zoneTariffs();
                        List<ZoneTariff> newTariffs = new ArrayList<>(currentTariffs);
                        newTariffs.add(tariff);
                        builder.zoneTariffs(newTariffs);
                    }
                }
            }
        }

        //converto la Map in List<Season>
        List<Season> finalSeasons = new ArrayList<>();
        for (Season.Builder builder : seasonBuilders.values()) {
            finalSeasons.add(builder.build());
        }
        return finalSeasons;
    }
}