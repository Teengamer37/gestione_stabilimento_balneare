package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class JdbcZoneDao {
    //assicura che una zona esista nel DB
    void ensureZoneExists(Integer beachId, String zoneName, Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO zones (name, beachId) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, zoneName);
            ps.setInt(2, beachId);
            ps.executeUpdate();
        }
    }

    //assicura che una lista di zone esitono nel DB
    void ensureZonesExist(Integer beachId, List<String> zoneNames, Connection conn) throws SQLException {
        if (zoneNames == null || zoneNames.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set in order to call this function");

        String sql = "INSERT IGNORE INTO zones (name, beachId) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String name : zoneNames) {
                ps.setString(1, name);
                ps.setInt(2, beachId);

                //creo un set di query SQL...
                ps.addBatch();
            }
            //...per poi eseguire la query tutto in un colpo solo
            ps.executeBatch();
        }
    }

    // TODO: implementare questi metodi
    // - Cancellare una zona
    // - Rinominare una zona
    // - Trovare tutte le zone di una spiaggia
}