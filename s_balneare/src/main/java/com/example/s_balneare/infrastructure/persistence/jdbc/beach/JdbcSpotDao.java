package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.Zone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class JdbcSpotDao {
    //sincronizzo tutto il grafo di una lista di spot
    //creazione zone -> spots
    void syncZones(Integer beachId, List<Zone> zones, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (zones == null || zones.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set");

        for (Zone zone : zones) {
            //passo 1: verifico se Zone esiste nel DB
            ensureZoneExists(beachId, zone.name(), connection);

            //passo 2: salvo gli Spot appartenenti a questa Zone
            upsertSpotsForZone(beachId, zone.name(), zone.spots(), connection);
        }
    }

    //sincronizzo (aggiungo/aggiorno) una singola Zone
    void syncZone(Integer beachId, Zone zone, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (zone == null) throw new IllegalArgumentException("ERROR: zone not valid");

        //passo 1: verifico se Zone esiste nel DB
        ensureZoneExists(beachId, zone.name(), connection);

        //passo 2: salvo gli Spot appartenenti a questa Zone
        upsertSpotsForZone(beachId, zone.name(), zone.spots(), connection);
    }


    //HELPERS
    private void ensureZoneExists(Integer beachId, String zoneName, Connection conn) throws SQLException {
        //INSERT IGNORE: se Zone presente, essa non viene aggiunta
        String sql = "INSERT IGNORE INTO zones (name, beachId) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, zoneName);
            ps.setInt(2, beachId);
            ps.executeUpdate();
        }
    }

    private void upsertSpotsForZone(Integer beachId, String zoneName, List<Spot> spots, Connection conn) throws SQLException {
        if (spots == null || spots.isEmpty()) throw new IllegalArgumentException("ERROR: at least one spot must be set for zone");

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
}