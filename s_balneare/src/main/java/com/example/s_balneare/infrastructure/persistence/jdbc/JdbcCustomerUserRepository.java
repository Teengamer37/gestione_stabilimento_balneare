package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.domain.user.*;

import java.sql.*;
import java.util.Optional;

//FIXME: un macello qui

public class JdbcCustomerUserRepository implements AppUserRepository {
    private final Connection connection;

    public JdbcCustomerUserRepository(Connection connection) {this.connection=connection; }

    @Override
    public Integer save(AppUser inputUser, String password){
        //check validità inputUser
        if (!(inputUser instanceof CustomerUser user)){
            throw new RuntimeException("ERROR: user is not a CustomerUser");
        }
        //check validità password
        if (password == null || password.isBlank()) throw new IllegalArgumentException("ERROR: password must not be null");


        String sqlUser = "INSERT INTO app_users(name, surname, username, email, hashPassword) " +
                "VALUES(?, ?, ?, ?, ?)";
        String sqlCustomer = "INSERT INTO customers(id,telephoneNumber, addressId, active)" + "VALUES(?,?,?)";
        try {
            connection.setAutoCommit(false);
            int newId;

            try (PreparedStatement statement = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getSurname());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getEmail());
                statement.setString(5, password);
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                    else throw new SQLException("ERROR: SQL FAILED, no ID generated for beach");
                }
            }

            try(PreparedStatement statement = connection.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, newId);
                statement.setString(2, user.getPhoneNumber());
                statement.setInt(3,user.getAddressId());
                statement.setBoolean(4, user.isActive());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return  1;
    }


    @Override
    public void delete(Integer id) {
    }

    @Override
    public void update(AppUser user, String password) {

    }

    @Override
    public Optional<AppUser> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }
}
