package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.Zone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class JdbcSpotDao {
    private final JdbcZoneDao zoneDao;

    public JdbcSpotDao(JdbcZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    //sincronizzo tutto il grafo di una lista di spot
    //creazione zone -> spots
    void syncZones(Integer beachId, List<Zone> zones, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (zones == null || zones.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set");

        //raccolgo i nomi delle zone
        List<String> zoneNames = new ArrayList<>();
        for (Zone z : zones) {
            zoneNames.add(z.name());
        }

        //creazione zone (se non già inserite)
        zoneDao.ensureZonesExist(beachId, zoneNames, connection);

        for (Zone zone : zones) {
            //aggiungo/aggiorno gli Spot per ogni Zone
            upsertSpotsForZone(beachId, zone.name(), zone.spots(), connection);
        }
    }

    //sincronizzo (aggiungo/aggiorno) una singola Zone
    void syncZone(Integer beachId, Zone zone, Connection connection) throws SQLException {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (zone == null) throw new IllegalArgumentException("ERROR: zone not valid");

        //creazione zone (se non già inserite)
        zoneDao.ensureZoneExists(beachId, zone.name(), connection);

        //aggiungo/aggiorno gli Spot per ogni Zone
        upsertSpotsForZone(beachId, zone.name(), zone.spots(), connection);
    }


    //HELPERS
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