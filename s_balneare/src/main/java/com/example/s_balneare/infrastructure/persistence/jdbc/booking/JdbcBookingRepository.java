package com.example.s_balneare.infrastructure.persistence.jdbc.booking;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Booking tramite
 * libreria JDBC.
 *
 * @see BookingRepository BookingRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcBookingRepository implements BookingRepository {
    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext) e
     * lo converte di nuovo nella classe concreta per estrarre java.sql.Connection.
     *
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext(Connection connection))) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return connection;
    }

    /**
     * Salva nuovo booking nel DB.
     *
     * @param booking Oggetto Booking da salvare
     * @param context Connessione JDBC
     * @return ID generato dal Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     * @throws SQLException     se ci sono problemi col Database
     */
    @Override
    public Integer save(Booking booking, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //query
        String sql = "INSERT INTO bookings(beachId, customerId, callerName, callerPhone, date, extraSdraio, extraLettini, extraSedie, camerini, " +
                "autoPark, motoPark, electricPark, totalPrice, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            int newId;

            //settaggio valori nella query + esecuzione query
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, booking.getBeachId());
                statement.setObject(2, booking.getCustomerId(), Types.INTEGER);
                statement.setString(3, booking.getCallerName());
                statement.setString(4, booking.getCallerPhone());
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
                statement.setInt(10, parking != null ? parking.electricPark() : 0);

                statement.setDouble(11, booking.getTotalPrice());
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
     * Cancella booking dal DB.
     *
     * @param id      ID del booking da cancellare
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
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
     * Aggiornamento booking nel DB.
     *
     * @param booking oggetto Booking da aggiornare nel DB
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void update(Booking booking, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (booking.getId() == null || booking.getId() <= 0)
            throw new IllegalArgumentException("ERROR: the parameter is not valid");

        //passo 1: aggiorno tabella bookings
        String sql = "UPDATE bookings SET extraSdraio = ?, extraLettini = ?, extraSedie = ?, camerini = ?, " +
                "autoPark = ?, motoPark = ?, electricPark = ?, totalPrice = ?, status = ? WHERE id = ?";

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
            statement.setInt(7, parking != null ? parking.electricPark() : 0);

            //aggiunta prezzo totale e status
            statement.setDouble(8, booking.getTotalPrice());
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
     * Trova booking dal DB da ID.
     *
     * @param id      ID del Booking da cercare nel DB
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se trovato lil booking, può essere estratto l'oggetto Booking; altri metodi altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
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
                Integer customerId = rs.getObject("customerId") != null ? rs.getInt("customerId") : null;
                String callerName = rs.getString("callerName");
                String callerPhone = rs.getString("callerPhone");
                LocalDate date = rs.getDate("date").toLocalDate();
                int extraSdraio = rs.getInt("extraSdraio");
                int extraLettini = rs.getInt("extraLettini");
                int extraSedie = rs.getInt("extraSedie");
                int camerini = rs.getInt("camerini");
                double totalPrice = rs.getDouble("totalPrice");
                BookingStatus status = BookingStatus.valueOf(rs.getString("status"));

                //dati parcheggio
                BookingParking parking = new BookingParking(
                        rs.getInt("autoPark"),
                        rs.getInt("motoPark"),
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
                        callerName,
                        callerPhone,
                        date,
                        spotIds,
                        extraSdraio,
                        extraLettini,
                        extraSedie,
                        camerini,
                        parking,
                        totalPrice,
                        status);

                return Optional.of(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: SQL query not executed correctly for booking " + id, e);
        }
    }

    /**
     * Trova spot occupati per una data specifica.<br>
     * Con excludeBookingId, vado a levare i posti tecnicamente occupati del Booking che sto modificando.
     *
     * @param beachId          ID della spiaggia
     * @param date             Data da cercare
     * @param excludeBookingId ID del Booking da escludere dalla ricerca
     * @param context          Connessione JDBC
     * @return una lista di ID di Spots occupati di quella spiaggia in quel giorno
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Integer> findOccupiedSpots(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0 || date == null)
            throw new IllegalArgumentException("ERROR: the parameter(s) is/are not valid");
        if (excludeBookingId != null && excludeBookingId <= 0)
            throw new IllegalArgumentException("ERROR: the excludeBookingId parameter is not valid");

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

    /**
     * Trova tutte le prenotazioni fatte da un customer online.
     *
     * @param customerId ID del customer da cercare
     * @param context    Connessione JDBC
     * @return lista di tutti i Booking fatti dal customer in questione
     * @throws IllegalArgumentException se il customer non è valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Booking> findByCustomerId(Integer customerId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (customerId == null || customerId <= 0) throw new IllegalArgumentException("ERROR: invalid customerId");

        String sql = "SELECT b.*, bs.spotId FROM bookings b " +
                "LEFT JOIN booking_spots bs ON b.id = bs.bookingId " +
                "WHERE b.customerId = ? " +
                "ORDER BY b.date DESC";

        //map per raggruppare gli spot in un'unica prenotazione
        Map<Integer, Booking> bookingMap = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);

            //praticamente in questa query ho booking ripetuti con vari spots
            //booking 1 - spot 2
            //booking 1 - spot 3
            //booking 2 - spot 5 ...
            //l'obiettivo è quello di raggruppare tutti gli spots nei vari bookings
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    //se non ho ancora creato l'oggetto Booking per questo ID, lo creo
                    if (!bookingMap.containsKey(id)) {
                        int beachId = rs.getInt("beachId");
                        LocalDate date = rs.getDate("date").toLocalDate();
                        int extraSdraio = rs.getInt("extraSdraio");
                        int extraLettini = rs.getInt("extraLettini");
                        int extraSedie = rs.getInt("extraSedie");
                        int camerini = rs.getInt("camerini");
                        double totalPrice = rs.getDouble("totalPrice");
                        BookingStatus status = BookingStatus.valueOf(rs.getString("status"));

                        BookingParking parking = new BookingParking(
                                rs.getInt("autoPark"), rs.getInt("motoPark"),
                                rs.getInt("electricPark")
                        );

                        Booking booking = new Booking(id, beachId, customerId, null, null, date, new ArrayList<>(),
                                extraSdraio, extraLettini, extraSedie, camerini, parking, totalPrice, status);
                        bookingMap.put(id, booking);
                    }

                    //aggiungo lo spot alla lista della prenotazione corrispondente
                    int spotId = rs.getInt("spotId");
                    if (!rs.wasNull()) {
                        bookingMap.get(id).getSpotIds().add(spotId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to fetch customer bookings", e);
        }

        //salvo tutto nella nuova lista, raggruppando bookings e spots
        return new ArrayList<>(bookingMap.values());
    }

    /**
     * Trova tutte le prenotazioni fatte per una determinata spiaggia.
     *
     * @param beachId ID della spiaggia da cercare
     * @param context Connessione JDBC
     * @return lista di Bookings fatte in quella spiaggia
     * @throws IllegalArgumentException se il beachId non è valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Booking> findByBeachId(Integer beachId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");

        String sql = "SELECT b.*, bs.spotId FROM bookings b " +
                "LEFT JOIN booking_spots bs ON b.id = bs.bookingId " +
                "WHERE b.beachId = ? ORDER BY b.date DESC";

        //map per raggruppare gli spot in un'unica prenotazione
        java.util.Map<Integer, Booking> bookingMap = new java.util.LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, beachId);

            //praticamente in questa query ho booking ripetuti con vari spots
            //booking 1 - spot 2
            //booking 1 - spot 3
            //booking 2 - spot 5 ...
            //l'obiettivo è quello di raggruppare tutti gli spots nei vari bookings
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    //se non ho ancora creato l'oggetto Booking per questo ID, lo creo
                    if (!bookingMap.containsKey(id)) {
                        Integer customerId = rs.getObject("customerId") != null ? rs.getInt("customerId") : null;

                        BookingParking parking = new BookingParking(
                                rs.getInt("autoPark"), rs.getInt("motoPark"),
                                rs.getInt("electricPark")
                        );

                        Booking booking = new Booking(
                                id, rs.getInt("beachId"), customerId,
                                rs.getString("callerName"), rs.getString("callerPhone"),
                                rs.getDate("date").toLocalDate(), new ArrayList<>(),
                                rs.getInt("extraSdraio"), rs.getInt("extraLettini"),
                                rs.getInt("extraSedie"), rs.getInt("camerini"),
                                parking, rs.getDouble("totalPrice"),
                                BookingStatus.valueOf(rs.getString("status"))
                        );
                        bookingMap.put(id, booking);
                    }

                    //aggiungo lo spot alla lista della prenotazione corrispondente
                    int spotId = rs.getInt("spotId");
                    if (!rs.wasNull()) {
                        bookingMap.get(id).getSpotIds().add(spotId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to fetch beach bookings", e);
        }

        //salvo tutto nella nuova lista, raggruppando bookings e spots
        return new ArrayList<>(bookingMap.values());
    }

    /**
     * Controlla se l'utente in questione ha avuto prenotazioni passate in stato CONFIRMED ad una determinata Beach.
     *
     * @param customerId    ID del customer
     * @param beachId       ID della spiaggia
     * @param referenceDate Data di riferimento
     * @param context       Connessione JDBC
     * @return TRUE se ci sono Booking passati con stato CONFIRMED a quella spiaggia; FALSE altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public boolean hasPastConfirmedBooking(Integer customerId, Integer beachId, LocalDate referenceDate, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check validità parametri
        if (customerId == null || customerId <= 0 || beachId == null || beachId <= 0)
            throw new IllegalArgumentException("ERROR: invalid customerId and/or beachId");
        if (referenceDate == null) throw new IllegalArgumentException("ERROR: invalid referenceDate");

        String sql = "SELECT 1 FROM bookings WHERE customerId = ? AND beachId = ? AND status = 'CONFIRMED' AND date < ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, beachId);
            ps.setDate(3, java.sql.Date.valueOf(referenceDate));
            try (ResultSet rs = ps.executeQuery()) {
                //TRUE se la query mi restituisce almeno una riga, FALSE altrimenti
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to check past bookings", e);
        }
    }

    /**
     * Cerca se ci sono prenotazioni registrate per una stagione.
     *
     * @param beachId     ID della spiaggia
     * @param seasonStart Data di inizio stagione
     * @param seasonEnd   Data di fine stagione
     * @param context     Connessione JDBC
     * @return TRUE se ci sono prenotazioni registrate per quella stagione, FALSE altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public boolean hasBookingsForSeason(Integer beachId, LocalDate seasonStart, LocalDate seasonEnd, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (seasonStart == null || seasonEnd == null)
            throw new IllegalArgumentException("ERROR: invalid seasonStart and/or seasonEnd");
        if (seasonStart.isAfter(seasonEnd))
            throw new IllegalArgumentException("ERROR: seasonStart must be < seasonEnd");

        //SELECT 1 prende il primo booking trovato per quella stagione
        String sql = "SELECT 1 FROM bookings WHERE beachId = ? AND date >= ? AND date <= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setDate(2, java.sql.Date.valueOf(seasonStart));
            ps.setDate(3, java.sql.Date.valueOf(seasonEnd));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to check bookings for season", e);
        }
    }

    /**
     * Cancella tutte le prenotazioni future di un utente (usato in caso di chiusura/ban account).
     *
     * @param customerId    ID del customer
     * @param referenceDate Data di riferimento (data di chiusura/ban account di solito)
     * @param context       Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void cancelFutureBookingsForCustomer(Integer customerId, LocalDate referenceDate, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (customerId == null || customerId <= 0) throw new IllegalArgumentException("ERROR: invalid customerId");
        if (referenceDate == null) throw new IllegalArgumentException("ERROR: invalid referenceDate");

        String sql = "UPDATE bookings SET status = 'CANCELLED' " +
                "WHERE customerId = ? AND date > ? AND status IN ('PENDING', 'CONFIRMED')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setDate(2, java.sql.Date.valueOf(referenceDate));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to cancel future bookings for customer", e);
        }
    }

    /**
     * Cancella tutte le prenotazioni future di una spiaggia (usato in caso di chiusura/ban spiaggia).
     *
     * @param beachId       ID della spiaggia
     * @param referenceDate Data di riferimento (data di chiusura/ban spiaggia di solito)
     * @param context       Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void cancelFutureBookingsForBeach(Integer beachId, LocalDate referenceDate, TransactionContext context) {
        Connection connection = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (referenceDate == null) throw new IllegalArgumentException("ERROR: invalid referenceDate");

        String sql = "UPDATE bookings SET status = 'CANCELLED' " +
                "WHERE beachId = ? AND date > ? AND status IN ('PENDING', 'CONFIRMED')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setDate(2, java.sql.Date.valueOf(referenceDate));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to cancel future bookings for beach", e);
        }
    }

    /**
     * Cancella tutte le prenotazioni future di un utente da una determinata spiaggia (usato nel caso in cui l'utente
     * viene bannato dalla spiaggia stessa).
     *
     * @param customerId    ID del customer
     * @param beachId       ID della spiaggia
     * @param referenceDate Data di riferimento (data odierna se ban istantaneo)
     * @param context       Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void cancelFutureUserBookingsFromBeach(Integer customerId, Integer beachId, LocalDate referenceDate, TransactionContext context) {
        Connection connection = getConnection(context);

        //check validità parametri
        if (customerId == null || customerId <= 0) throw new IllegalArgumentException("ERROR: invalid customerId");
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (referenceDate == null) throw new IllegalArgumentException("ERROR: invalid referenceDate");

        String sql = "UPDATE bookings SET status = 'CANCELLED'" +
                "WHERE customerId=? AND beachId= ? AND date > ? AND status IN ('PENDING', 'CONFIRMED')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, beachId);
            ps.setDate(3, java.sql.Date.valueOf(referenceDate));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to cancel future bookings for beach of user: " + customerId, e);
        }
    }

    /**
     * Trova il numero massimo di prenotazioni di posti auto per giorno.
     *
     * @param beachId       ID della spiaggia
     * @param referenceDate Data di riferimento
     * @param context       Connessione JDBC
     * @return un record del più grande numero di parcheggi prenotati di un giorno futuro a referenceDate
     */
    @Override
    public BookedParkingSpaces getMaxFutureParkings(Integer beachId, LocalDate referenceDate, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check integrità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (referenceDate == null) throw new IllegalArgumentException("ERROR: invalid referenceDate");

        //query "divertente" dove all'inizio cerco i posti prenotati per giorno e dopo seleziono i numeri più grandi
        String sql = "SELECT " +
                "COALESCE(MAX(dailyAuto), 0) as maxAuto, " +
                "COALESCE(MAX(dailyMoto), 0) as maxMoto, " +
                "COALESCE(MAX(dailyElec), 0) as maxElec " +
                "FROM (" +
                "SELECT date, " +
                "SUM(autoPark) as dailyAuto, " +
                "SUM(motoPark) as dailyMoto, " +
                "SUM(electricPark) as dailyElec " +
                "FROM bookings " +
                "WHERE beachId = ? AND date >= ? AND status IN ('PENDING', 'CONFIRMED') " +
                "GROUP BY date" +
                ") as DailyTotals";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setDate(2, java.sql.Date.valueOf(referenceDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookedParkingSpaces(
                            rs.getInt("maxAuto"), rs.getInt("maxMoto"),
                            rs.getInt("maxElec")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to check max future parkings", e);
        }
        //in caso di nessun posto prenotato, passo un record di default
        return new BookedParkingSpaces(0, 0, 0);
    }

    /**
     * Trova il numero massimo di prenotazioni di oggetti extra per giorno.
     *
     * @param beachId       ID della spiaggia
     * @param referenceDate Data di riferimento
     * @param context       Connessione JDBC
     * @return un record del più grande numero di oggetti extra prenotati di un giorno futuro a referenceDate
     */
    @Override
    public BookedInventory getMaxFutureInventory(Integer beachId, LocalDate referenceDate, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check integrità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (referenceDate == null) throw new IllegalArgumentException("ERROR: invalid referenceDate");

        //query "divertente" dove all'inizio cerco gli oggetti extra prenotati per giorno e dopo seleziono i numeri più grandi
        String sql = "SELECT " +
                "COALESCE(MAX(dailySdraio), 0) as maxSdraio, " +
                "COALESCE(MAX(dailyLettini), 0) as maxLettini, " +
                "COALESCE(MAX(dailySedie), 0) as maxSedie, " +
                "COALESCE(MAX(dailyCamerini), 0) as maxCamerini " +
                "FROM (" +
                "SELECT date, " +
                "SUM(extraSdraio) as dailySdraio, " +
                "SUM(extraLettini) as dailyLettini, " +
                "SUM(extraSedie) as dailySedie, " +
                "SUM(camerini) as dailyCamerini " +
                "FROM bookings " +
                "WHERE beachId = ? AND date >= ? AND status IN ('PENDING', 'CONFIRMED') " +
                "GROUP BY date" +
                ") as DailyTotals";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beachId);
            ps.setDate(2, java.sql.Date.valueOf(referenceDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookedInventory(
                            rs.getInt("maxSdraio"), rs.getInt("maxLettini"),
                            rs.getInt("maxSedie"), rs.getInt("maxCamerini")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to check max future inventory", e);
        }
        //in caso di nessun extra prenotato, passo un record di default
        return new BookedInventory(0, 0, 0, 0);
    }
}