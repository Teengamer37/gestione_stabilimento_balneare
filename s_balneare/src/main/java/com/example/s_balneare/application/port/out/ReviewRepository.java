package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.review.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    public Integer save(Review review, TransactionContext context);
    public void delete(Integer id, TransactionContext context);
    public Optional<Review> findById(Integer id, TransactionContext context);
    public List<Review> findAll(TransactionContext context);
    public List<Review> findByBeachId(Integer beachId, TransactionContext context);
    public List<Review> findByBeachIdAndRating(Integer beachId, Integer raitingId, TransactionContext context);
}
