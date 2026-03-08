package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.BeachServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO che permette la manipolazione facilitata della tabella beach_services nel Database attraverso JDBC.
 * Essa è in stretta collaborazione con JdbcBeachRepository.
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachRepository JdbcBeachRepository
 */
class JdbcBeachServicesDao {
    /**
     * Update/Insert di un oggetto BeachServices nel DB
     * @param beachId ID della spiaggia
     * @param srv oggetto BeachServices
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void upsert(Integer beachId, BeachServices srv, Connection connection) throws SQLException {
        //UPDATE se nel caso abbiamo già un beach_services istanziato nel DB
        String updateSql = "UPDATE beach_services " +
                "SET bathrooms = ?, showers = ?, pool = ?, bar = ?, restaurant = ?, wifi = ?, volleyballField = ? " +
                "WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(updateSql)) {
            st.setBoolean(1, srv.bathrooms());
            st.setBoolean(2, srv.showers());
            st.setBoolean(3, srv.pool());
            st.setBoolean(4, srv.bar());
            st.setBoolean(5, srv.restaurant());
            st.setBoolean(6, srv.wifi());
            st.setBoolean(7, srv.volleyballField());
            st.setInt(8, beachId);

            int rows = st.executeUpdate();

            if (rows == 0) {
                //INSERT altrimenti
                insert(beachId, srv, connection);
            }
        }
    }

    /**
     * Inserimento di un oggetto BeachServices nel DB
     * @param beachId ID della spiaggia
     * @param srv oggetto BeachServices
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void insert(Integer beachId, BeachServices srv, Connection connection) throws SQLException {
        //INSERT nuovo elemento beach_services
        String insertSql = "INSERT INTO beach_services (beachId, bathrooms, showers, pool, bar, restaurant, wifi, volleyballField) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
            insertSt.setInt(1, beachId);
            insertSt.setBoolean(2, srv.bathrooms());
            insertSt.setBoolean(3, srv.showers());
            insertSt.setBoolean(4, srv.pool());
            insertSt.setBoolean(5, srv.bar());
            insertSt.setBoolean(6, srv.restaurant());
            insertSt.setBoolean(7, srv.wifi());
            insertSt.setBoolean(8, srv.volleyballField());
            insertSt.executeUpdate();
        }
    }

    /**
     * Eliminazione di un oggetto BeachServices dal DB
     * @param beachId ID della spiaggia
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void delete(Integer beachId, Connection connection) throws SQLException {
        //DELETE un elemento dentro beach_services dato l'ID
        String sql = "DELETE FROM beach_services WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, beachId);
            st.executeUpdate();
        }
    }
}