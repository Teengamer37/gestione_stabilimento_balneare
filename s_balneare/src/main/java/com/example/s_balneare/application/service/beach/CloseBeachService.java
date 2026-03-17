package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.CloseBeachUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.domain.beach.Beach;

import java.time.LocalDate;

/**
 * Servizio che implementa lo Use Case di chiusura di una spiaggia.<br>
 * Usa BeachRepository per prelevare la spiaggia dal database e disattivarla;<br>
 * Usa BookingRepository per annullare tutte le future prenotazioni a quella spiaggia.<br>
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see CloseBeachUseCase CloseBeachUseCase
 * @see BeachRepository BeachRepository
 * @see BookingRepository BookingRepository
 * @see TransactionManager TransactionManager
 */
public class CloseBeachService implements CloseBeachUseCase {
    private final BeachRepository beachRepository;
    private final BookingRepository bookingRepository;
    private final TransactionManager transactionManager;

    public CloseBeachService(BeachRepository beachRepository,
                             BookingRepository bookingRepository,
                             TransactionManager transactionManager) {
        this.beachRepository = beachRepository;
        this.bookingRepository = bookingRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Chiude una spiaggia, annullando tutte le future prenotazioni
     *
     * @param beachId ID della spiaggia da disattivare
     * @param ownerId ID dell'utente proprietario della spiaggia
     * @throws IllegalArgumentException se la spiaggia non esiste nel DB
     * @throws SecurityException se l'ownerId passato non è il proprietario della spiaggia
     */
    @Override
    public void closeBeach(Integer beachId, Integer ownerId) {
        transactionManager.executeInTransaction(context -> {
            //passo 1: cerco la spiaggia interessata
            Beach beach = beachRepository.findById(beachId, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found"));

            //passo 2: verifico che l'utente sia l'effettivo proprietario della spiaggia
            if (!beach.getOwnerId().equals(ownerId)) {
                throw new SecurityException("ERROR: ownerId is not the owner of this beach");
            }

            //passo 3: chiudo la spiaggia
            beach.closeBeach();

            //passo 4: annullo i booking futuri e salvo
            bookingRepository.cancelFutureBookingsForBeach(beachId, LocalDate.now(), context);
            beachRepository.update(beach, context);
        });
    }
}