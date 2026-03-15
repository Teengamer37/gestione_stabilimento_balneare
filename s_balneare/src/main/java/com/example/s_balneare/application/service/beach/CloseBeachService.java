package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.CloseBeachUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.user.Customer;

import java.time.LocalDate;

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

            //passo 3: annullo i booking futuri
            bookingRepository.cancelFutureBookingsForBeach(beachId, LocalDate.now(), context);

            //passo 4: chiudo la spiaggia e salvo
            beach.closeBeach();
            beachRepository.update(beach, context);
        });
    }
}