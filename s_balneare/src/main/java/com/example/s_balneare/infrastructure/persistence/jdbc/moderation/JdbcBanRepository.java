package com.example.s_balneare.infrastructure.persistence.jdbc.moderation;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Ban;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Ban tramite
 * libreria JDBC.
 *
 * @see BanRepository BanRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcBanRepository implements BanRepository {
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
     * Salva un nuovo Ban nel DB.
     *
     * @param ban     Oggetto Ban da salvare
     * @param context Connessione JDBC
     * @return ID generato dal Database del nuovo Ban
     * @throws SQLException     se ci sono problemi col Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Integer save(Ban ban, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "INSERT INTO bans (bannedId, banType, bannedFromBeachId, adminId, reason, createdAt) VALUES (?, ?, ?, ?, ?, ?) ";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, ban.bannedId());
            statement.setString(2, ban.banType().name());
            statement.setInt(3, ban.bannedFromBeachId());
            statement.setInt(4, ban.adminId());
            statement.setString(5, ban.reason());
            statement.setTimestamp(6, java.sql.Timestamp.from(ban.createdAt()));
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: SQL failed, no ID generated for ban");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save ban", e);
        }
    }

    /**
     * Cerca un Ban tramite ID nel DB.
     *
     * @param id      ID del Ban da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se trovato il Ban, può essere estratto l'oggetto Ban; altri metodi altrimenti
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Ban> findById(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: ID not valid");

        String sql = "SELECT id, bannedId, banType, bannedFromBeachId, adminId, reason, createdAt FROM bans WHERE id = ?";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find ban by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Estrae tutti i Ban registrati nel DB (da usare solo per scopi di debugging).
     *
     * @param context Connessione JDBC
     * @return una lista di tutti i Ban salvati nel Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Ban> findAll(TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "SELECT id, bannedId, banType, bannedFromBeachId, adminId, reason, createdAt FROM bans";
        List<Ban> bans = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    bans.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find bans", e);
        }
        return bans;
    }

    /**
     * Verifica se un Customer ha un Ban attivo sulla spiaggia.
     *
     * @param customerId ID del Customer
     * @param beachId    ID della spiaggia
     * @param context    Connessione JDBC
     * @return TRUE se il Customer ha un Ban attivo su quella spiaggia; FALSE altrimenti
     * @throws IllegalArgumentException se ID passati non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public boolean isBannedFromBeach(Integer customerId, Integer beachId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (customerId == null || customerId <= 0) throw new IllegalArgumentException("ERROR: invalid customerId");
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");

        String sql = "SELECT * FROM bans WHERE bannedId = ? AND bannedFromBeachId = ? AND banType = 'BEACH' LIMIT 1";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, beachId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find bans for this user", e);
        }
    }

    /**
     * Verifica se un utente ha un Ban attivo a livello applicazione.
     *
     * @param userId  ID dell'utente
     * @param context Connessione JDBC
     * @return TRUE se l'utente ha un Ban attivo a livello applicazione; FALSE altrimenti
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public boolean isBannedFromApp(Integer userId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (userId == null || userId <= 0) throw new IllegalArgumentException("ERROR: invalid userId");

        String sql = "SELECT * FROM bans WHERE bannedId = ? AND banType = 'APPLICATION' LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find bans for this user", e);
        }
    }

    /**
     * Metodo privato che prende l'oggetto ResultSet e restituisce un oggetto Ban creato da esso.
     *
     * @param rs Oggetto contenente riga di una operazione SQL
     * @return oggetto Ban creato
     * @throws SQLException se ci sono problemi col Database
     * @see ResultSet ResultSet
     */
    private Ban mapToEntity(ResultSet rs) throws SQLException {
        return new Ban(
                rs.getInt("id"),
                rs.getInt("bannedId"),
                com.example.s_balneare.domain.moderation.BanType.valueOf(rs.getString("banType")),
                rs.getInt("bannedFromBeachId"),
                rs.getInt("adminId"),
                rs.getString("reason"),
                rs.getTimestamp("time").toInstant()
        );
    }
}