package com.example.s_balneare.application.service.report;

import com.example.s_balneare.application.port.in.report.CreateReportCommand;
import com.example.s_balneare.application.port.in.report.CreateReportUseCase;
import com.example.s_balneare.application.port.out.BanRepository;
import com.example.s_balneare.application.port.out.ReportRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.domain.moderation.ReportTargetType;
import com.example.s_balneare.domain.user.Customer;
import com.example.s_balneare.domain.user.Role;
import com.example.s_balneare.domain.user.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

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
     *Aggiunge un nuovo report al DB
     * @param command, parametri necessari alla creazione di un nuovo report
     * @return id del report appena creato
     * @throws IllegalArgumentException se gli argomenti passati non esistono nel DB
     * @throws SecurityException se si prova a lasciare un report in una spiaggia non attiva/bannata/chiusa
     * @throws IllegalStateException se l'utente non ha un booking passato in stato CONFIRMED in quella spiaggia
     * @see TransactionManager TransactionManager per le transazioni SQL
     */
    @Override
    public Integer createReport(CreateReportCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //1: Validazione booking
            Booking booking = bookingRepository.findById(command.bookingId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: This booking does not exist"));

            if (booking.getStatus() != BookingStatus.CONFIRMED){
                throw new IllegalStateException("ERROR: This booking does not exist");
            }

            //2: Controllo temporale booking
            Instant createdAt = Instant.now();
            LocalDate reportDate = (createdAt).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!reportDate.isAfter(booking.getDate())) {
                throw new IllegalStateException("ERROR: Reports are available from the next day");
            }

            //3: Identificazione reporter
            T reporterUser = userRepository.findById(command.reporterId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Invalid user"));
            if(reporterUser.getRole() == Role.ADMIN) {
                throw new IllegalStateException("ERROR: Invalid type user");
            }
            //Controllo l'utente non sia bannato dall'app, identico che controllare se l'utente è attivo
            //Uso ban per controllare così il controllo viene effettutato sia se è un customer che un owner
            if (banRepository.isBannedFromApp(command.reporterId(), context)) {
                throw new IllegalStateException("ERROR: The user has been banned");
            }
            //4: Recupero dati spiaggia del booking e controllo
            Beach beach = beachRepository.findById(booking.getBeachId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach does not exist"));
            //Controllo spiaggia non sia chiusa, se è disattivata perchè in manutenzione posso lasciare una recensione
            if (beach.isClosed()) {
                throw new IllegalStateException("ERROR: Beach is not active or closed");
            }

            //5: logica di business e sicurezza
            ReportTargetType reportedType;
            Integer reportedId;

            if (reporterUser instanceof Customer customer){
                //Customer segnala la spiaggia
                //Controllo segnali spiagge inerenti alla prenotazione
                if(!Objects.equals(command.reporterId(), booking.getCustomerId())) {
                    throw new IllegalStateException("ERROR: You are not the owner of this reservation");
                }
                //Controllo che il customer che fa il report non sia bannato dalla spiaggia
                if (banRepository.isBannedFromBeach(command.reporterId(), beach.getId(), context)) {
                    throw new IllegalStateException("ERROR: Your are banned from this beach");
                }
                reportedType = ReportTargetType.BEACH;
                reportedId = beach.getOwnerId();
            } else{ //Owner segnala il customer
                if (!Objects.equals(command.reporterId(), beach.getOwnerId())){
                    throw new IllegalStateException("ERROR: You can only report bookings of your own beach");
                }
                reportedType = ReportTargetType.USER;
                reportedId = booking.getCustomerId();
            }

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

            return reportRepository.save(newReport, context);
        });
    }
}
