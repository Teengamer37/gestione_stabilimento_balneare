package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.domain.beach.BeachInventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO che permette la manipolazione facilitata della tabella beach_inventories nel Database attraverso JDBC.<br>
 * Essa è in stretta collaborazione con JdbcBeachRepository.
 *
 * @see JdbcBeachRepository JdbcBeachRepository
 */
class JdbcBeachInventoryDao {
    /**
     * Update/Insert di un oggetto BeachInventory nel DB.
     *
     * @param beachId    ID della spiaggia
     * @param inv        Oggetto BeachInventory
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void upsert(Integer beachId, BeachInventory inv, Connection connection) throws SQLException {
        //UPDATE se nel caso abbiamo già un beach_inventories istanziato nel DB
        String updateSql = "UPDATE beach_inventories " +
                "SET countOmbrelloni = ?, countTende = ?, countExtraSdraio = ?, countExtraLettini = ?, countExtraSedie = ?, countCamerini = ? " +
                "WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(updateSql)) {
            st.setInt(1, inv.countOmbrelloni());
            st.setInt(2, inv.countTende());
            st.setInt(3, inv.countExtraSdraio());
            st.setInt(4, inv.countExtraLettini());
            st.setInt(5, inv.countExtraSedie());
            st.setInt(6, inv.countCamerini());
            st.setInt(7, beachId);

            int rows = st.executeUpdate();

            if (rows == 0) {
                //INSERT altrimenti
                insert(beachId, inv, connection);
            }
        }
    }

    /**
     * Inserimento di un oggetto BeachInventory nel DB.
     *
     * @param beachId    ID della spiaggia
     * @param inv        Oggetto BeachInventory
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void insert(Integer beachId, BeachInventory inv, Connection connection) throws SQLException {
        //INSERT nuovo elemento beach_inventories
        String insertSql = "INSERT INTO beach_inventories (beachId, countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
            insertSt.setInt(1, beachId);
            insertSt.setInt(2, inv.countOmbrelloni());
            insertSt.setInt(3, inv.countTende());
            insertSt.setInt(4, inv.countExtraSdraio());
            insertSt.setInt(5, inv.countExtraLettini());
            insertSt.setInt(6, inv.countExtraSedie());
            insertSt.setInt(7, inv.countCamerini());
            insertSt.executeUpdate();
        }
    }

    /**
     * Eliminazione di un oggetto BeachInventory dal DB.
     *
     * @param beachId    ID della spiaggia
     * @param connection Connessione JDBC
     * @throws SQLException se ci sono problemi col Database
     */
    void delete(Integer beachId, Connection connection) throws SQLException {
        //DELETE un elemento dentro beach_inventories dato l'ID
        String sql = "DELETE FROM beach_inventories WHERE beachId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, beachId);
            st.executeUpdate();
        }
    }
}