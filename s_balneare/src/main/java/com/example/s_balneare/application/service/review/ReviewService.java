package com.example.s_balneare.application.service.review;

import com.example.s_balneare.application.port.in.booking.BookingUseCase;
import com.example.s_balneare.application.port.in.review.CreateReviewCommand;
import com.example.s_balneare.application.port.in.review.ReviewUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.review.ReviewRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.review.Review;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Review tra l'app Java e il Database.
 * <p>Usa ReviewRepository per manipolare oggetti Review nel DB;
 * <p>Usa BeachRepository per recuperare la spiaggia interessata alla recensione;
 * <p>Usa BookingRepository per recuperare la prenotazione interessata alla recensione;
 * <p>Usa BanRepository per verificare lo stato degli attori interessati.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see ReviewUseCase ReviewUseCase
 * @see ReviewRepository ReviewRepository
 * @see BeachRepository BeachRepository
 * @see BookingRepository BookingRepository
 * @see BanRepository BanRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class ReviewService implements ReviewUseCase {
    private final ReviewRepository reviewRepository;
    private final BeachRepository beachRepository;
    private final BookingRepository bookingRepository;
    private final BanRepository banRepository;
    private final TransactionManager transactionManager;

    public ReviewService(ReviewRepository reviewRepository,
                         BeachRepository beachRepository,
                         BookingRepository bookingRepository,
                         BanRepository banRepository,
                         TransactionManager transactionManager) {
        this.reviewRepository = reviewRepository;
        this.beachRepository = beachRepository;
        this.bookingRepository = bookingRepository;
        this.banRepository = banRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Aggiunge una nuova recensione nel DB.
     *
     * @param command parametri necessari per la creazione di una nuova recensione
     * @return ID della recensione appena creata
     * @throws IllegalArgumentException se gli argomenti passati non esistono nel DB
     * @throws SecurityException        se si prova a lasciare una recensione ad una spiaggia non attiva/bannata/chiusa
     * @throws IllegalStateException    se l'utente non ha un booking passato in stato CONFIRMED in quella spiaggia
     */
    @Override
    public Integer addReview(CreateReviewCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //passo 1: trovo la spiaggia da valutare
            Beach beach = beachRepository.findById(command.beachId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found"));
            //controllo se spiaggia attiva
            if (!beach.isActive()) {
                throw new SecurityException("ERROR: cannot review an inactive beach");
            }

            //passo 2: controllo che l'utente non sia bannato dalla spiaggia e dall'app
            if (banRepository.isBannedFromApp(command.customerId(), context)) {
                throw new SecurityException("ERROR: user is banned from the app");
            }
            if (banRepository.isBannedFromBeach(command.customerId(), command.beachId(), context)) {
                throw new SecurityException("ERROR: user is banned from this beach");
            }

            //passo 3: controllo che l'utente abbia effettivamente visitato la spiaggia con un booking confermato nel passato
            boolean hasCompletedStay = bookingRepository.hasPastConfirmedBooking(
                    command.customerId(),
                    command.beachId(),
                    LocalDate.now(),
                    context
            );
            if (!hasCompletedStay) {
                throw new IllegalStateException("ERROR: customer does not have past confirmed bookings in this structure");
            }

            //passo 4: creo oggetto Review
            Review review = new Review(
                    0,
                    command.beachId(),
                    command.customerId(),
                    command.rating(),
                    command.comment(),
                    Instant.now()
            );

            //passo 5: salvo nel DB
            return reviewRepository.save(review, context);
        });
    }

    /**
     * Elimina una recensione dal DB.
     *
     * @param reviewId   ID della recensione da eliminare
     * @param customerId ID del Customer creatore della recensione
     * @throws IllegalArgumentException se la recensione non esiste nel DB
     * @throws SecurityException        se si prova a cancellare una recensione di un altro Customer
     */
    @Override
    public void deleteReview(Integer reviewId, Integer customerId) {
        transactionManager.executeInTransaction(context -> {
            //passo 1: cerco la review nel DB
            Review review = reviewRepository.findById(reviewId, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Review not found"));

            //passo 2: controllo che l'utente non sia bannato dalla spiaggia e dall'app
            if (banRepository.isBannedFromApp(customerId, context)) {
                throw new SecurityException("ERROR: user is banned from the app");
            }
            if (banRepository.isBannedFromBeach(customerId, review.getBeachId(), context)) {
                throw new SecurityException("ERROR: user is banned from this beach");
            }

            //passo 3: controllo che la review sia effettivamente dell'utente richiedente
            if (!review.getCustomerId().equals(customerId)) {
                throw new SecurityException("ERROR: the review doesn't belong to this customer");
            }

            //passo 4: elimino la recensione dal DB
            reviewRepository.delete(reviewId, context);
        });
    }
}