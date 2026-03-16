package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.SpotType;
import com.example.s_balneare.domain.layout.Zone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO che permette la manipolazione facilitata della tabella spots nel Database attraverso JDBC.<br>
 * Essa è in stretta collaborazione con JdbcBeachRepository e JdbcZoneDao.
 *
 * @see JdbcBeachRepository JdbcBeachRepository
 * @see JdbcZoneDao JdbcZoneDao
 */
class JdbcSpotDao {
    private final JdbcZoneDao zoneDao;

    public JdbcSpotDao(JdbcZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    /**
     * Sincronizzo tutto il grafo di una lista di spot:<br>
     * creazione zone -> spots.
     *
     * @param beachId    ID della spiaggia
     * @param zones      Lista di zone da sincronizzare
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    void syncZones(Integer beachId, List<Zone> zones, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (zones == null || zones.isEmpty())
            throw new IllegalArgumentException("ERROR: at least one zone must be set");

        //passo 1: eliminazione zone non incluse nella lista passata, ma presenti nel Database
        //trovo i nomi delle Zone salvate nel DB
        List<String> dbZoneNames = zoneDao.findZoneNamesByBeachId(beachId, connection);

        //raccolgo i nomi delle zone
        List<String> zoneNames = new ArrayList<>();
        for (Zone z : zones) {
            zoneNames.add(z.name());
        }

        //elimino dal DB gli Spot delle Zone rimosse in Java
        for (String dbZone : dbZoneNames) {
            if (!zoneNames.contains(dbZone)) {
                deleteSpotsForZone(beachId, dbZone, connection);
            }
        }

        if (zones.isEmpty()) return;


        //passo 2: creazione zone (se non già inserite)
        zoneDao.ensureZonesExist(beachId, zoneNames, connection);

        for (Zone zone : zones) {
            //aggiungo/aggiorno gli Spot per ogni Zone
            upsertSpotsForZone(beachId, zone.name(), zone.spots(), connection);

            //elimina singoli Spot che sono stati tolti dalla Zone
            deleteMissingSpots(beachId, zone.name(), zone.spots(), connection);
        }
    }

    /**
     * Sincronizzo (aggiungo/aggiorno) una singola Zone.
     *
     * @param beachId    ID della spiaggia
     * @param zone       Zona da sincronizzare
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    void syncZone(Integer beachId, Zone zone, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (zone == null) throw new IllegalArgumentException("ERROR: zone not valid");

        //creazione zone (se non già inserite)
        zoneDao.ensureZoneExists(beachId, zone.name(), connection);

        //aggiungo/aggiorno gli Spot per ogni Zone
        upsertSpotsForZone(beachId, zone.name(), zone.spots(), connection);
    }

    /**
     * Elimina il layout fisico di tutte le Zone connesse ad una Beach.<br>
     * Le Zone verranno eliminate più tardi con JdbcZoneDao.deleteOrphanedZones().
     *
     * @param beachId    ID della spiaggia
     * @param connection Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcZoneDao#deleteOrphanedZones(Integer, Connection) JdbcZoneDao.deleteOrphanedZones()
     * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachRepository#update(Beach, TransactionContext) JdbcBeachRepository.update()
     */
    void deleteAllZones(Integer beachId, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) {
            throw new IllegalArgumentException("ERROR: beachId not valid");
        }

        String sql = "DELETE FROM spots WHERE beachId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.executeUpdate();
        }
    }

    /**
     * Trova tutte le Zone di una Beach.
     *
     * @param beachId ID della spiaggia
     * @param conn    Connessione JDBC
     * @return Lista di Zone trovate di quella spiaggia
     * @throws SQLException se ci sono problemi col Database
     */
    List<Zone> findZonesByBeachId(Integer beachId, Connection conn) throws SQLException {
        //uso una Map<nome + lista di Spot> per raggruppare gli Spot per ogni Zone
        Map<String, List<Spot>> zoneMap = new HashMap<>();

        //cerco tutte le zone (anche quelle vuote)
        String sqlZones = "SELECT name FROM zones WHERE beachId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlZones)) {
            ps.setInt(1, beachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    zoneMap.put(rs.getString("name"), new ArrayList<>());
                }
            }
        }

        //trovo tutti gli Spot e li inserisco assieme alle Zone rispettive
        String sqlSpots = "SELECT id, zoneName, `row`, `column`, type FROM spots WHERE beachId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSpots)) {
            ps.setInt(1, beachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String zoneName = rs.getString("zoneName");
                    Spot spot = new Spot(
                            rs.getInt("id"),
                            SpotType.valueOf(rs.getString("type")),
                            rs.getInt("row"),
                            rs.getInt("column")
                    );

                    //aggiungo lo Spot alla Map
                    zoneMap.computeIfPresent(zoneName, (_, list) -> {
                        list.add(spot);
                        return list;
                    });
                }
            }
        }

        //converto la Map in List<Zone>
        List<Zone> zones = new ArrayList<>();
        for (Map.Entry<String, List<Spot>> entry : zoneMap.entrySet()) {
            zones.add(new Zone(entry.getKey(), entry.getValue()));
        }
        return zones;
    }

    /**
     * Controlla che tutti gli Spot appartengano a quella spiaggia.
     *
     * @param beachId ID della spiaggia
     * @param spotIds Lista di ID degli Spot da controllare
     * @param conn    Connessione JDBC
     * @return se appartengono tutti alla spiaggia o meno (boolean)
     * @throws SQLException se ci sono problemi col Database
     */
    public boolean doSpotsBelongToBeach(Integer beachId, List<Integer> spotIds, Connection conn) throws SQLException {
        if (spotIds == null || spotIds.isEmpty()) return false;

        //creazione di query dinamica per aggiungere gli N spot indipendentemente dalla dimensione della lista
        StringBuilder sql = new StringBuilder("SELECT COUNT(id) FROM spots WHERE beachId = ? AND id IN (");
        for (int i = 0; i < spotIds.size(); i++) {
            sql.append("?");
            if (i < spotIds.size() - 1) sql.append(", ");
        }
        sql.append(")");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, beachId);
            for (int i = 0; i < spotIds.size(); i++) {
                ps.setInt(i + 2, spotIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    //se il COUNT() restituisce la dimensione della lista, allora tutti gli Spot appartengono alla spiaggia
                    return rs.getInt(1) == spotIds.size();
                }
            }
        }
        return false;
    }


    //HELPERS

    /**
     * Inserisce vari Spot per una determinata Zone.
     *
     * @param beachId  ID della spiaggia
     * @param zoneName Nome della zona
     * @param spots    Lista di Spot da inserire
     * @param conn     Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    private void upsertSpotsForZone(Integer beachId, String zoneName, List<Spot> spots, Connection conn) throws SQLException {
        if (spots == null || spots.isEmpty())
            throw new IllegalArgumentException("ERROR: at least one spot must be set for zone");

        //inserisco Spot
        //se la combinazione (row, column, zoneName, beachId) esiste, aggiorno solo il tipo
        String sql = "INSERT INTO spots (beachId, zoneName, `row`, `column`, type) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE type = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Spot spot : spots) {
                //parametri INSERT
                ps.setInt(1, beachId);
                ps.setString(2, zoneName);
                ps.setInt(3, spot.row());
                ps.setInt(4, spot.column());
                ps.setString(5, spot.type().name());

                //parametro UPDATE
                ps.setString(6, spot.type().name());

                //preparo tutte le query da mandare...
                ps.addBatch();
            }
            //...alla fine eseguo tutto
            ps.executeBatch();
        }
    }

    /**
     * Cancella tutti gli Spot di una specifica Zone.
     *
     * @param beachId  ID della spiaggia
     * @param zoneName Nome della zona
     * @param conn     Connessione JDBC
     * @throws SQLException             se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
    private void deleteSpotsForZone(Integer beachId, String zoneName, Connection conn) throws SQLException {
        String sql = "DELETE FROM spots WHERE beachId = ? AND zoneName = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setString(2, zoneName);
            ps.executeUpdate();
        }
    }

    /**
     * Cancella i singoli Spot che non esistono più nell'oggetto Java (quelli non associati a nessuna Zone).
     *
     * @param beachId   ID della spiaggia
     * @param zoneName  Nome della zona
     * @param javaSpots Lista di Spot da controllare
     * @param conn      Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    private void deleteMissingSpots(Integer beachId, String zoneName, List<Spot> javaSpots, Connection conn) throws SQLException {
        if (javaSpots == null) javaSpots = new ArrayList<>();

        //costruisco una lista delle coordinate (row_col) presenti in javaSpots
        List<String> javaCoordinates = new ArrayList<>();
        for (Spot s : javaSpots) javaCoordinates.add(s.row() + "_" + s.column());

        String selectSql = "SELECT id, `row`, `column` FROM spots WHERE beachId = ? AND zoneName = ?";
        String deleteSql = "DELETE FROM spots WHERE id = ?";

        try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
             PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {

            selectPs.setInt(1, beachId);
            selectPs.setString(2, zoneName);

            try (ResultSet rs = selectPs.executeQuery()) {
                while (rs.next()) {
                    //ricavo ID e coordinate spot
                    int dbId = rs.getInt("id");
                    String dbCoord = rs.getInt("row") + "_" + rs.getInt("column");

                    //se dbCoord non esiste in javaCoordinates, elimino l'ID dal DB
                    if (!javaCoordinates.contains(dbCoord)) {
                        deletePs.setInt(1, dbId);
                        deletePs.addBatch();
                    }
                }
            }
            deletePs.executeBatch();
        }
    }
}