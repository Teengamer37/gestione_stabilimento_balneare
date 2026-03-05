package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.Parking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class JdbcParkingDao {
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

    void delete(Integer beachId, Connection connection) throws SQLException {
        //DELETE un elemento dentro parkings dato l'ID
        String sql = "DELETE FROM parkings WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, beachId);
            st.executeUpdate();
        }
    }
}