package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.BookingRepository;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Booking tramite
 * libreria JDBC.
 * @see com.example.s_balneare.application.port.out.BookingRepository BookingRepository
 */
public class JdbcBookingRepository implements BookingRepository {
    private final DataSource dataSource;

    public JdbcBookingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Salva nuovo booking nel DB
     * @param booking Oggetto Booking da salvare
     * @return ID generato dal Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     * @throws SQLException se ci sono problemi col Database
     */
    @Override
    public Integer save(Booking booking) {
        //query
        String sql = "INSERT INTO bookings(beachId, customerId, date, extraSdraio, extraLettini, extraSedie, camerini, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try {
                //inizio "transaction"
                connection.setAutoCommit(false);
                int newId;

                //settaggio valori nella query + esecuzione query
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, booking.getBeachId());
                    statement.setInt(2, booking.getCustomerId());
                    statement.setDate(3, java.sql.Date.valueOf(booking.getDate()));
                    statement.setInt(4, booking.getExtraSdraio());
                    statement.setInt(5, booking.getExtraLettini());
                    statement.setInt(6, booking.getExtraSedie());
                    statement.setInt(7, booking.getCamerini());
                    statement.setString(8, booking.getStatus().toString());
                    statement.executeUpdate();

                    //prendo nuovo id generato dal DB
                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        if (rs.next()) newId = rs.getInt(1);
                        else throw new SQLException("ERROR: SQL FAILED, no ID generated");
                    }
                }

                //aggiungo spot del nuovo booking nel DB
                List<Integer> spotIds = booking.getSpotIds();
                sql = "INSERT INTO bookings_spots(booking, spot) VALUES (?, ?)";
                try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                    for (int spotId : spotIds) {
                        statement2.setInt(1, newId);
                        statement2.setInt(2, spotId);
                        statement2.addBatch();
                    }
                    statement2.executeBatch();
                }

                //fine transazione: posso salvare
                connection.commit();
                return newId;
            } catch (SQLException e) {
                //se succede un errore, provo a fare un rollback senza salvare nulla nel DB
                try {
                    connection.rollback();
                } catch (SQLException e2) {
                    e.addSuppressed(e2);
                }
                throw new RuntimeException("ERROR: unable to save booking", e);
            } finally {
                //in qualsiasi caso, rimetto i valori di default dell'autocommit
                try {
                    if (connection != null) connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("WARNING: unable to set autocommit to true on save booking");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    /**
     * Cancella booking dal DB
     * @param id ID del booking da cancellare
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void delete(Integer id) {
        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //query
        String sql = "DELETE FROM bookings WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to delete booking", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    /**
     * Aggiornamento booking nel DB
     * @param booking oggetto Booking da aggiornare nel DB
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void update(Booking booking) {
        //check validità ID
        if (booking.getId() == null || booking.getId() <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //query
        String sql = "UPDATE bookings SET extraSdraio = ?, extraLettini = ?, extraSedie = ?, camerini = ?, status = ? WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            //settaggio valori nella query + esecuzione query
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, booking.getExtraSdraio());
                statement.setInt(2, booking.getExtraLettini());
                statement.setInt(3, booking.getExtraSedie());
                statement.setInt(4, booking.getCamerini());
                statement.setString(5, booking.getStatus().name());
                statement.setInt(6, booking.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to update booking", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    /**
     * Trova booking dal DB da ID
     * @param id ID del Booking da cercare nel DB
     * @return oggetto Optional dal quale, se trovato lil booking, può essere estratto l'oggetto Booking; altri metodi altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Booking> findById(Integer id) {
        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //query
        String sql = "SELECT b.*, bs.spot FROM bookings b " +
                     "LEFT JOIN bookings_spots bs ON b.id = bs.booking " +
                     "WHERE b.id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
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

                    //inserisco tutti gli spot associati al booking
                    List<Integer> spotIds = new ArrayList<>();
                    int spotId;
                    do {
                        spotId = rs.getInt("spot");
                        if (!rs.wasNull()) {
                            spotIds.add(spotId);
                        }
                    } while (rs.next());

                    //costruisco il Booking
                    Booking booking = new Booking(id, beachId, customerId, date, spotIds, extraSdraio, extraLettini, extraSedie, camerini, status);

                    return Optional.of(booking);
                }
            } catch (SQLException e) {
                System.out.println("ERROR: SQL query not executed correctly");
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    //FIXME: probabile metodo da mettere in un successivo Use Case
    /**
     * Trova spot occupati per una data specifica
     * @param beachId ID della spiaggia
     * @param date Data da cercare
     * @return Una lista di ID di Spots occupati di quella spiaggia in quel giorno
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Integer> findOccupiedSpots(Integer beachId, LocalDate date) {
        //check validità parametri
        if (beachId == null || beachId <= 0 || date == null) throw new IllegalArgumentException("ERROR: the parameter(s) is/are not valid");

        //query
        String sql = "SELECT bs.spot FROM bookings b " +
                     "JOIN bookings_spots bs ON b.id = bs.booking " +
                     "WHERE b.beachId = ? AND b.date = ? " +
                     "AND b.status != 'CANCELLED' AND b.status != 'REJECTED'";
        List<Integer> occupiedSpots = new ArrayList<>();

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, beachId);
                statement.setDate(2, java.sql.Date.valueOf(date));
                statement.executeQuery();

                //aggiungo spot trovati occupati per la data x
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        occupiedSpots.add(rs.getInt("spot"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to find occupied spots", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }

        return occupiedSpots;
    }
}