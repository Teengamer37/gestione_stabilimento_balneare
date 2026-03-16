package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.Parking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO che permette la manipolazione facilitata della tabella parkings nel Database attraverso JDBC.<br>
 * Essa è in stretta collaborazione con JdbcBeachRepository.
 *
 * @see JdbcBeachRepository JdbcBeachRepository
 */
class JdbcParkingDao {
    /**
     * Update/Insert di un oggetto Parking nel DB.
     *
     * @param beachId    ID della spiaggia
     * @param parking    oggetto Parking
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void upsert(Integer beachId, Parking parking, Connection connection) throws SQLException {
        //UPDATE se nel caso abbiamo già un parking istanziato nel DB
        String updateSql = "UPDATE parkings " +
                "SET nAutoPark = ?, nMotoPark = ?, nBikePark = ?, nElectricPark = ?, CCTV = ? " +
                "WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(updateSql)) {
            st.setInt(1, parking.nAutoPark());
            st.setInt(2, parking.nMotoPark());
            st.setInt(3, parking.nBikePark());
            st.setInt(4, parking.nElectricPark());
            st.setBoolean(5, parking.CCTV());
            st.setInt(6, beachId);

            int rows = st.executeUpdate();

            if (rows == 0) {
                //INSERT altrimenti
                insert(beachId, parking, connection);
            }
        }
    }

    /**
     * Inserimento di un oggetto Parking nel DB.
     *
     * @param beachId    ID della spiaggia
     * @param parking    oggetto Parking
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void insert(Integer beachId, Parking parking, Connection connection) throws SQLException {
        //INSERT nuovo elemento parkings
        String insertSql = "INSERT INTO parkings (beachId, nAutoPark, nMotoPark, nBikePark, nElectricPark, CCTV) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
            insertSt.setInt(1, beachId);
            insertSt.setInt(2, parking.nAutoPark());
            insertSt.setInt(3, parking.nMotoPark());
            insertSt.setInt(4, parking.nBikePark());
            insertSt.setInt(5, parking.nElectricPark());
            insertSt.setBoolean(6, parking.CCTV());
            insertSt.executeUpdate();
        }
    }

    /**
     * Eliminazione di un oggetto Parking dal DB.
     *
     * @param beachId    ID della spiaggia
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void delete(Integer beachId, Connection connection) throws SQLException {
        //DELETE un elemento dentro parkings dato l'ID
        String sql = "DELETE FROM parkings WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, beachId);
            st.executeUpdate();
        }
    }
}