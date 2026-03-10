package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.OwnerUserRepository;
import com.example.s_balneare.domain.user.OwnerUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcOwnerUserRepository
        extends JdbcAppUserRepository<OwnerUser>
        implements OwnerUserRepository {


    protected JdbcOwnerUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, OwnerUser user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Admin
        String sql = "INSERT INTO owners(id) VALUES(?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.executeUpdate();
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, OwnerUser user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
    }

    @Override
    public Optional<OwnerUser> findById(Integer id) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, id);
    }

    @Override
    public Optional<OwnerUser> findByUsername(String username) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.username = ?";
        return executeFindQuery(sql, username);
    }

    @Override
    public Optional<OwnerUser> findByEmail(String email) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.email = ?";
        return executeFindQuery(sql, email);
    }

    @Override
    public List<OwnerUser> findAll() {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM app_users u " +
                "INNER JOIN owners o ON u.id = o.id ";
        return executeFindAll(sql);
    }

    @Override
    protected OwnerUser mapToEntity(ResultSet rs) throws SQLException {
        return new OwnerUser(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname")
        );
    }

}