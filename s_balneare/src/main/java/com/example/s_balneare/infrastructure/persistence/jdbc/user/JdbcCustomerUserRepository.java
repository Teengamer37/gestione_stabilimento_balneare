package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.CustomerUserRepository;
import com.example.s_balneare.domain.user.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcCustomerUserRepository
        extends JdbcAppUserRepository<CustomerUser>
        implements CustomerUserRepository {

    protected JdbcCustomerUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, CustomerUser user) throws SQLException{
        String sql = "INSERT INTO customers(id,phoneNumber, addressId, active) VALUES(?,?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setString(2, user.getPhoneNumber());
            statement.setInt(3, user.getAddressId());
            statement.setBoolean(4, user.isActive());
            statement.executeUpdate();
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, CustomerUser user) throws SQLException{
        String sqlCustomer = "UPDATE customers SET phoneNumber = ?, addressId = ?, active = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sqlCustomer)) {
            statement.setString(1, user.getPhoneNumber());
            statement.setInt(2, user.getAddressId());
            statement.setBoolean(3, user.isActive());
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<CustomerUser> findById(Integer id) {
        if (id == null || id <= 0) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, id);
    }

    @Override
    public Optional<CustomerUser> findByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.username = ?";

        return executeFindQuery(sql, username);
    }

    @Override
    public Optional<CustomerUser> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE u.email = ?";

        return executeFindQuery(sql, email);
    }

    @Override
    public List<CustomerUser> findAll() {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id ";
        return executeFindAll(sql);
    }

    @Override
    public Optional<CustomerUser> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return Optional.empty();

        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, " +
                "c.phoneNumber, c.addressId, c.active " +
                "FROM app_users u " +
                "INNER JOIN customers c ON u.id = c.id " +
                "WHERE c.phoneNumber = ?";

        return executeFindQuery(sql, phoneNumber);
    }

    protected CustomerUser mapToEntity(ResultSet rs) throws SQLException {
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