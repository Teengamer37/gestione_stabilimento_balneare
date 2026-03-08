package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO che permette la manipolazione facilitata della tabella zones nel Database attraverso JDBC.
 * Essa è in stretta collaborazione con JdbcBeachRepository, JdbcSpotDao e JdbcSeasonDao.
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachRepository JdbcBeachRepository
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcSpotDao JdbcSpotDao
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcSeasonDao JdbcSeasonDao
 */
class JdbcZoneDao {
    /**
     * Assicura che una zona esista nel DB
     * @param beachId ID della spiaggia
     * @param zoneName Nome della zona
     * @param conn Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void ensureZoneExists(Integer beachId, String zoneName, Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO zones (name, beachId) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, zoneName);
            ps.setInt(2, beachId);
            ps.executeUpdate();
        }
    }

    /**
     * Assicura che una lista di zone esistano nel DB
     * @param beachId ID della spiaggia
     * @param zoneNames Lista di nomi di zone
     * @param conn Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     * @throws IllegalArgumentException se ci sono parametri non validi
     */
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

    /**
     * Elimina tutte le zone associate ad una spiaggia
     * @param beachId ID della spiaggia
     * @param conn Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void deleteAllZones(Integer beachId, Connection conn) throws SQLException {
        String sql = "DELETE FROM zones WHERE beachId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una singola Zone di una Beach
     * @param beachId ID della spiaggia
     * @param zoneName Nome della zona da eliminare
     * @param conn Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void deleteZone(Integer beachId, String zoneName, Connection conn) throws SQLException {
        String sql = "DELETE FROM zones WHERE beachId = ? AND name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setString(2, zoneName);
            ps.executeUpdate();
        }
    }

    /**
     * Cancella le Zone che non hanno nè Spot nè ZoneTariff connesse
     * @param beachId ID della spiaggia
     * @param conn Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void deleteOrphanedZones(Integer beachId, Connection conn) throws SQLException {
        String sql = "DELETE z FROM zones z " +
                "LEFT JOIN spots s ON z.name = s.zoneName AND z.beachId = s.beachId " +
                "LEFT JOIN zone_tariffs zt ON z.name = zt.zoneName AND z.beachId = zt.beachId " +
                "WHERE z.beachId = ? AND s.zoneName IS NULL AND zt.zoneName IS NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.executeUpdate();
        }
    }

    /**
     * Rinomina una Zone di una Beach
     * @param beachId ID della spiaggia
     * @param oldZoneName Vecchio nome della zona da rinominare
     * @param newZoneName Nuovo nome della zona
     * @param conn Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void renameZone(Integer beachId, String oldZoneName, String newZoneName, Connection conn) throws SQLException {
        String sql = "UPDATE zones SET name = ? WHERE beachId = ? AND name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newZoneName);
            ps.setInt(2, beachId);
            ps.setString(3, oldZoneName);
            ps.executeUpdate();
        }
    }

    /**
     * Trova tutte le Zone.name di una Beach
     * @param beachId ID della spiaggia
     * @param conn Connessione JDBC
     * @return una lista di nomi delle zone di una spiaggia
     * @throws SQLException se ci sono problemi col Database
     */
    List<String> findZoneNamesByBeachId(Integer beachId, Connection conn) throws SQLException {
        List<String> zoneNames = new ArrayList<>();
        String sql = "SELECT name FROM zones WHERE beachId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    zoneNames.add(rs.getString("name"));
                }
            }
        }
        return zoneNames;
    }
}