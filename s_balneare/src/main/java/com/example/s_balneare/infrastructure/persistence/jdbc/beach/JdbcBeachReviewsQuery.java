package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.application.port.out.beach.BeachReviewDto;
import com.example.s_balneare.application.port.out.beach.BeachReviewsQuery;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository che gestisce tramite SQL e JDBC metodi di recupero recensioni di una determinata spiaggia.
 *
 * @see BeachReviewsQuery BeachReviewsQuery
 */
public class JdbcBeachReviewsQuery implements BeachReviewsQuery {
    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext)
     * e lo converte di nuovo nella classe concreta per estrarre java.sql.Connection.
     *
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.connection();
    }

    /**
     * Cerca tutte le recensioni di una determinata spiaggia.
     *
     * @param beachId ID della spiaggia
     * @param context Connessione JDBC
     * @return una lista di recensioni per quella spiaggia
     * @throws IllegalArgumentException se i parametri passati non sono validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<BeachReviewDto> getReviewsByBeachId(Integer beachId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");

        String sql = "SELECT r.id, r.rating, r.comment, r.createdAt, u.name, u.surname " +
                "FROM reviews r " +
                "JOIN users u ON r.customerId = u.id " +
                "WHERE r.beachId = ? " +
                "ORDER BY r.createdAt DESC";
        List<BeachReviewDto> reviews = new ArrayList<>();

        //esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, beachId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    //assemblo pian piano ogni singola recensione
                    reviews.add(new BeachReviewDto(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getTimestamp("createdAt").toInstant()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to fetch beach reviews", e);
        }
        return reviews;
    }

    /**
     * Calcola la valutazione media di una spiaggia.
     *
     * @param beachId ID della spiaggia
     * @param context Connessione JDBC
     * @return valutazione media della spiaggia
     * @throws IllegalArgumentException se i parametri passati non sono validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public double getAverageRating(Integer beachId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");

        //se ci sono 0 reviews per la spiaggia, COALESCE mette il numero a 0.0 invece che a NULL
        String sql = "SELECT COALESCE(AVG(rating), 0.0) AS avgRating FROM reviews WHERE beachId = ?";

        //esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, beachId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    //arrotondamento ad una cifra decimale dopo la virgola
                    double rawAvg = rs.getDouble("avgRating");
                    return Math.round(rawAvg * 10.0) / 10.0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to calculate average rating", e);
        }
        //nel caso le cose vadano male, ritorno 0.0
        return 0.0;
    }
}