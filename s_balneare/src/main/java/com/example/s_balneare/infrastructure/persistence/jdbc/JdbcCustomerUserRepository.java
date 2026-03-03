package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.domain.user.*;

import java.sql.*;
import java.util.Optional;

public class JdbcCustomerUserRepository implements AppUserRepository {
    private final Connection connection;

    public JdbcCustomerUserRepository(Connection connection) {this.connection=connection; }

    @Override
    public int save(AppUser user, String password){
        String sqlBase = "INSERT INTO app_users(name, surname, username, email, hashPassword) " +
                "VALUES(?, ?, ?, ?, ?)";
        String sqlCustomer= "INSERT INTO customers(id, telephoneNumber, addressId, active)" +
                "VALUES(?, ?, ?, ?)";
        String sqlAdmin_Owner= "INSERT INTO customers(id)" +
                "VALUES(?)";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                //statement.setString(5, user.getPassword);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return  1;
    }


    @Override
    public void delete(int id) {
    }

    @Override
    public void update(AppUser user, String password) {

    }

    @Override
    public Optional<AppUser> findById(int id) {
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
