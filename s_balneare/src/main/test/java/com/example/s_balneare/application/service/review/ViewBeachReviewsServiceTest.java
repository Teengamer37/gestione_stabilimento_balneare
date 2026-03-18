package com.example.s_balneare.application.service.review;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachReviewDto;
import com.example.s_balneare.application.port.out.beach.BeachReviewsQuery;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewBeachReviewsServiceTest {
    //mock della porta di uscita (il database fittizio)
    @Mock
    private BeachReviewsQuery beachReviewsQuery;

    //System Under Test (SUT)
    private ViewBeachReviewsService viewBeachReviewsService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        viewBeachReviewsService = new ViewBeachReviewsService(beachReviewsQuery, transactionManager);
    }

    // ==========================================
    // TEST PER getBeachReviews()
    // ==========================================

    @Test
    void getBeachReviews_ReturnsListOfReviews_WhenReviewsExist() {
        //creo Beach + lista di Review
        Integer beachId = 10;
        List<BeachReviewDto> expectedReviews = List.of(
                new BeachReviewDto(1, "Roberto", "Benigni", 5, "La vita è bella, come questa spiaggia!", Instant.now()),
                new BeachReviewDto(2, "Luigi", "Verdi", 4, "Non avevano la pasta in bianco al ristorante, ma nel resto fantastica spiaggia", Instant.now())
        );
        //simulo comportamento database
        when(beachReviewsQuery.getReviewsByBeachId(eq(beachId), any(TransactionContext.class)))
                .thenReturn(expectedReviews);

        //chiamo il metodo
        List<BeachReviewDto> actualReviews = viewBeachReviewsService.getBeachReviews(beachId);

        //controllo i dati ricevuti
        assertEquals(2, actualReviews.size());
        assertEquals("Roberto", actualReviews.getFirst().customerName());
        assertEquals(5, actualReviews.getFirst().rating());
        verify(beachReviewsQuery, times(1)).getReviewsByBeachId(eq(beachId), any(TransactionContext.class));
    }

    @Test
    void getBeachReviews_ReturnsEmptyList_WhenNoReviewsExist() {
        //creo Beach + lista vuota di Review
        Integer beachId = 20;
        when(beachReviewsQuery.getReviewsByBeachId(eq(beachId), any(TransactionContext.class)))
                .thenReturn(Collections.emptyList());

        //chiamo il metodo
        List<BeachReviewDto> actualReviews = viewBeachReviewsService.getBeachReviews(beachId);

        //controllo di aver ricevuto una lista vuota
        assertTrue(actualReviews.isEmpty());
        verify(beachReviewsQuery, times(1)).getReviewsByBeachId(eq(beachId), any(TransactionContext.class));
    }

    // ==========================================
    // TEST PER getBeachAverageRating()
    // ==========================================
    //*la media delle valutazioni viene fatta lato SQL, non c'è tanto da testare qui effettivamente...

    @Test
    void getBeachAverageRating_ReturnsCorrectAverage() {
        //preparo i dati
        Integer beachId = 10;
        double expectedAverage = 4.5;
        when(beachReviewsQuery.getAverageRating(eq(beachId), any(TransactionContext.class)))
                .thenReturn(expectedAverage);

        //chiamo il metodo
        double actualAverage = viewBeachReviewsService.getBeachAverageRating(beachId);

        //verifico mi abbia ritornato il valore prodotto dal mockup del DB
        assertEquals(expectedAverage, actualAverage);
        verify(beachReviewsQuery, times(1)).getAverageRating(eq(beachId), any(TransactionContext.class));
    }

    @Test
    void getBeachAverageRating_ReturnsZero_WhenNoReviews() {
        //preparo i dati
        Integer beachId = 30;
        //simulo il comportamento del DB (COALESCE(AVG(rating), 0.0) = 0.0)
        when(beachReviewsQuery.getAverageRating(eq(beachId), any(TransactionContext.class)))
                .thenReturn(0.0);

        //chiamo il metodo
        double actualAverage = viewBeachReviewsService.getBeachAverageRating(beachId);

        //mi assicuro di aver ricevuto 0.0
        assertEquals(0.0, actualAverage);
        verify(beachReviewsQuery, times(1)).getAverageRating(eq(beachId), any(TransactionContext.class));
    }
}