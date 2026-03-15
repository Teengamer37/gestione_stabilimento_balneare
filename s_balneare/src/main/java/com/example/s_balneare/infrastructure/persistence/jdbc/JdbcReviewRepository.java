package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.review.ReviewRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.review.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcReviewRepository implements ReviewRepository {
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }

    @Override
    public Integer save(Review review, TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "INSERT INTO reviews(beachId, customerId, rating, comment, createdAt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, review.getBeachId());
            statement.setInt(2, review.getCustomerId());
            statement.setInt(3, review.getRating());
            statement.setString(4, review.getComment());
            statement.setTimestamp(5, java.sql.Timestamp.from(review.getCreatedAt()));
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: SQL failed, no ID generated for review");
                }
            }
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException("ERROR: the beach was already reviewed by this customer");
            }
            throw new RuntimeException("ERROR: unable to save review", e);
        }
    }

    @Override
    public void delete(Integer id, TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete review", e);
        }
    }

    @Override
    public Optional<Review> findById(Integer id, TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find review by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Review> findAll(TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews";
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                reviews.add(mapToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find reviews", e);
        }
        return reviews;
    }

    @Override
    public List<Review> findByBeachId(Integer beachId, TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews WHERE beachId = ?";
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, beachId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find reviews by beachId", e);
        }
        return reviews;
    }

    @Override
    public List<Review> findByBeachIdAndRating(Integer beachId, Integer ratingId, TransactionContext context) {
        Connection conn = getConnection(context);
        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews WHERE beachId = ? AND rating = ?";
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, beachId);
            statement.setInt(2, ratingId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find reviews by beachId", e);
        }
        return reviews;
    }

    private Review mapToEntity(ResultSet rs) throws SQLException {
        return new Review(
                rs.getInt("id"),
                rs.getInt("beachId"),
                rs.getInt("customerId"),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getTimestamp("createdAt").toInstant()
        );
    }
}