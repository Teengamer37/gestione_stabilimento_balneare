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

public class JdbcOwnerRepository extends JdbcUserRepository<Owner> implements OwnerRepository {
    @Override
    protected void saveSpecificData(Connection conn, Integer newId, Owner user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Owner
        String sql = "INSERT INTO owners(id, OTP) VALUES(?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setBoolean(2, user.isOTP());
            statement.executeUpdate();
        }
    }

    @Override
    protected void updateSpecificData(Connection conn, Owner user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Owner
        String sql = "UPDATE owners SET OTP = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setBoolean(1, user.isOTP());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Owner> findById(Integer id, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.id = ?";

        return executeFindQuery(sql, context, id).stream().findFirst();
    }

    @Override
    public Optional<Owner> findByIdentifier(String identifier, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.username = ? OR u.email = ?";

        //passo identifier due volte (una per lo username, una per l'email)
        return executeFindQuery(sql, context, identifier, identifier).stream().findFirst();
    }

    @Override
    public Optional<Owner> findByUsername(String username, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.username = ?";

        return executeFindQuery(sql, context, username).stream().findFirst();
    }

    @Override
    public Optional<Owner> findByEmail(String email, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.email = ?";

        return executeFindQuery(sql, context, email).stream().findFirst();
    }

    @Override
    public List<Owner> findAll(TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id ";

        return executeFindQuery(sql, context);
    }

    @Override
    protected Owner mapToEntity(ResultSet rs) throws SQLException {
        return new Owner(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getBoolean("active"),
                rs.getBoolean("OTP")
        );
    }
}