package com.example.s_balneare.infrastructure.persistence.jdbc.booking;

import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcTransactionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Booking tramite
 * libreria JDBC.
 * @see BookingRepository BookingRepository
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcBookingRepository implements BookingRepository {
    private final DataSource dataSource;

    public JdbcBookingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
     * Salva nuovo booking nel DB
     * @param booking Oggetto Booking da salvare
     * @param context Connessione JDBC
     * @return ID generato dal Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     * @throws SQLException se ci sono problemi col Database
     */
    @Override
    public Integer save(Booking booking, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //query
        String sql = "INSERT INTO bookings(beachId, customerId, date, extraSdraio, extraLettini, extraSedie, camerini, " +
                "autoPark, motoPark, bikePark, electricPark, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            int newId;

            //settaggio valori nella query + esecuzione query
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, booking.getBeachId());
                statement.setInt(2, booking.getCustomerId());
                statement.setDate(3, java.sql.Date.valueOf(booking.getDate()));

                //estrazione dati extra
                statement.setInt(4, booking.getExtraSdraio());
                statement.setInt(5, booking.getExtraLettini());
                statement.setInt(6, booking.getExtraSedie());
                statement.setInt(7, booking.getCamerini());

                //estrazione dati parcheggio dal record
                BookingParking parking = booking.getParking();
                statement.setInt(8, parking != null ? parking.autoPark() : 0);
                statement.setInt(9, parking != null ? parking.motoPark() : 0);
                statement.setInt(10, parking != null ? parking.bikePark() : 0);
                statement.setInt(11, parking != null ? parking.electricPark() : 0);

                statement.setString(12, booking.getStatus().name());
                statement.executeUpdate();

                //prendo nuovo id generato dal DB
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                    else throw new SQLException("ERROR: SQL FAILED, no ID generated");
                }
            }

            //aggiungo spot del nuovo booking nel DB
            List<Integer> spotIds = booking.getSpotIds();
            sql = "INSERT INTO booking_spots(bookingId, date, spotId) VALUES (?, ?, ?)";
            try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                for (int spotId : spotIds) {
                    statement2.setInt(1, newId);
                    statement2.setDate(2, java.sql.Date.valueOf(booking.getDate()));
                    statement2.setInt(3, spotId);
                    statement2.addBatch();
                }
                statement2.executeBatch();
            }

            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    /**
     * Cancella booking dal DB
     * @param id ID del booking da cancellare
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void delete(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //query
        String sqlSpots = "DELETE FROM booking_spots WHERE bookingId = ?";
        String sqlBooking = "DELETE FROM bookings WHERE id = ?";

        try (PreparedStatement stSpots = connection.prepareStatement(sqlSpots);
             PreparedStatement stBooking = connection.prepareStatement(sqlBooking)) {
            stSpots.setInt(1, id);
            stSpots.executeUpdate();

            stBooking.setInt(1, id);
            stBooking.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete booking", e);
        }
    }

    /**
     * Aggiornamento booking nel DB
     * @param booking oggetto Booking da aggiornare nel DB
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void update(Booking booking, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (booking.getId() == null || booking.getId() <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //passo 1: aggiorno tabella bookings
        String sql = "UPDATE bookings SET extraSdraio = ?, extraLettini = ?, extraSedie = ?, camerini = ?, " +
                "autoPark = ?, motoPark = ?, bikePark = ?, electricPark = ?, status = ? WHERE id = ?";

        //settaggio valori nella query + esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            //aggiunta extra accessori
            statement.setInt(1, booking.getExtraSdraio());
            statement.setInt(2, booking.getExtraLettini());
            statement.setInt(3, booking.getExtraSedie());
            statement.setInt(4, booking.getCamerini());

            //aggiunta parcheggi
            BookingParking parking = booking.getParking();
            statement.setInt(5, parking != null ? parking.autoPark() : 0);
            statement.setInt(6, parking != null ? parking.motoPark() : 0);
            statement.setInt(7, parking != null ? parking.bikePark() : 0);
            statement.setInt(8, parking != null ? parking.electricPark() : 0);

            //aggiunta status
            statement.setString(9, booking.getStatus().name());
            statement.setInt(10, booking.getId());
            statement.executeUpdate();

            //passo 2: aggiorno tabella booking_spots (cancello i vecchi spots, inserisco i nuovi spots)
            String deleteSpots = "DELETE FROM booking_spots WHERE bookingId = ?";
            try (PreparedStatement delSt = connection.prepareStatement(deleteSpots)) {
                delSt.setInt(1, booking.getId());
                delSt.executeUpdate();
            }

            String insertSpots = "INSERT INTO booking_spots(bookingId, date, spotId) VALUES (?, ?, ?)";
            try (PreparedStatement insSt = connection.prepareStatement(insertSpots)) {
                for (int spotId : booking.getSpotIds()) {
                    insSt.setInt(1, booking.getId());
                    insSt.setDate(2, java.sql.Date.valueOf(booking.getDate()));
                    insSt.setInt(3, spotId);
                    insSt.addBatch();
                }
                insSt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update booking", e);
        }
    }

    /**
     * Trova booking dal DB da ID
     * @param id ID del Booking da cercare nel DB
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se trovato lil booking, può essere estratto l'oggetto Booking; altri metodi altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Booking> findById(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //query
        String sql = "SELECT b.*, bs.spotId FROM bookings b " +
                     "LEFT JOIN booking_spots bs ON b.id = bs.bookingId " +
                     "WHERE b.id = ?";

        //inserisco ID ed eseguo
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeQuery();

            try (ResultSet rs = statement.getResultSet()) {
                //controllo se ho trovato qualcosa
                if (!rs.next()) {
                    return Optional.empty();
                }

                //ricavo la prima riga ritornata dal SELECT
                int beachId = rs.getInt("beachId");
                int customerId = rs.getInt("customerId");
                LocalDate date = rs.getDate("date").toLocalDate();
                int extraSdraio = rs.getInt("extraSdraio");
                int extraLettini = rs.getInt("extraLettini");
                int extraSedie = rs.getInt("extraSedie");
                int camerini = rs.getInt("camerini");
                BookingStatus status = BookingStatus.valueOf(rs.getString("status"));

                //dati parcheggio
                BookingParking parking = new BookingParking(
                        rs.getInt("autoPark"),
                        rs.getInt("motoPark"),
                        rs.getInt("bikePark"),
                        rs.getInt("electricPark")
                );

                //inserisco tutti gli spot associati al booking
                List<Integer> spotIds = new ArrayList<>();
                int spotId;
                do {
                    spotId = rs.getInt("spotId");
                    if (!rs.wasNull()) {
                        spotIds.add(spotId);
                    }
                } while (rs.next());

                //costruisco il Booking
                Booking booking = new Booking(id,
                        beachId,
                        customerId,
                        date,
                        spotIds,
                        extraSdraio,
                        extraLettini,
                        extraSedie,
                        camerini,
                        parking,
                        status);

                return Optional.of(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: SQL query not executed correctly for booking " + id, e);
        }
    }

    /**
     * Trova spot occupati per una data specifica
     * Con excludeBookingId, vado a levare i posti tecnicamente occupati del Booking che sto modificando
     * @param beachId ID della spiaggia
     * @param date Data da cercare
     * @param excludeBookingId ID del Booking da escludere dalla ricerca
     * @param context Connessione JDBC
     * @return Una lista di ID di Spots occupati di quella spiaggia in quel giorno
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Integer> findOccupiedSpots(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0 || date == null) throw new IllegalArgumentException("ERROR: the parameter(s) is/are not valid");
        if (excludeBookingId != null && excludeBookingId <= 0) throw new IllegalArgumentException("ERROR: the excludeBookingId parameter is not valid");

        //query
        String sql = "SELECT bs.spotId FROM bookings b " +
                     "JOIN booking_spots bs ON b.id = bs.bookingId " +
                     "WHERE b.beachId = ? AND b.date = ? " +
                     "AND b.status != 'CANCELLED' AND b.status != 'REJECTED' " +
                     "AND b.id != ?";
        List<Integer> occupiedSpots = new ArrayList<>();


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, beachId);
            statement.setDate(2, java.sql.Date.valueOf(date));
            statement.setInt(3, excludeBookingId != null ? excludeBookingId : -1);

            //aggiungo spot trovati occupati per la data x
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    occupiedSpots.add(rs.getInt("spotId"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find occupied spots", e);
        }

        return occupiedSpots;
    }
}