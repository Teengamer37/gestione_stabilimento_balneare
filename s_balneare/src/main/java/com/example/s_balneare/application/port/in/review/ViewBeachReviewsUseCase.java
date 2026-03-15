package com.example.s_balneare.application.port.in.review;

import com.example.s_balneare.application.port.out.beach.BeachReviewDto;
import com.example.s_balneare.application.service.review.ViewBeachReviewsService;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per mostrare i vari Reviews di una Beach.
 * Implementata in:
 *
 * @see ViewBeachReviewsService ViewBeachReviewsService
 */
public interface ViewBeachReviewsUseCase {
    List<BeachReviewDto> getBeachReviews(Integer beachId);
    double getBeachAverageRating(Integer beachId);
}