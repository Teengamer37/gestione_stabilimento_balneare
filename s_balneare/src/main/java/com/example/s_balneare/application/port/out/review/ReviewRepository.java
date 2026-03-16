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
    public Integer save(Review review, TransactionContext context);
    public void delete(Integer id, TransactionContext context);

    //ricerche
    public Optional<Review> findById(Integer id, TransactionContext context);
    public List<Review> findAll(TransactionContext context);
    public List<Review> findByBeachId(Integer beachId, TransactionContext context);
    public List<Review> findByBeachIdAndRating(Integer beachId, Integer rating, TransactionContext context);
}