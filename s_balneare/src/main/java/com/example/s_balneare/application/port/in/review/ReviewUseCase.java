package com.example.s_balneare.application.port.in.review;

import com.example.s_balneare.application.service.review.ReviewService;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Review.<br>
 * Implementata in:
 *
 * @see ReviewService ReviewService
 */
public interface ReviewUseCase {
    Integer addReview(CreateReviewCommand command);

    //passo customerId per verificare se il creatore del Review coincide
    void deleteReview(Integer reviewId, Integer customerId);
}