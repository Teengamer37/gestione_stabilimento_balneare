package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.CustomerRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcCustomerRepository extends JdbcUserRepository<Customer> implements CustomerRepository {
    @Override
    protected void saveSpecificData(Connection conn, Integer newId, Customer user) throws SQLException {
        String sql = "INSERT INTO customers(id, phoneNumber, addressId, active) VALUES(?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setString(2, user.getPhoneNumber());
            statement.setInt(3, user.getAddressId());
            statement.setBoolean(4, user.isActive());
            statement.executeUpdate();
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException("ERROR: Phone Number is already in use by another account");
            }
            throw e;
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, Customer user) throws SQLException {
        String sqlCustomer = "UPDATE customers SET phoneNumber = ?, addressId = ?, active = ? WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sqlCustomer)) {
            statement.setString(1, user.getPhoneNumber());
            statement.setInt(2, user.getAddressId());
            statement.setBoolean(3, user.isActive());
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException("ERROR: Phone Number is already in use by another account");
            }
            throw e;
        }
    }

    @Override
    public Optional<Customer> findById(Integer id, TransactionContext context) {
        if (id == null || id <= 0) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, context, id).stream().findFirst();
    }
    @Override
    public Optional<Customer> findByIdentifier(String identifier, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.username = ? OR u.email = ?";

        //passo identifier due volte (una per lo username, una per l'email)
        return executeFindQuery(sql, context, identifier, identifier).stream().findFirst();
    }

    @Override
    public Optional<Customer> findByUsername(String username, TransactionContext context) {
        if (username == null || username.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.username = ?";

        return executeFindQuery(sql, context, username).stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email, TransactionContext context) {
        if (email == null || email.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.email = ?";

        return executeFindQuery(sql, context, email).stream().findFirst();
    }

    @Override
    public List<Customer> findAll(TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM users u " +
                "INNER JOIN customers c ON u.id = c.id ";

        return executeFindQuery(sql,context);
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber, TransactionContext context) {
        if (phoneNumber == null || phoneNumber.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE c.phoneNumber = ?";

        return executeFindQuery(sql, context, phoneNumber).stream().findFirst();
    }

    protected Customer mapToEntity(ResultSet rs) throws SQLException {
        return new Customer(
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