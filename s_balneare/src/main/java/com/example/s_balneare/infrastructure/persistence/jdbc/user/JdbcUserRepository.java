package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcTransactionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JdbcUserRepository<T extends User> implements UserRepository<T> {
    protected final DataSource dataSource;

    protected JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //metodi astratti
    protected abstract void saveSpecificData(Connection conn, Integer newId, T user) throws SQLException;
    protected abstract void updateSpecificData(Connection conn, T user) throws SQLException;
    protected abstract T mapToEntity(ResultSet rs) throws SQLException;

    // METIODO HELPER: utilizzato per la gestione della connessione
    protected Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }

    @Override
    public Optional<String> findPassword(Integer id, TransactionContext context){
        String sql ="SELECT hashPassword FROM users WHERE id = ?";
        Connection conn = getConnection(context);
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
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

    @Override
    public Optional<String> findPassword(String identifier, TransactionContext context) {
        //interroga solo la tabella padre 'users'
        String sql = "SELECT hashPassword FROM users WHERE username = ? OR email = ?";
        Connection conn = getConnection(context);

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
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

    //implementazione comune di UpdatePassword
    @Override
    public void updatePassword(Integer id, String hashedPassword, TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "UPDATE users u SET u.hashPassword = ? WHERE u.id = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, hashedPassword);
            st.setInt(2, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update password", e);
        }
    }

    //Strumenti di logica comune per gli utenti, utili per evitare replicazione del codice
    @Override
    public Integer save(T user, String password, TransactionContext context) {
        Connection conn = getConnection(context);

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("ERROR: password must not be null");

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
                statement.setString(5, password);
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                    else throw new SQLException("ERROR: SQL FAILED, no ID generated for user");
                }
            }
            //non si necessita di un rollback perché necessita di un executeTransaction a causa del legame con address
            saveSpecificData(conn, newId, user);
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save user", e);
        }
    }

    @Override
    public void update(T user, TransactionContext context) {
        if (user.getId() == null || user.getId() <= 0)
            throw new IllegalArgumentException("ERROR: the parameter is not valid");
        Connection conn = getConnection(context);
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
            updateSpecificData(conn, user);
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                if (e.getMessage().contains("email")) {
                    throw new IllegalArgumentException("ERROR: Email is already in use by another account");
                } else if (e.getMessage().contains("username")) {
                    throw new IllegalArgumentException("ERROR: Username is already in use by another account");
                }
            }
            throw new RuntimeException("ERROR: unable to update user", e);
        }
    }

    //unico metodo per findAll e findBy
    protected List<T> executeFindQuery(String sql, TransactionContext context, Object... parameters) {
        List<T> list = new ArrayList<>();
        Connection conn = getConnection(context);

        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            //inserisco i parametri (se ce ne sono)
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

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