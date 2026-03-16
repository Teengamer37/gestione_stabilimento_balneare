package com.example.s_balneare.application.service.moderation;

import com.example.s_balneare.application.port.in.moderation.CreateReportCommand;
import com.example.s_balneare.application.port.in.moderation.CreateReportUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.moderation.ReportRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.domain.moderation.ReportTargetType;
import com.example.s_balneare.domain.user.Customer;
import com.example.s_balneare.domain.user.Owner;
import com.example.s_balneare.domain.user.Role;
import com.example.s_balneare.domain.user.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Implementazione dello Use Case di aggiunta report nel DB:
 * <p>Usa ReportRepository per aggiungere il nuovo Report nel DB;
 * <p>Usa UserRepository per assicurarsi che gli utenti influenzati esistano nel DB;
 * <p>Usa BookingRepository per associare un report ad un prenotazione;
 * <p>Usa BeachRepository per assicurarsi l’esistenza nel DB della spiaggia interessata;
 * <p>Usa BanRepository per verificare se l’utente in questione possa generare il report.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see CreateReportUseCase CreateReportUseCase
 * @see ReportRepository ReportRepository
 * @see UserRepository UserRepository
 * @see BookingRepository BookingRepository
 * @see BeachRepository BeachRepository
 * @see BanRepository BanRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class CreateReportService<T extends User> implements CreateReportUseCase {
    private final ReportRepository reportRepository;
    private final UserRepository<T> userRepository;
    private final BookingRepository bookingRepository;
    private final BeachRepository beachRepository;
    private final BanRepository banRepository;
    private final TransactionManager transactionManager;

    public CreateReportService(ReportRepository reportRepository, UserRepository<T> userRepository, BookingRepository bookingRepository, BeachRepository beachRepository, BanRepository banRepository, TransactionManager transactionManager) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.beachRepository = beachRepository;
        this.banRepository = banRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Aggiunge un nuovo report al DB.
     *
     * @param command oggetto contenente i parametri necessari alla creazione di un nuovo report
     * @return id del report appena creato
     * @throws IllegalArgumentException se gli argomenti passati non esistono nel DB
     * @throws SecurityException        se si prova a lasciare un report in una spiaggia non attiva/bannata/chiusa
     * @throws IllegalStateException    se l'utente non ha un booking passato in stato CONFIRMED in quella spiaggia
     * @see CreateReportCommand CreateReportCommand
     */
    @Override
    public Integer createReport(CreateReportCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //passo 1: cerco se booking esiste nel DB e se è in stato CONFIRMED
            Booking booking = bookingRepository.findById(command.bookingId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: booking does not exist"));
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new IllegalStateException("ERROR: booking not in CONFIRMED state");
            }

            //passo 2: controllo temporale booking
            Instant createdAt = Instant.now();
            LocalDate reportDate = (createdAt).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!reportDate.isAfter(booking.getDate())) {
                throw new IllegalStateException("ERROR: report referring to this booking can be made after " + booking.getDate());
            }

            //passo 3: cerco l’utente creatore nel DB
            T reporterUser = userRepository.findById(command.reporterId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: invalid user"));
            if (reporterUser.getRole() == Role.ADMIN) {
                throw new IllegalStateException("ERROR: invalid type user");
            }

            //passo 4: controllo che l'utente non sia bannato dall'app e che sia attivo
            if (banRepository.isBannedFromApp(command.reporterId(), context)) {
                throw new IllegalStateException("ERROR: user has an active ban");
            }
            if (reporterUser instanceof Customer customer && !customer.isActive()) {
                throw new IllegalStateException("ERROR: user is not active");
            } if (reporterUser instanceof Owner owner && !owner.isActive()) {
                throw new IllegalStateException("ERROR: user is not active");
            }

            //passo 5: recupero dati spiaggia del booking e controllo
            Beach beach = beachRepository.findById(booking.getBeachId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: beach does not exist"));
            if (beach.isClosed()) {
                throw new IllegalStateException("ERROR: beach is closed");
            }

            //passo 6: controllo se booking appartiene al Customer/alla spiaggia
            ReportTargetType reportedType;
            Integer reportedId;

            if (reporterUser instanceof Customer customer) {
                //Customer segnala la spiaggia
                if (!Objects.equals(command.reporterId(), booking.getCustomerId())) {
                    throw new IllegalStateException("ERROR: Customer does not match with the booking");
                }

                //controllo che il Customer che fa il report non sia bannato dalla spiaggia
                if (banRepository.isBannedFromBeach(command.reporterId(), beach.getId(), context)) {
                    throw new IllegalStateException("ERROR: user banned from this beach");
                }
                reportedType = ReportTargetType.BEACH;
                reportedId = beach.getOwnerId();
            } else {
                //Owner segnala Customer
                if (!Objects.equals(command.reporterId(), beach.getOwnerId())) {
                    throw new IllegalStateException("ERROR: this booking does not belong to this beach");
                }
                reportedType = ReportTargetType.USER;
                reportedId = booking.getCustomerId();
            }

            //passo 7: creo nuovo Report
            Report newReport = new Report(
                    0,
                    command.reporterId(),
                    reportedId,
                    reportedType,
                    command.description(),
                    createdAt,
                    ReportStatus.PENDING,
                    command.bookingId()
            );

            //passo 8: salvo nel DB
            return reportRepository.save(newReport, context);
        });
    }
}