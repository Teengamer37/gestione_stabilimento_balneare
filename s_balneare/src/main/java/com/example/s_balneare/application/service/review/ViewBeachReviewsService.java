package com.example.s_balneare.application.service.review;

import com.example.s_balneare.application.port.in.review.ViewBeachReviewsUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachReviewDto;
import com.example.s_balneare.application.port.out.beach.BeachReviewsQuery;

import java.util.List;

/**
 * Implementazione dell'interfaccia che permette l'interazione per il recupero di oggetti Review collegati ad una
 * determinata Beach tra l'app Java e il Database.
 * @see ViewBeachReviewsUseCase ViewBeachReviewsUseCase
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class ViewBeachReviewsService implements ViewBeachReviewsUseCase {
    private final BeachReviewsQuery beachReviewsQuery;
    private final TransactionManager transactionManager;

    public ViewBeachReviewsService(BeachReviewsQuery beachReviewsQuery, TransactionManager transactionManager) {
        this.beachReviewsQuery = beachReviewsQuery;
        this.transactionManager = transactionManager;
    }

    /**
     * Genera una lista con tutte le Review di quella determinata spiaggia
     * @param beachId ID della spiaggio
     * @return lista di recensioni fatte su quella spiaggia
     */
    public List<BeachReviewDto> getBeachReviews(Integer beachId) {
        return transactionManager.executeInTransaction(context -> {
            return beachReviewsQuery.getReviewsByBeachId(beachId, context);
        });
    }

    /**
     * Calcola la valutazione media della spiaggia dalle sue recensioni
     * @param beachId ID della spiaggia
     * @return la valutazione media della spiaggia
     */
    public double getBeachAverageRating(Integer beachId) {
        return transactionManager.executeInTransaction(context -> {
            return beachReviewsQuery.getAverageRating(beachId, context);
        });
    }
}