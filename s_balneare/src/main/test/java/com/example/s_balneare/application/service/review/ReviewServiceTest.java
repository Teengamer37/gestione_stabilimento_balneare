package com.example.s_balneare.application.service.review;

import com.example.s_balneare.application.port.in.review.CreateReviewCommand;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.review.ReviewRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.review.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BeachRepository beachRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BanRepository banRepository;

    //System Under Test (SUT)
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        reviewService = new ReviewService(
                reviewRepository, beachRepository, bookingRepository, banRepository, transactionManager
        );
    }

    // ==========================================
    // TEST ADD REVIEW
    // ==========================================

    @Test
    void addReview_Succeeds_WhenAllChecksPass() {
        //creo Review + Beach attiva
        CreateReviewCommand command = new CreateReviewCommand(10, 100, 5, "Daje Roma Daje! YAHOO!");
        Beach activeBeach = createBeach(true);
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(eq(100), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(100), eq(10), any())).thenReturn(false);
        //simulo che il customer abbia completato un soggiorno
        when(bookingRepository.hasPastConfirmedBooking(eq(100), eq(10), any(LocalDate.class), any())).thenReturn(true);
        when(reviewRepository.save(any(Review.class), any())).thenReturn(555);

        //eseguo il metodo che aggiunge la recensione
        Integer reviewId = reviewService.addReview(command);

        //controllo che il metodo abbia restituito l'ID della recensione generato dal DB
        assertEquals(555, reviewId);

        //verifico i dati passati al repository
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture(), any());
        Review savedReview = captor.getValue();
        assertEquals(10, savedReview.getBeachId());
        assertEquals(100, savedReview.getCustomerId());
        assertEquals(5, savedReview.getRating());
        assertEquals("Daje Roma Daje! YAHOO!", savedReview.getComment());
    }

    @Test
    void addReview_ThrowsException_IfBeachNotFound() {
        //creo Review, ma senza Beach
        CreateReviewCommand command = new CreateReviewCommand(10, 100, 1, "Sal Da Vinci vincitore di Sanremo...");
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non salvare nulla
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(command));
        verify(reviewRepository, never()).save(any(), any());
    }

    @Test
    void addReview_ThrowsException_IfBeachIsInactive() {
        //creo Review + Beach disattivata
        CreateReviewCommand command = new CreateReviewCommand(10, 100, 5, "Ci hanno offerto pure un Aperitivo!");
        Beach inactiveBeach = createBeach(false);
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(inactiveBeach));

        //deve lanciare eccezione e non salvare nulla
        SecurityException ex = assertThrows(SecurityException.class, () -> reviewService.addReview(command));
        assertTrue(ex.getMessage().contains("inactive beach"));
        verify(reviewRepository, never()).save(any(), any());
    }

    @Test
    void addReview_ThrowsException_IfUserBannedFromApp() {
        //creo Review + Beach attiva + Utente bannato dall'app
        CreateReviewCommand command = new CreateReviewCommand(10, 100, 5, "SAREMO IO E TE, ACCUSÌ: SARÀ PE' SEMPRE SÌ");
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(createBeach(true)));
        when(banRepository.isBannedFromApp(eq(100), any())).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        SecurityException ex = assertThrows(SecurityException.class, () -> reviewService.addReview(command));
        assertTrue(ex.getMessage().contains("banned from the app"));
        verify(reviewRepository, never()).save(any(), any());
    }

    @Test
    void addReview_ThrowsException_IfUserBannedFromBeach() {
        //creo Review + Beach attiva + Utente bannato dalla spiaggia
        CreateReviewCommand command = new CreateReviewCommand(10, 100, 3, "Il benzinaio accanto vende gasolio a 2,78€/l");
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(createBeach(true)));
        when(banRepository.isBannedFromApp(eq(100), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(100), eq(10), any())).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        SecurityException ex = assertThrows(SecurityException.class, () -> reviewService.addReview(command));
        assertTrue(ex.getMessage().contains("banned from this beach"));
        verify(reviewRepository, never()).save(any(), any());
    }

    @Test
    void addReview_ThrowsException_IfUserHasNoPastBookings() {
        //creo Review + Beach attiva + Utente non ha nessuna prenotazione fatta in passato
        CreateReviewCommand command = new CreateReviewCommand(10, 100, 5, "Il proprietario tifa Viola, massimo rispetto");
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(createBeach(true)));
        when(banRepository.isBannedFromApp(eq(100), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(100), eq(10), any())).thenReturn(false);
        when(bookingRepository.hasPastConfirmedBooking(eq(100), eq(10), any(), any())).thenReturn(false);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> reviewService.addReview(command));
        assertTrue(ex.getMessage().contains("does not have past confirmed bookings"));
        verify(reviewRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST DELETE REVIEW
    // ==========================================

    @Test
    void deleteReview_Succeeds_IfUserIsAuthorAndNotBanned() {
        //creo Review
        Integer reviewId = 99;
        Integer customerId = 100;
        Review existingReview = new Review(reviewId, 10, customerId, 4, "Forza Pisa", Instant.now());
        when(reviewRepository.findById(eq(reviewId), any())).thenReturn(Optional.of(existingReview));
        when(banRepository.isBannedFromApp(eq(customerId), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(customerId), eq(10), any())).thenReturn(false);

        //chiamo il metodo che elimina la recensione
        reviewService.deleteReview(reviewId, customerId);

        //verifico che sia stato chiamato il metodo di eliminazione
        verify(reviewRepository).delete(eq(reviewId), any(TransactionContext.class));
    }

    @Test
    void deleteReview_ThrowsException_IfReviewNotFound() {
        //mock del metodo che mi ritorna Optional.empty() per simulare la non esistenza della recensione
        when(reviewRepository.findById(eq(99), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non eliminare nulla
        assertThrows(IllegalArgumentException.class, () -> reviewService.deleteReview(99, 100));
        verify(reviewRepository, never()).delete(any(), any());
    }

    @Test
    void deleteReview_ThrowsException_IfUserIsNotAuthor() {
        //creo Review + ID Utente non corrispondente al proprietario della recensione
        Integer reviewId = 99;
        Integer authorId = 100;
        Integer hackerId = 200;
        Review existingReview = new Review(reviewId, 10, authorId, 4, "Bella location, ma peccato per la sporcizia", Instant.now());
        when(reviewRepository.findById(eq(reviewId), any())).thenReturn(Optional.of(existingReview));
        when(banRepository.isBannedFromApp(eq(hackerId), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(hackerId), eq(10), any())).thenReturn(false);

        //deve lanciare eccezione e non eliminare nulla
        SecurityException ex = assertThrows(SecurityException.class, () -> reviewService.deleteReview(reviewId, hackerId));
        assertTrue(ex.getMessage().contains("doesn't belong to this customer"));
        verify(reviewRepository, never()).delete(any(), any());
    }

    @Test
    void deleteReview_ThrowsException_IfUserIsBanned() {
        //creo Review + Utente bannato dall'app
        Integer reviewId = 99;
        Integer customerId = 100;
        Review existingReview = new Review(reviewId, 10, customerId, 2, "Non servono i panini di CiccioGamer, nel resto bella spiaggia", Instant.now());
        when(reviewRepository.findById(eq(reviewId), any())).thenReturn(Optional.of(existingReview));
        when(banRepository.isBannedFromApp(eq(customerId), any())).thenReturn(true);

        //deve lanciare eccezione e non eliminare nulla
        assertThrows(SecurityException.class, () -> reviewService.deleteReview(reviewId, customerId));
        verify(reviewRepository, never()).delete(any(), any());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private Beach createBeach(boolean active) {
        return new Beach(10, 1, 1, new BeachGeneral("Nome", "Desc", "+3900"),
                null, null, null, "", null, null, active, false);
    }
}