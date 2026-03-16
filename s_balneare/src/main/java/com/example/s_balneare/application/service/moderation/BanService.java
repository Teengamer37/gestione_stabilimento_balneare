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

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Address tra l'app Java e il Database.<br>
 * Usa BanRepository per manipolare l’oggetto Ban nel database;<br>
 * Usa BookingRepository per annullare tutte le prenotazioni future di un customer/fatte su una spiaggia;<br>
 * Usa UserRepository per recuperare e aggiornare righe di utenti partecipanti al Ban;<br>
 * Usa BeachRepository per recuperare e aggiornare righe di spiagge partecipanti al Ban.<br>
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see BanUseCase BanUseCase
 * @see BanRepository BanRepository
 * @see BookingRepository BookingRepository
 * @see UserRepository UserRepository
 * @see BeachRepository BeachRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
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

    /**
     * Crea e applica un nuovo ban.
     *
     * @param command Oggetto contenente tutti gli attributi necessari per creare il ban
     * @return ID univoco generato dal Database
     * @see CreateBanCommand CreateBanCommand
     */
    @Override
    public Integer createBan(CreateBanCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //trovo l'utente
            T user = userRepository.findById(command.bannedId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: User not found"));

            //flusso di operazione se l'utente è un Owner
            if (user instanceof Owner owner) {
                //controllo che non ci sia ID di una spiaggia
                if (command.bannedFromBeachId() != null && command.bannedFromBeachId() > 0) {
                    throw new IllegalStateException("ERROR: Owner cannot be banned from beach");
                }

                //controllo che il ban sia di tipo application
                if (command.banType() == BanType.BEACH) {
                    throw new IllegalStateException("ERROR: Owner cannot be banned from beach");
                }

                //recupero Beach dal DB
                Beach beach = beachRepository.findByOwnerId(command.bannedId(), context)
                        .orElseThrow(() -> new IllegalArgumentException("ERROR: beach doesn't exist"));

                //elimino booking futuri
                bookingRepository.cancelFutureBookingsForBeach(beach.getId(), LocalDate.now(), context);

                //disattivo owner
                owner.closeAccount();
                userRepository.update(user, context);

                //chiudo spiaggia
                beach.closeBeach();
                beachRepository.update(beach, context);
            //flusso di operazioni se l'utente è un Customer
            } else if (user instanceof Customer customer) {
                //controllo tipo di ban
                if (command.banType() == BanType.BEACH) {
                    //se tipo BEACH, elimino prenotazioni del singolo utente da quella spiaggia
                    Beach beach = beachRepository.findById(command.bannedFromBeachId(), context)
                            .orElseThrow(() -> new IllegalArgumentException("ERROR: beach doesn't exist"));

                    //elimino prenotazioni utente da una singola spiaggia
                    bookingRepository.cancelFutureUserBookingsFromBeach(customer.getId(), beach.getId(), LocalDate.now(), context);
                } else {
                    //se tipo APPLICATION, elimino tutte le sue prenotazioni
                    bookingRepository.cancelFutureBookingsForCustomer(customer.getId(), LocalDate.now(), context);

                    //chiudo definitivamente l'account
                    customer.closeAccount();
                    userRepository.update(user, context);
                }
            //gestione caso utente non bannabile (es. Admin)
            } else {
                throw new IllegalArgumentException("ERROR: This type of user cannot be banned");
            }

            //creo nuovo oggetto Ban
            Ban ban = new Ban(0,
                    command.bannedId(),
                    command.banType(),
                    command.bannedFromBeachId(),
                    command.adminId(),
                    command.reason(),
                    Instant.now());

            //salvo nel DB
            return banRepository.save(ban, context);
        });
    }

    /**
     * Controlla se l’utente in questione ha un ban attivo a livello applicazione.
     *
     * @param customerId ID dell'utente da controllare
     * @return TRUE se c’è un ban attivo a livello applicazione per l’utente, FALSE altrimenti
     */
    @Override
    public boolean isUserBannedFromApp(Integer customerId) {
        return transactionManager.executeInTransaction(context -> {
            return banRepository.isBannedFromApp(customerId, context);
        });
    }

    /**
     * Controlla se il Customer in questione ha un ban attivo su una determinata spiaggia.
     *
     * @param customerId ID dell'utente da controllare
     * @param beachId ID della spiaggia
     * @return TRUE se c’è un ban attivo su quella spiaggia, FALSE altrimenti
     */
    @Override
    public boolean isCustomerBannedFromBeach(Integer customerId, Integer beachId) {
        return transactionManager.executeInTransaction(context -> {
            return banRepository.isBannedFromBeach(customerId, beachId, context);
        });
    }
}