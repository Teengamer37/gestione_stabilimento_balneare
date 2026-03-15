package com.example.s_balneare.application.service.moderation;

import com.example.s_balneare.application.port.in.moderation.BanUseCase;
import com.example.s_balneare.application.port.in.moderation.CreateBanCommand;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.moderation.Ban;
import com.example.s_balneare.domain.moderation.BanType;
import com.example.s_balneare.domain.user.Customer;
import com.example.s_balneare.domain.user.Owner;
import com.example.s_balneare.domain.user.User;

import java.time.Instant;
import java.time.LocalDate;

public class BanService<T extends User> implements BanUseCase {
    private final BanRepository banRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository<T> userRepository;
    private final BeachRepository beachRepository;
    private final TransactionManager transactionManager;

    public BanService(BanRepository banRepository, BookingRepository bookingRepository, UserRepository<T> userRepository, BeachRepository beachRepository, TransactionManager transactionManager) {
        this.banRepository = banRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.beachRepository = beachRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public Integer createBan(CreateBanCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //Trovo l'utente
            T user = userRepository.findById(command.bannedId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: User not found"));

            //Flusso di operazione se l'utente è un owner
            if (user instanceof Owner owner) {
                //Controllo che non ci sia id di una spiaggia
                if (command.bannedFromBeachId() != null && command.bannedFromBeachId() > 0) {
                    throw new IllegalStateException("ERROR: Owner cannot be banned from beach");
                }
                //Controllo il ban sia di application
                if (command.banType() == BanType.BEACH) {
                    throw new IllegalStateException("ERROR: owner cannot be banned from beach ");
                }
                Beach beach = beachRepository.findByOwnerId(command.bannedId(), context)
                        .orElseThrow(() -> new IllegalArgumentException("ERROR: beach doesn't exist"));
                //elimino booking futuri
                bookingRepository.cancelFutureBookingsForBeach(beach.getId(), LocalDate.now(), context);
                //Disattiva owner
                owner.closeAccount();
                userRepository.update(user, context);
                //Chiudi spiaggia
                beach.closeBeach();
                beachRepository.update(beach, context);
            }
            //Flusso di operazioni se l'utente è un customer
            else if (user instanceof Customer customer) {
                //Controllo tipo di ban
                if (command.banType() == BanType.BEACH) {
                    //Elimino prenotazioni del singolo utente da quella spiaggia
                    Beach beach = beachRepository.findById(command.bannedFromBeachId(), context)
                            .orElseThrow(() -> new IllegalArgumentException("ERROR: beach doesn't exist"));
                    // metodo che elimina prenotazioni utente da una singola spiaggia
                    bookingRepository.cancelFutureUserBookingsFromBeach(customer.getId(), beach.getId(), LocalDate.now(), context);
                } else { //Ban dall'applicazione
                    //Elimino tutte prenotazioni utente
                    bookingRepository.cancelFutureBookingsForCustomer(customer.getId(), LocalDate.now(), context);
                    //Chiudo definitivamente l'account
                    customer.closeAccount();
                    userRepository.update(user, context);
                }
            }
            // Gestione caso utente non bannabile (es. Admin)
            else {
                throw new IllegalArgumentException("ERROR: This type of user cannot be banned");
            }

            Ban ban = new Ban(0,
                    command.bannedId(),
                    command.banType(),
                    command.bannedFromBeachId(),
                    command.adminId(),
                    command.reason(),
                    Instant.now());

            return banRepository.save(ban, context);
        });
    }

    @Override
    public boolean isUserBannedFromApp(Integer customerId) {
        return transactionManager.executeInTransaction(context -> {
            return banRepository.isBannedFromApp(customerId, context);
        });
    }

    @Override
    public boolean isCustomerBannedFromBeach(Integer customerId, Integer beachId) {
        return transactionManager.executeInTransaction(context -> {
            return banRepository.isBannedFromBeach(customerId, beachId, context);
        });
    }

}