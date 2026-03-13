package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.ReportRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.domain.moderation.ReportTargetType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcReportRepository implements ReportRepository {
    private final DataSource dataSource;

    public JdbcReportRepository(DataSource dataSource) {this.dataSource = dataSource;}

    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }
    @Override
    public Integer save(Report report, TransactionContext context){
        Connection connection = getConnection(context);

        String sql = "INSERT INTO reports(reporterId, reportedId, reportedType, description, createdAt, status) VALUES (?, ?, ?, ?, ?, ?) ";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, report.getReporterId());
            statement.setInt(2, report.getReportedId());
            statement.setString(3, report.getReportedType().name());
            statement.setString(4, report.getDescription());
            statement.setTimestamp(5, java.sql.Timestamp.from(report.getCreatedAt()));
            statement.setString(6, report.getStatus().name());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if(rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: SQL failed, no ID generated for report");
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("ERROR: unable to save reports", e);
        }
    }

    @Override
    public Optional<Report> findById(Integer id, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id,reporterId, reportedId, reportedType, description, createdAt, status FROM reports WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Report> findAll(TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id,reporterId, reportedId, reportedType, description, createdAt, status FROM reports";
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find reports", e);
        }
        return reports;
    }

    @Override
    public List<Report> findByReporterId(Integer reporterId, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id,reporterId, reportedId, reportedType, description, createdAt, status FROM reports WHERE reporterId=?";
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reporterId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by reporterId", e);
        }
        return reports;
    }

    @Override
    public List<Report> findByReportedId(Integer reportedId, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id,reporterId, reportedId, reportedType, description, createdAt, status FROM reports WHERE reportedId=?";
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reportedId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by reportedId", e);
        }
        return reports;
    }

    @Override
    public List<Report> findByReportedType(ReportTargetType reportTargetType, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id,reporterId, reportedId, reportedType, description, createdAt, status FROM reports WHERE reportedType=?";
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportTargetType.name());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by reportedType", e);
        }
        return reports;
    }

    @Override
    public List<Report> findByStatus(ReportStatus status, TransactionContext context) {
        Connection connection = getConnection(context);

        String sql =  "SELECT id,reporterId, reportedId, reportedType, description, createdAt, status FROM reports WHERE status=?";
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find report by status", e);
        }
        return reports;
    }

    @Override
    public void updateStatus(ReportStatus status, TransactionContext context, Integer Id) {
        Connection conn =  getConnection(context);
        String sql =  "UPDATE reports SET status=? WHERE id=?";
        try (PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, status.name());
            statement.setInt(2, Id);
            statement.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update status", e);
        }
    }


    private Report mapToEntity(ResultSet rs) throws SQLException{
        return new Report(
                rs.getInt("id"),
                rs.getInt("reporterId"),
                rs.getInt("reportedId"),
                com.example.s_balneare.domain.moderation.ReportTargetType.valueOf(rs.getString("reportedType")),
                rs.getString("description"),
                rs.getTimestamp("createdAt").toInstant(),
                com.example.s_balneare.domain.moderation.ReportStatus.valueOf(rs.getString("status"))
        );
    }
}
