package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.AdminRepository;
import com.example.s_balneare.domain.common.TransactionContext;
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
    public Optional<Admin> findById(Integer id, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, context, id).stream().findFirst();
    }

    @Override
    public Optional<Admin> findByUsername(String username, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.username = ?";
        return executeFindQuery(sql, context, username).stream().findFirst();
    }

    @Override
    public Optional<Admin> findByEmail(String email, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.email = ?";
        return executeFindQuery(sql, context, email).stream().findFirst();
    }

    @Override
    public List<Admin> findAll(TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id ";
        return executeFindQuery(sql, context);
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
