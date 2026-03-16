package com.example.s_balneare.infrastructure.persistence.jdbc.moderation;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.moderation.ReportRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Report tramite
 * libreria JDBC.
 *
 * @see ReportRepository ReportRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcReportRepository implements ReportRepository {
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
     * Salva un nuovo Report nel DB.
     *
     * @param report  Oggetto Report da salvare
     * @param context Connessione JDBC
     * @return ID generato dal Database del nuovo Report
     * @throws SQLException     se ci sono problemi col Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Integer save(Report report, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "INSERT INTO reports(reporterId, reportedId, reportedType, description, createdAt, status, bookingId) VALUES (?, ?, ?, ?, ?, ?, ?) ";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, report.getReporterId());
            statement.setInt(2, report.getReportedId());
            statement.setString(3, report.getReportedType().name());
            statement.setString(4, report.getDescription());
            statement.setTimestamp(5, java.sql.Timestamp.from(report.getCreatedAt()));
            statement.setString(6, report.getStatus().name());
            statement.setInt(7, report.getBookingId());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: SQL failed, no ID generated for report");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save reports", e);
        }
    }

    /**
     * Cerca un Report tramite ID nel DB.
     *
     * @param id      ID del Report da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se trovato il Report, può essere estratto l'oggetto Report; altri metodi altrimenti
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Report> findById(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: ID not valid");

        String sql = "SELECT id, reporterId, reportedId, reportedType, description, createdAt, status, bookingId FROM reports WHERE id = ?";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Estrae tutti i Report registrati nel DB (da usare solo per scopi di debugging).
     *
     * @param context Connessione JDBC
     * @return una lista di tutti i Report salvati nel Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Report> findAll(TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "SELECT id, reporterId, reportedId, reportedType, description, createdAt, status, bookingId FROM reports";
        List<Report> reports = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find reports", e);
        }
        return reports;
    }

    /**
     * Cerca i Report creati da un utente nel DB.
     *
     * @param reporterId ID del reporter da cercare
     * @param context    Connessione JDBC
     * @return lista di Report fatti da quell'utente
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Report> findByReporterId(Integer reporterId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (reporterId == null || reporterId <= 0) throw new IllegalArgumentException("ERROR: reporterId not valid");

        String sql = "SELECT id, reporterId, reportedId, reportedType, description, createdAt, status, bookingId FROM reports WHERE reporterId = ?";
        List<Report> reports = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reporterId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by reporterId", e);
        }
        return reports;
    }

    /**
     * Cerca i Report dove l'utente è stato segnalato nel DB.
     *
     * @param reportedId ID dell'utente segnalato da cercare
     * @param context    Connessione JDBC
     * @return lista di Report dove l'utente è stato segnalato
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Report> findByReportedId(Integer reportedId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (reportedId == null || reportedId <= 0) throw new IllegalArgumentException("ERROR: reportedId not valid");

        String sql = "SELECT id, reporterId, reportedId, reportedType, description, createdAt, status, bookingId FROM reports WHERE reportedId = ?";
        List<Report> reports = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reportedId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by reportedId", e);
        }
        return reports;
    }

    /**
     * Cerca i Report nel DB che hanno un determinato stato passato tramite parametro.
     *
     * @param status  Stato dei Report da cercare
     * @param context Connessione JDBC
     * @return lista di Report che hanno quello stato
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Report> findByStatus(ReportStatus status, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "SELECT id, reporterId, reportedId, reportedType, description, createdAt, status, bookingId FROM reports WHERE status = ?";
        List<Report> reports = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report in status " + status, e);
        }
        return reports;
    }

    /**
     * Aggiorna lo stato di un Report.
     *
     * @param report  Oggetto Report da aggiornare nel DB
     * @param context Connessione JDBC
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void updateStatus(Report report, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "UPDATE reports SET status=? WHERE id=?";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, report.getStatus().name());
            statement.setInt(2, report.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update status", e);
        }
    }

    /**
     * Metodo privato che prende l'oggetto ResultSet e restituisce un oggetto Report creato da esso.
     *
     * @param rs Oggetto contenente riga di una operazione SQL
     * @return oggetto Report creato
     * @throws SQLException se ci sono problemi col Database
     * @see ResultSet ResultSet
     */
    private Report mapToEntity(ResultSet rs) throws SQLException {
        return new Report(
                rs.getInt("id"),
                rs.getInt("reporterId"),
                rs.getInt("reportedId"),
                com.example.s_balneare.domain.moderation.ReportTargetType.valueOf(rs.getString("reportedType")),
                rs.getString("description"),
                rs.getTimestamp("createdAt").toInstant(),
                com.example.s_balneare.domain.moderation.ReportStatus.valueOf(rs.getString("status")),
                rs.getInt("bookingId")
        );
    }
}