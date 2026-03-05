package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.Pricing;
import com.example.s_balneare.domain.beach.Season;
import com.example.s_balneare.domain.beach.ZoneTariff;

import java.sql.*;
import java.util.List;

class JdbcSeasonDao {
    //sincronizzo tutto il grafo di una lista di stagioni
    //creazione pricing -> season -> zone -> zoneTariff
    void syncSeasons(Integer beachId, List<Season> seasons, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (seasons == null || seasons.isEmpty()) throw new IllegalArgumentException("ERROR: at least one season must be set");

        for (Season season : seasons) {
            //passo 1: gestione Pricing
            //se pricing.id = null, pricing viene inserito
            //se pricing.id != null, pricing viene aggiornato
            int pricingId = upsertPricing(season.pricing(), connection);

            //passo 2: gestione Season
            //collega season al beachId e al pricingId
            upsertSeasonRow(beachId, season, pricingId, connection);

            //passo 3: gestione Zone e ZoneTariff
            upsertZonesAndTariffs(beachId, season, connection);
        }
    }

    //sincronizzo (aggiungo/aggiorno) una singola stagione
    void syncSeason(Integer beachId, Season season, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (season == null) throw new IllegalArgumentException("ERROR: season not valid");

        //passo 1: gestione Pricing
        //se pricing.id = null, pricing viene inserito
        //se pricing.id != null, pricing viene aggiornato
        int pricingId = upsertPricing(season.pricing(), connection);

        //passo 2: gestione Season
        //collega season al beachId e al pricingId
        upsertSeasonRow(beachId, season, pricingId, connection);

        //passo 3: gestione Zone e ZoneTariff
        upsertZonesAndTariffs(beachId, season, connection);
    }


    //HELPERS
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

    private void upsertZonesAndTariffs(Integer beachId, Season s, Connection connection) throws SQLException {
        if (s.zoneTariffs() == null || s.zoneTariffs().isEmpty()) throw new IllegalArgumentException("ERROR: at least one zoneTariff must be set for season for update");

        for (ZoneTariff tariff : s.zoneTariffs()) {
            //verifico se la Zone esiste nel DB
            //con INSERT IGNORE se una Zone esiste già per questa Beach, non la riaggiunge
            //al contrario, se NON esiste, essa viene aggiunta
            String insertZone = "INSERT IGNORE INTO zones (name, beachId) " +
                    "VALUES (?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(insertZone)) {
                ps.setString(1, tariff.zoneName());
                ps.setInt(2, beachId);
                ps.executeUpdate();
            }

            //inserisco/aggiorno la ZoneTariff per quella Zone in questa Season
            //stessa logica di prima: se esiste già, aggiorno solo alcuni parametri
            String upsertTariff = "INSERT INTO zone_tariffs (seasonName, beachId, zoneName, priceOmbrellone, priceTenda) VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE priceOmbrellone = ?, priceTenda = ?";

            try (PreparedStatement ps = connection.prepareStatement(upsertTariff)) {
                //parametri INSERT
                ps.setString(1, s.name());
                ps.setInt(2, beachId);
                ps.setString(3, tariff.zoneName());
                ps.setDouble(4, tariff.priceOmbrellone());
                ps.setDouble(5, tariff.priceTenda());

                //parametri UPDATE
                ps.setDouble(6, tariff.priceOmbrellone());
                ps.setDouble(7, tariff.priceTenda());

                ps.executeUpdate();
            }
        }
    }
}