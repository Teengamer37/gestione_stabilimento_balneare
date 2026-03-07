package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.CustomerUserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

//FIXME: un macello qui

public class JdbcCustomerUserRepository implements CustomerUserRepository {
    private final DataSource dataSource;

    public JdbcCustomerUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //---- METODO HELPER ----
    //prende il token vuoto (TransactionContext)
    //lo converte di nuovo nella classe concreta per estrarre java.sql.Connection
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }

    @Override
    public Integer save(CustomerUser user, String password, TransactionContext context) {
        Connection conn = getConnection(context);

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("ERROR: password must not be null");

        String sqlUser = "INSERT INTO app_users(name, surname, username, email, hashPassword) " +
                "VALUES(?, ?, ?, ?, ?)";
        String sqlCustomer = "INSERT INTO customers(id,phoneNumber, addressId, active) VALUES(?,?,?,?)";

        try {
            int newId;
            //Inserisco prima il record in app_users
            try (PreparedStatement statement = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getSurname());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getEmail());
                statement.setString(5, password);
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                    else throw new SQLException("ERROR: SQL FAILED, no ID generated for customer");
                }
            }
            //Non si necessita di un rollback perchè necessita di un executeTransaction a causa del legame con address
            try (PreparedStatement statement = conn.prepareStatement(sqlCustomer)) {
                statement.setInt(1, newId);
                statement.setString(2, user.getPhoneNumber());
                statement.setInt(3, user.getAddressId());
                statement.setBoolean(4, user.isActive());
                statement.executeUpdate();
            }
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save customer", e);
        }
    }

    @Override
    public void delete(Integer id, TransactionContext context) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        Connection conn = getConnection(context);
        String sqlUser = "DELETE FROM app_users WHERE id = ?";
        String sqlCustomer = "DELETE FROM customers WHERE id = ?";

        try {
            try (PreparedStatement statement = conn.prepareStatement(sqlCustomer)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = conn.prepareStatement(sqlUser)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete customer", e);
        }
    }

    @Override
    public void update(CustomerUser user, TransactionContext context) {
        if (user.getId() == null || user.getId() <= 0)
            throw new IllegalArgumentException("ERROR: the parameter is not valid");

        Connection conn = getConnection(context);
        String sqlUser = "UPDATE app_users SET name = ?, surname = ?, username = ?, email = ? WHERE id = ?";
        String sqlCustomer = "UPDATE customers SET phoneNumber = ?, addressId = ?, active = ? WHERE id = ?";

        try {
            try (PreparedStatement statement = conn.prepareStatement(sqlUser)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getSurname());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getEmail());
                statement.setInt(5, user.getId());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = conn.prepareStatement(sqlCustomer)) {
                statement.setString(1, user.getPhoneNumber());
                statement.setInt(2, user.getAddressId());
                statement.setBoolean(3, user.isActive());
                statement.setInt(4, user.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update customer", e);
        }
    }

    @Override
    //TODO: (Scrivimi qui come vuoi che faccia ed eseguo) questo metodo è comune a tutti gli user
    // pensavo la creazione di un classe padre jdbc per gli user
    public void updatePassword(AppUser user, String hashedPassword, TransactionContext context) {
        if (user.getId() == null || user.getId() <= 0)
            throw new IllegalArgumentException("ERROR: the parameter is not valid");

        Connection conn = getConnection(context);
        String sqlUser = "UPDATE app_users SET hashPassword = ? WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sqlUser)) {
            statement.setString(1, hashedPassword);
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update customer password", e);
        }
    }

    @Override
    //TODO: per la costruzione del costumer utilizzo il costruttore di customero passo per la request e poi alla factory,
    // google ai studio dice la prima ps. le factory  al momento sembrano inutili
    public Optional<CustomerUser> findById(Integer id) {
        if (id == null || id <= 0) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find customer by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find customer by username", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find customer by email", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findAll() {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id ";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to fetch all customers", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE c.phoneNumber = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find customer by phone number", e);
        }
        return Optional.empty();
    }

    private CustomerUser mapToCustomer(ResultSet rs) throws SQLException {
        return new CustomerUser(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("phoneNumber"),
                rs.getInt("addressId"),
                rs.getBoolean("active")
        );
    }
}