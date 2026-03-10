package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.OwnerRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Owner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcOwnerRepository
        extends JdbcUserRepository<Owner>
        implements OwnerRepository {


    protected JdbcOwnerRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, Owner user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Admin
        String sql = "INSERT INTO owners(id) VALUES(?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.executeUpdate();
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, Owner user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
    }

    @Override
    public Optional<Owner> findById(Integer id, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.id = ?";
        return executeFindQuery(sql, id, context);
    }

    @Override
    public Optional<Owner> findByUsername(String username, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.username = ?";
        return executeFindQuery(sql, username, context );
    }

    @Override
    public Optional<Owner> findByEmail(String email, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.email = ?";
        return executeFindQuery(sql, email, context);
    }

    @Override
    public List<Owner> findAll(TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id ";
        return executeFindAll(sql, context);
    }

    @Override
    protected Owner mapToEntity(ResultSet rs) throws SQLException {
        return new Owner(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname")
        );
    }

}