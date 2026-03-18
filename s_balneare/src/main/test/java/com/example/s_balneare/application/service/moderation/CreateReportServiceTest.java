package com.example.s_balneare.application.service.moderation;

import com.example.s_balneare.application.port.in.moderation.CreateReportCommand;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.moderation.ReportRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportTargetType;
import com.example.s_balneare.domain.user.Admin;
import com.example.s_balneare.domain.user.Customer;
import com.example.s_balneare.domain.user.Owner;
import com.example.s_balneare.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateReportServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository<User> userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BeachRepository beachRepository;
    @Mock
    private BanRepository banRepository;

    //System Under Test (SUT)
    private CreateReportService<User> createReportService;

    private final int BOOKING_ID = 100;
    private final int CUSTOMER_ID = 10;
    private final int OWNER_ID = 20;
    private final int BEACH_ID = 30;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        createReportService = new CreateReportService<>(
                reportRepository, userRepository, bookingRepository,
                beachRepository, banRepository, transactionManager
        );
    }

    // ==========================================
    // TEST CREAZIONE REPORT CON SUCCESSO
    // ==========================================

    @Test
    void createReport_Succeeds_WhenCustomerReportsBeach() {
        //creo nuovo Report, Booking, Customer e Beach
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "Mi hanno lanciato acqua addosso senza motivo", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Customer customer = new Customer(CUSTOMER_ID, "c@test.com", "val.val", "Valentino", "Valentini", "+392645", 1, true);
        Beach beach = createValidBeach(BEACH_ID, OWNER_ID, false);
        when(bookingRepository.findById(eq(BOOKING_ID), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(CUSTOMER_ID), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(eq(CUSTOMER_ID), any())).thenReturn(false);
        when(beachRepository.findById(eq(BEACH_ID), any())).thenReturn(Optional.of(beach));
        when(banRepository.isBannedFromBeach(eq(CUSTOMER_ID), eq(BEACH_ID), any())).thenReturn(false);
        when(reportRepository.save(any(Report.class), any())).thenReturn(999);

        //chiamo il metodo che crea il report
        Integer reportId = createReportService.createReport(command);

        //verifico che tutto sia stato inserito correttamente nel database
        assertEquals(999, reportId);
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture(), any());
        Report savedReport = captor.getValue();
        assertEquals(ReportTargetType.BEACH, savedReport.getReportedType());
        assertEquals(OWNER_ID, savedReport.getReportedId());
        assertEquals("Mi hanno lanciato acqua addosso senza motivo", savedReport.getDescription());
    }

    @Test
    void createReport_Succeeds_WhenOwnerReportsCustomer() {
        //creo nuovo Report, Booking, Owner e Beach
        CreateReportCommand command = new CreateReportCommand(OWNER_ID, "Mi ha augurato il fallimento", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Owner owner = new Owner(OWNER_ID, "o@test.com", "giu.giu", "Giustino", "Giustinini", true, false);
        Beach beach = createValidBeach(BEACH_ID, OWNER_ID, false);
        when(bookingRepository.findById(eq(BOOKING_ID), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(OWNER_ID), any())).thenReturn(Optional.of(owner));
        when(banRepository.isBannedFromApp(eq(OWNER_ID), any())).thenReturn(false);
        when(beachRepository.findById(eq(BEACH_ID), any())).thenReturn(Optional.of(beach));
        when(reportRepository.save(any(Report.class), any())).thenReturn(888);

        //chiamo il metodo che crea il report
        Integer reportId = createReportService.createReport(command);

        //verifico che tutto sia stato inserito correttamente nel database
        assertEquals(888, reportId);
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture(), any());
        Report savedReport = captor.getValue();
        assertEquals(ReportTargetType.USER, savedReport.getReportedType());
        assertEquals(CUSTOMER_ID, savedReport.getReportedId());
        assertEquals("Mi ha augurato il fallimento", savedReport.getDescription());
    }

    // ==========================================
    // TEST VALIDAZIONE PARAMETRI
    // ==========================================

    @Test
    void createReport_ThrowsException_IfBookingNotFound() {
        //creo nuovo Report, ma Beach non esiste
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "La spiaggia non era pulita, il gestore si rifiutava di pulirla", BOOKING_ID);
        when(bookingRepository.findById(eq(BOOKING_ID), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non salvare nulla
        assertThrows(IllegalArgumentException.class, () -> createReportService.createReport(command));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfBookingNotConfirmedOrPending() {
        //creo nuovo Report, ma il Booking non è mai stato confermato
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "BRUTTA SPIAGGIA!!!", BOOKING_ID);
        Booking pendingBooking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.REJECTED, LocalDate.now().minusDays(2));
        when(bookingRepository.findById(eq(BOOKING_ID), any())).thenReturn(Optional.of(pendingBooking));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("CONFIRMED or PENDING state"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfBookingIsInFutureOrToday() {
        //creo nuovo Report, ma la data del booking è quella odierna
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "Mi hanno cacciato dalla spiaggia", BOOKING_ID);
        Booking todayBooking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now());
        when(bookingRepository.findById(eq(BOOKING_ID), any())).thenReturn(Optional.of(todayBooking));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("after"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfReporterIsAdmin() {
        //creo nuovo Report, ma il Reporter è un Admin
        CreateReportCommand command = new CreateReportCommand(1, "Mi hanno rubato il telefono", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Admin admin = new Admin(1, "a@a.com", "admin", "Admin", "Amministrati", false);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(admin));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("invalid type user"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfReporterIsBannedFromApp() {
        //creo nuovo Report, ma il Reporter è bannato dall'app
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "Il gestore non è simpatico", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Customer customer = new Customer(CUSTOMER_ID, "c@c.com", "gennaro.g", "Gennaro", "Gennarini", "+39123", 1, true);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(CUSTOMER_ID), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(eq(CUSTOMER_ID), any())).thenReturn(true); // BANNED

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("active ban"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfReporterIsInactive() {
        //creo nuovo Report, ma l'account del Reporter è disattivato
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "Il gestore sostiene che la vita non è bella", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Customer customer = new Customer(CUSTOMER_ID, "c@c.com", "roberto.b", "Roberto", "Benigni", "+39123", 1, false);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(CUSTOMER_ID), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("not active"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfBeachIsClosed() {
        //creo nuovo Report, ma la spiaggia è chiusa
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "Non ha detto 'Che bella cosa che hai detto?'", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Customer customer = new Customer(CUSTOMER_ID, "c@c.com", "p.ruff", "Paolo", "Ruffini", "+39123", 1, true);
        Beach closedBeach = createValidBeach(BEACH_ID, OWNER_ID, true);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any(), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(beachRepository.findById(eq(BEACH_ID), any())).thenReturn(Optional.of(closedBeach));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("beach is closed"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfCustomerDoesNotMatchBooking() {
        //creo nuovo Report, ma il Customer non ha mai fatto quel Booking
        CreateReportCommand command = new CreateReportCommand(999, "Non gli piace la fisica", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Customer customer = new Customer(999, "c@c.com", "v.schiett", "Vincenzo", "Schettini", "+39123", 1, true);
        Beach beach = createValidBeach(BEACH_ID, OWNER_ID, false);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any(), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(beachRepository.findById(any(), any())).thenReturn(Optional.of(beach));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("does not match with the booking"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfCustomerBannedFromBeach() {
        //creo nuovo Report, ma il Customer è bannato dalla spiaggia
        CreateReportCommand command = new CreateReportCommand(CUSTOMER_ID, "Non ha vinto Sanremo hahahahah", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Customer customer = new Customer(CUSTOMER_ID, "c@c.com", "olly", "Federico", "Olivieri", "+39123", 1, true);
        Beach beach = createValidBeach(BEACH_ID, OWNER_ID, false);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any(), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(beachRepository.findById(any(), any())).thenReturn(Optional.of(beach));
        //Customer bannato da questa spiaggia
        when(banRepository.isBannedFromBeach(eq(CUSTOMER_ID), eq(BEACH_ID), any())).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("user banned from this beach"));
        verify(reportRepository, never()).save(any(), any());
    }

    @Test
    void createReport_ThrowsException_IfOwnerDoesNotMatchBeach() {
        //creo nuovo Report, ma Owner non possiede questa spiaggia
        CreateReportCommand command = new CreateReportCommand(999, "Non ha giocato a tennis (racchetta)", BOOKING_ID);
        Booking booking = createValidBooking(CUSTOMER_ID, BEACH_ID, BookingStatus.CONFIRMED, LocalDate.now().minusDays(2));
        Owner owner = new Owner(999, "o@o.com", "j.sinner", "Jannik", "Sinner", true, false);
        Beach beach = createValidBeach(BEACH_ID, OWNER_ID, false);
        when(bookingRepository.findById(any(), any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any(), any())).thenReturn(Optional.of(owner));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(beachRepository.findById(any(), any())).thenReturn(Optional.of(beach));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createReportService.createReport(command));
        assertTrue(ex.getMessage().contains("booking does not belong to this beach"));
        verify(reportRepository, never()).save(any(), any());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private Booking createValidBooking(int customerId, int beachId, BookingStatus status, LocalDate date) {
        return new Booking(BOOKING_ID, beachId, customerId, null, null, date,
                List.of(1), 0, 0, 0, 0, new BookingParking(0, 0, 0), 10.0, status);
    }

    private Beach createValidBeach(int beachId, int ownerId, boolean closed) {
        return new Beach(beachId, ownerId, 1, new BeachGeneral("Name", "Desc", "+39123"),
                null, null, null, "", null, null, !closed, closed);
    }
}