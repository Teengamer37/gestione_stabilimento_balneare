package com.example.s_balneare.application.port.out.review;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.review.Review;
import com.example.s_balneare.infrastructure.persistence.jdbc.review.JdbcReviewRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare oggetti di tipo Review.<br>
 * Implementata in:
 *
 * @see JdbcReviewRepository JdbcReviewRepository
 */
public interface ReviewRepository {
    //manipolazione
    Integer save(Review review, TransactionContext context);
    void delete(Integer id, TransactionContext context);

    //ricerche
    Optional<Review> findById(Integer id, TransactionContext context);
    List<Review> findAll(TransactionContext context);
    List<Review> findByBeachId(Integer beachId, TransactionContext context);
    List<Review> findByBeachIdAndRating(Integer beachId, Integer rating, TransactionContext context);
}