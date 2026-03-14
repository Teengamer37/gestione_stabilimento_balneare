package com.example.s_balneare.infrastructure.persistence.jdbc.booking;

import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.AvailabilityQuery;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class JdbcAvailabilityQuery implements AvailabilityQuery {
    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext) e
     * lo converte di nuovo nella classe concreta per estrarre java.sql.Connection
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }

    /**
     * Estrae il numero di parcheggi prenotati in un giorno specifico ad una determinata spiaggia
     * @param beachId ID della spiaggia
     * @param date Data da cercare
     * @param context Connessione JDBC
     * @return Il record di numeri di parcheggi occupati in quella data
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public BookedParkingSpaces getBookedParking(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context) {
        if (beachId == null || date == null) throw new IllegalArgumentException("ERROR: the parameter(s) is/are not valid");
        if (excludeBookingId != null && excludeBookingId <= 0) throw new IllegalArgumentException("ERROR: the excludeBookingId parameter is not valid");

        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //sommo tutti i parcheggi di quella data e di quella spiaggia che hanno come stato booking PENDING o CONFIRMED
        //Nota: COALESCE sostituisce NULL con 0
        String sql = "SELECT " +
                "COALESCE(SUM(autoPark), 0) AS sumAuto, " +
                "COALESCE(SUM(motoPark), 0) AS sumMoto, " +
                "COALESCE(SUM(bikePark), 0) AS sumBike, " +
                "COALESCE(SUM(electricPark), 0) AS sumElectric " +
                "FROM bookings " +
                "WHERE beachId = ? AND date = ? AND status IN ('PENDING', 'CONFIRMED') " +
                "AND id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, beachId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setInt(3, excludeBookingId != null ? excludeBookingId : -1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookedParkingSpaces(
                            rs.getInt("sumAuto"),
                            rs.getInt("sumMoto"),
                            rs.getInt("sumBike"),
                            rs.getInt("sumElectric")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to check parking availability", e);
        }

        //paracadute di sicurezza se non trova nulla
        return new BookedParkingSpaces(0, 0, 0, 0);
    }

    /**
     * Estrae il numero di oggetti extra prenotati in un giorno specifico ad una determinata spiaggia
     * @param beachId ID della spiaggia
     * @param date Data da cercare
     * @param context Connessione JDBC
     * @return Il record di numeri di oggetti extra occupati in quella data
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    public BookedInventory getBookedInventory(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context) {
        if (beachId == null || date == null) throw new IllegalArgumentException("ERROR: the parameter(s) is/are not valid");
        if (excludeBookingId != null && excludeBookingId <= 0) throw new IllegalArgumentException("ERROR: the excludeBookingId parameter is not valid");

        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //sommo tutti gli extra prenotati in quella data e di quella spiaggia che hanno come stato booking PENDING o CONFIRMED
        //Nota: COALESCE sostituisce NULL con 0
        String sql = "SELECT " +
                "COALESCE(SUM(extraSdraio), 0) as sumSdraio, " +
                "COALESCE(SUM(extraLettini), 0) as sumLettini, " +
                "COALESCE(SUM(extraSedie), 0) as sumSedie, " +
                "COALESCE(SUM(camerini), 0) as sumCamerini " +
                "FROM bookings " +
                "WHERE beachId = ? AND date = ? AND status IN ('PENDING', 'CONFIRMED') " +
                "AND id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, beachId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setInt(3, excludeBookingId != null ? excludeBookingId : -1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookedInventory(
                            rs.getInt("sumSdraio"),
                            rs.getInt("sumLettini"),
                            rs.getInt("sumSedie"),
                            rs.getInt("sumCamerini")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to check extra items availability", e);
        }

        //paracadute di sicurezza se non trova nulla
        return new BookedInventory(0, 0, 0, 0);
    }
}