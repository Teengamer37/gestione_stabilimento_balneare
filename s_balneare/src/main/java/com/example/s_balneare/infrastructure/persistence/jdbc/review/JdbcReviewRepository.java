package com.example.s_balneare.infrastructure.persistence.jdbc.review;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.review.ReviewRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.review.Review;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Review tramite
 * libreria JDBC.
 *
 * @see ReviewRepository ReviewRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcReviewRepository implements ReviewRepository {
    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext) e
     * lo converte di nuovo nella classe concreta per estrarre java.sql.Connection.
     *
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext(Connection connection))) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return connection;
    }

    /**
     * Salva una nuova Review nel DB.
     *
     * @param review  Oggetto Review da salvare
     * @param context Connessione JDBC
     * @return ID generato dal Database del nuovo Review
     * @throws SQLException     se ci sono problemi col Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Integer save(Review review, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "INSERT INTO reviews(beachId, customerId, rating, comment, createdAt) VALUES (?, ?, ?, ?, ?)";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            //lanciato se l'utente tenta di lasciate più recensioni alla stessa spiaggia
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException("ERROR: the beach was already reviewed by this customer");
            }
            throw new RuntimeException("ERROR: unable to save review", e);
        }
    }

    /**
     * Elimina una Review dal DB.
     *
     * @param id      ID della Review da eliminare
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void delete(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection conn = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: ID not valid");

        String sql = "DELETE FROM reviews WHERE id = ?";

        //query
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete review", e);
        }
    }

    /**
     * Cerca una Review tramite ID nel DB.
     *
     * @param id      ID della Review da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se trovato la Review, può essere estratto l'oggetto Review; altri metodi altrimenti
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Review> findById(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: ID not valid");

        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews WHERE id = ?";

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    /**
     * Estrae tutte le Review registrate nel DB (da usare solo per scopi di debugging).
     *
     * @param context Connessione JDBC
     * @return una lista di tutte le Review salvate nel Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Review> findAll(TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews";
        List<Review> reviews = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                reviews.add(mapToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find reviews", e);
        }
        return reviews;
    }

    /**
     * Cerca le Review fatte ad una spiaggia.
     *
     * @param beachId ID della spiaggia
     * @param context Connessione JDBC
     * @return lista di Review che corrispondono a quella spiaggia
     * @throws IllegalArgumentException se ID passato non valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Review> findByBeachId(Integer beachId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");

        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews WHERE beachId = ?";
        List<Review> reviews = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    /**
     * Cerca le Review fatte ad una spiaggia con una specifica valutazione.
     *
     * @param beachId ID della spiaggia
     * @param rating  Valutazione da cercare
     * @param context Connessione JDBC
     * @return lista di Review che corrispondono a quella spiaggia
     * @throws IllegalArgumentException se parametri passati non validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Review> findByBeachIdAndRating(Integer beachId, Integer rating, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid");
        if (rating == null || rating < 1 || rating > 5) throw new IllegalArgumentException("ERROR: rating not valid");

        String sql = "SELECT id, beachId, customerId, rating, comment, createdAt FROM reviews WHERE beachId = ? AND rating = ?";
        List<Review> reviews = new ArrayList<>();

        //query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, beachId);
            statement.setInt(2, rating);
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

    /**
     * Metodo privato che prende l'oggetto ResultSet e restituisce un oggetto Review creato da esso.
     *
     * @param rs Oggetto contenente riga di una operazione SQL
     * @return oggetto Review creato
     * @throws SQLException se ci sono problemi col Database
     * @see ResultSet ResultSet
     */
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