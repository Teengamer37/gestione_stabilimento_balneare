package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.AdminUserRepository;
import com.example.s_balneare.domain.user.AdminUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcAdminUserRepository
        extends JdbcAppUserRepository<AdminUser>
        implements AdminUserRepository {


    protected JdbcAdminUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, AdminUser user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Admin
        String sql = "INSERT INTO admins(id) VALUES(?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.executeUpdate();
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, AdminUser user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
    }

    @Override
    public Optional<AdminUser> findById(Integer id) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, id);
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.username = ?";
        return executeFindQuery(sql, username);
    }

    @Override
    public Optional<AdminUser> findByEmail(String email) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.email = ?";
        return executeFindQuery(sql, email);
    }

    @Override
    public List<AdminUser> findAll() {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN admins a ON u.id = a.id ";
        return executeFindAll(sql);
    }

    @Override
    protected AdminUser mapToEntity(ResultSet rs) throws SQLException {
        //Scrivere qui il codice per mappare gli attributi aggiuntivi di Admin
        return new AdminUser(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname")
        );
    }
}
