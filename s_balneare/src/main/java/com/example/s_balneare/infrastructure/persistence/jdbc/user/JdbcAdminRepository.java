package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.AdminRepository;
import com.example.s_balneare.domain.user.Admin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcAdminRepository
        extends JdbcUserRepository<Admin>
        implements AdminRepository {


    protected JdbcAdminRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, Admin user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Admin
        String sql = "INSERT INTO admins(id) VALUES(?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.executeUpdate();
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, Admin user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
    }

    @Override
    public Optional<Admin> findById(Integer id) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, id);
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.username = ?";
        return executeFindQuery(sql, username);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.email = ?";
        return executeFindQuery(sql, email);
    }

    @Override
    public List<Admin> findAll() {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id ";
        return executeFindAll(sql);
    }

    @Override
    protected Admin mapToEntity(ResultSet rs) throws SQLException {
        //Scrivere qui il codice per mappare gli attributi aggiuntivi di Admin
        return new Admin(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname")
        );
    }
}
