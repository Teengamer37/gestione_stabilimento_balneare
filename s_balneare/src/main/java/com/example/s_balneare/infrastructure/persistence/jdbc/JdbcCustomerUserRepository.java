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
        //estraggo la connection JDBC
        Connection conn = getConnection(context);

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("ERROR: password must not be null");

        String sqlUser = "INSERT INTO app_users(name, surname, username, email, hashPassword) " +
                "VALUES(?, ?, ?, ?, ?)";
        String sqlCustomer = "INSERT INTO customers(id,phoneNumber, addressId, active)" + "VALUES(?,?,?,?)";

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
            try (PreparedStatement statement = conn.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, newId);
                statement.setString(2, user.getPhoneNumber());
                statement.setInt(3, user.getAddressId());
                statement.setBoolean(4, user.isActive());
                statement.executeUpdate();
            }
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save customer",e);
        }
    }

    @Override
    public void delete(Integer id, TransactionContext context) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        Connection conn = getConnection(context);
        String sqlUser = "DELETE FROM app_users WHERE id = ?";
        String sqlCustomer = "DELETE FROM customers WHERE id = ?";

        try {
            try(PreparedStatement statement = conn.prepareStatement(sqlCustomer)){
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            try(PreparedStatement statement = conn.prepareStatement(sqlUser)){
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete customer", e);
        }
    }

    @Override
    public void update(CustomerUser user) {

    }

    @Override
    public void updatePassword(AppUser user, String password) {

    }

    @Override
    public Optional<CustomerUser> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findAll() {
        return Optional.empty();
    }

    @Override
    public Optional<CustomerUser> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }
}

