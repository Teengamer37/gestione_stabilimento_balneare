package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Repository che implementa i metodi comuni che permettono di interagire con un Database su oggetti di tipo User tramite
 * libreria JDBC.<br>
 * Estesa nelle classi: JdbcCustomerRepository, JdbcOwnerRepository, JdbcAdminRepository.
 *
 * @see UserRepository UserRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public abstract class JdbcUserRepository<T extends User> implements UserRepository<T> {
    //pattern per verificare se maneggio password crittografate prima di manipolarle nel database
    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("^\\$2[abyx]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    //metodi astratti
    protected abstract void saveSpecificData(Connection conn, Integer newId, T user) throws SQLException;
    protected abstract void updateSpecificData(Connection conn, T user) throws SQLException;
    protected abstract T mapToEntity(ResultSet rs) throws SQLException;

    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext) e
     * lo converte di nuovo nella classe concreta per estrarre java.sql.Connection.
     *
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    protected Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext(Connection connection))) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return connection;
    }

    /**
     * Cerca la password di un utente nel DB tramite ID (restituendola crittografata).
     *
     * @param id      ID dell'utente da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional da dove, se utente trovato, si può estrarre la stringa hashedPassword
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<String> findPassword(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: ID not valid");

        String sql = "SELECT hashPassword FROM users WHERE id = ?";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("hashPassword"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find password", e);
        }
        return Optional.empty();
    }

    /**
     * Cerca la password di un utente nel DB tramite username o email (restituendola crittografata).
     *
     * @param identifier Username/email dell'utente da cercare
     * @param context    Connessione JDBC
     * @return oggetto Optional da dove, se utente trovato, si può estrarre la stringa hashedPassword
     * @throws IllegalArgumentException se parametro passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<String> findPassword(String identifier, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (identifier == null || identifier.isBlank()) throw new IllegalArgumentException("ERROR: username or email not valid");

        String sql = "SELECT hashPassword FROM users WHERE username = ? OR email = ?";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("hashPassword"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find password", e);
        }
        return Optional.empty();
    }

    /**
     * Aggiorna la password di un utente.
     *
     * @param id             ID dell'utente richiedente l'aggiornamento
     * @param hashedPassword Nuova password già crittografata da salvare
     * @param context        Connessione JDBC
     * @throws IllegalArgumentException se parametri passati non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void updatePassword(Integer id, String hashedPassword, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: ID not valid");
        if (hashedPassword == null || !BCRYPT_PATTERN.matcher(hashedPassword).matches())
            throw new IllegalArgumentException("ERROR: new password not valid");

        String sql = "UPDATE users u SET u.hashPassword = ? WHERE u.id = ?";

        //query
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, hashedPassword);
            st.setInt(2, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update password", e);
        }
    }

    /**
     * Salva nuovo utente nel Database.
     *
     * @param user           Oggetto User da salvare
     * @param hashedPassword Nuova password già crittografata
     * @param context        Connessione JDBC
     * @return ID generato dal Database del nuovo utente
     * @throws IllegalArgumentException se password passata non valida
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public Integer save(T user, String hashedPassword, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check validità password
        if (hashedPassword == null || !BCRYPT_PATTERN.matcher(hashedPassword).matches())
            throw new IllegalArgumentException("ERROR: password not valid");

        //start con le query varie
        try {
            String sqlUser = "INSERT INTO users(name, surname, username, email, hashPassword) " +
                    "VALUES(?, ?, ?, ?, ?)";
            int newId;

            //inserisco prima il record in app_users
            try (PreparedStatement statement = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getSurname());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getEmail());
                statement.setString(5, hashedPassword);
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                    else throw new SQLException("ERROR: SQL FAILED, no ID generated for user");
                }
            }

            //chiamo metodo specializzato per l'utente effettivamente creato
            saveSpecificData(conn, newId, user);
            return newId;
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                if (e.getMessage().contains("email")) {
                    throw new IllegalArgumentException("ERROR: email is already in use by another account");
                } else if (e.getMessage().contains("username")) {
                    throw new IllegalArgumentException("ERROR: username is already in use by another account");
                }
            }
            throw new RuntimeException("ERROR: unable to save user", e);
        }
    }

    /**
     * Aggiorna utente nel Database
     *
     * @param user    Utente da aggiornare
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ID utente non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void update(T user, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check validità ID
        if (user.getId() == null || user.getId() <= 0)
            throw new IllegalArgumentException("ERROR: userId is not valid");

        //start con le query varie
        try {
            String sqlUser = "UPDATE users SET name = ?, surname = ?, username = ?, email = ? WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sqlUser)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getSurname());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getEmail());
                statement.setInt(5, user.getId());
                statement.executeUpdate();
            }

            //chiamo metodo specializzato per lo specifico utente che vado ad aggiornare
            updateSpecificData(conn, user);
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                if (e.getMessage().contains("email")) {
                    throw new IllegalArgumentException("ERROR: email is already in use by another account");
                } else if (e.getMessage().contains("username")) {
                    throw new IllegalArgumentException("ERROR: username is already in use by another account");
                }
            }
            throw new RuntimeException("ERROR: unable to update user", e);
        }
    }

    /**
     * Metodo che esegue le varie query relative alla ricerca.
     *
     * @param sql        Query SQL da lanciare
     * @param context    Connessione JDBC
     * @param parameters Serie di parametri da sostituire nei '?' della stringa SQL
     * @return tutti i ResultSet generati dalla query
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    protected List<T> executeFindQuery(String sql, TransactionContext context, Object... parameters) {
        List<T> list = new ArrayList<>();

        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            //inserisco i parametri (se ce ne sono)
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            //avvio query e salvo i ResultSet
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to execute query", e);
        }
        return list;
    }
}