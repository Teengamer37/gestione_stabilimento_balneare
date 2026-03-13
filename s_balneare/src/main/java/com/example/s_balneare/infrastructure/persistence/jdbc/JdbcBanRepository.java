package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.BanRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Ban;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBanRepository implements BanRepository {
    private final DataSource dataSource;

    public JdbcBanRepository(DataSource dataSource) {this.dataSource = dataSource;}

    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }


    @Override
    public Integer save(Ban ban, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql = "INSERT INTO bans (bannedId, banType, bannedFromBeachId, adminId, reason, time) VALUES (?, ?, ?, ?, ?, ?) ";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, ban.getBannedId());
            statement.setString(2, ban.getBanType().name());
            statement.setInt(3, ban.getBannedFromBeachId());
            statement.setInt(4, ban.getAdminId());
            statement.setString(5, ban.getReason());
            statement.setTimestamp(6, java.sql.Timestamp.from(ban.getCreatedAt()));
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: SQL failed, no ID generated for ban");
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("ERROR: unable to save ban", e);
        }
    }

    @Override
    public Optional<Ban> findById(Integer id, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id, bannedId, banType, bannedFromBeachId, adminId, reason, time FROM bans WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Ban> findAll(TransactionContext context) {

        Connection connection = getConnection(context);

        String sql =  "SELECT id, bannedId, banType, bannedFromBeachId, adminId, reason, time FROM bans";
        List<Ban> bans = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    bans.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find addresses", e);
        }
        return bans;
    }

    @Override
    public Optional<Ban> findByBannedId(Integer bannedId, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id, bannedId, banType, bannedFromBeachId, adminId, reason, time FROM bans WHERE bannedId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bannedId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by BannedID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ban> findbyBannedFromBeachId(Integer bannedFromBeachId, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id, bannedId, banType, bannedFromBeachId, adminId, reason, time FROM bans WHERE bannedFromBeachId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bannedFromBeachId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by bannedFrombeachID", e);
        }
        return Optional.empty();
    }

    private Ban mapToEntity(ResultSet rs) throws SQLException{
        return new Ban(
                rs.getInt("id"),
                rs.getInt("bannedId"),
                com.example.s_balneare.domain.moderation.BanType.valueOf(rs.getString("banType")),
                rs.getInt("bannedFromBeachId"),
                rs.getInt("adminId"),
                rs.getString("reason"),
                rs.getTimestamp("time").toInstant()
        );
    }
}
