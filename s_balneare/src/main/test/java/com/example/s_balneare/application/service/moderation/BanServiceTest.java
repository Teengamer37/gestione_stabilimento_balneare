package com.example.s_balneare.application.service.moderation;

import com.example.s_balneare.application.port.in.moderation.CreateBanCommand;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Ban;
import com.example.s_balneare.domain.moderation.BanType;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private BanRepository banRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository<User> userRepository;
    @Mock
    private BeachRepository beachRepository;

    //System Under Test (SUT)
    private BanService<User> banService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        banService = new BanService<>(
                banRepository, bookingRepository, userRepository, beachRepository, transactionManager
        );
    }

    // ==========================================
    // TEST FLUSSO OWNER
    // ==========================================

    @Test
    void createBan_Owner_ApplicationBan_Succeeds_AndClosesBeachAndAccount() {
        //creo nuovo Ban (assieme a un Owner e a una Beach)
        CreateBanCommand command = new CreateBanCommand(1, BanType.APPLICATION, null, 99, "Mi garba bannarti scemo");
        Owner owner = new Owner(1, "owner@test.com", "marco.c", "Marco", "Marchetto", true, false);
        Beach beach = createValidBeach();
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(owner));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(false);
        when(beachRepository.findByOwnerId(eq(1), any())).thenReturn(Optional.of(beach));
        when(banRepository.save(any(Ban.class), any())).thenReturn(100);

        //chiamo il metodo
        Integer banId = banService.createBan(command);

        //mi assicuro ritorni ID generato dal DB
        assertEquals(100, banId);

        //verifico che l'account dell'Owner sia stato chiuso
        assertFalse(owner.isActive());
        verify(userRepository).update(eq(owner), any(TransactionContext.class));

        //verifico che la spiaggia sia stata chiusa
        assertTrue(beach.isClosed());
        verify(beachRepository).update(eq(beach), any(TransactionContext.class));

        //verifico la cancellazione delle prenotazioni per la spiaggia
        verify(bookingRepository).cancelFutureBookingsForBeach(eq(10), any(LocalDate.class), any(TransactionContext.class));

        //verifico il salvataggio del Ban
        ArgumentCaptor<Ban> banCaptor = ArgumentCaptor.forClass(Ban.class);
        verify(banRepository).save(banCaptor.capture(), any(TransactionContext.class));
        assertEquals(BanType.APPLICATION, banCaptor.getValue().banType());
    }

    @Test
    void createBan_Owner_ThrowsException_IfAlreadyBannedFromApp() {
        //creo nuovo Ban e Owner (però già bannato)
        CreateBanCommand command = new CreateBanCommand(1, BanType.APPLICATION, null, 99, "NUOVO BAN PER TE MUHAHAHA");
        Owner owner = new Owner(1, "owner@test.com", "owner", "nome originale", "cognome originale", true, false);
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(owner));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> banService.createBan(command));
        assertTrue(ex.getMessage().contains("user is already banned"));
        verify(beachRepository, never()).findByOwnerId(any(), any());
        verify(banRepository, never()).save(any(), any());
    }

    @Test
    void createBan_Owner_ThrowsException_IfBanTypeIsBeach() {
        //un Owner non può ricevere un ban limitato a una singola spiaggia, il ban è sempre sull'app
        CreateBanCommand command = new CreateBanCommand(1, BanType.BEACH, 10, 99, "Ti banno da ogni spiaggia hehe");
        Owner owner = new Owner(1, "owner@test.com", "a.sig", "Alberto", "Albertopoli", true, false);
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(owner));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(false);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> banService.createBan(command));
        assertTrue(ex.getMessage().contains("Owner cannot be banned from beach"));
        verify(banRepository, never()).save(any(), any());
    }

    @Test
    void createBan_Owner_ThrowsException_IfBeachIdIsProvided() {
        //anche se è un ban APP, per un Owner non si deve specificare un Beach ID
        CreateBanCommand command = new CreateBanCommand(1, BanType.APPLICATION, 10, 99, "Sono alle prime armi, scusami :(");
        Owner owner = new Owner(1, "owner@test.com", "m.dollars", "Marco", "Soldino", true, false);
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(owner));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(false);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> banService.createBan(command));
        assertTrue(ex.getMessage().contains("Owner cannot be banned from beach"));
        verify(banRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST FLUSSO CUSTOMER
    // ==========================================

    @Test
    void createBan_Customer_ApplicationBan_Succeeds_AndClosesAccount() {
        //creo Ban e Customer
        CreateBanCommand command = new CreateBanCommand(2, BanType.APPLICATION, null, 99, "Ti ho visto per strada e non mi hai dato la precedenza");
        Customer customer = new Customer(2, "cust@test.com", "g.fin", "Gigi", "Francese", "+39111", 1, true);
        when(userRepository.findById(eq(2), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(eq(2), any(TransactionContext.class))).thenReturn(false);
        when(banRepository.save(any(Ban.class), any())).thenReturn(101);

        //eseguo il metodo
        Integer banId = banService.createBan(command);

        //mi assicuro ritorni ID generato dal DB
        assertEquals(101, banId);

        //verifico chiusura account Customer
        assertFalse(customer.isActive());
        verify(userRepository).update(eq(customer), any(TransactionContext.class));

        //verifico cancellazione di tutte le prenotazioni future del Customer
        verify(bookingRepository).cancelFutureBookingsForCustomer(eq(2), any(LocalDate.class), any(TransactionContext.class));
    }

    @Test
    void createBan_Customer_BeachBan_Succeeds_AndCancelsSpecificBookings() {
        //creo Ban, Customer e Beach
        CreateBanCommand command = new CreateBanCommand(2, BanType.BEACH, 10, 99, "Non hai offerto al gestore una patatina");
        Customer customer = new Customer(2, "cust@test.com", "cust", "Name", "Surname", "+39111", 1, true);
        Beach beach = createValidBeach();
        when(userRepository.findById(eq(2), any())).thenReturn(Optional.of(customer));
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(beach));
        when(banRepository.isBannedFromBeach(eq(2), eq(10), any(TransactionContext.class))).thenReturn(false);
        when(banRepository.save(any(Ban.class), any())).thenReturn(102);

        //eseguo il metodo
        Integer banId = banService.createBan(command);

        //mi assicuro ritorni ID generato dal DB
        assertEquals(102, banId);

        //l'account del Customer deve rimanere attivo
        assertTrue(customer.isActive());
        verify(userRepository, never()).update(any(), any());

        //verifico cancellazione prenotazioni solo per quella spiaggia
        verify(bookingRepository).cancelFutureUserBookingsFromBeach(eq(2), eq(10), any(LocalDate.class), any(TransactionContext.class));
    }

    @Test
    void createBan_Customer_ThrowsException_IfAlreadyBannedFromApp() {
        //creo nuovo Ban e Customer (ma Customer già bannato)
        CreateBanCommand command = new CreateBanCommand(2, BanType.APPLICATION, null, 99, "Secondo ban my man, calmiamoci");
        Customer customer = new Customer(2, "cust@test.com", "cust", "Customer", "Customerone", "+39111", 1, true);
        when(userRepository.findById(eq(2), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromApp(eq(2), any(TransactionContext.class))).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> banService.createBan(command));
        assertTrue(ex.getMessage().contains("user is already banned"));
        verify(banRepository, never()).save(any(), any());
    }

    @Test
    void createBan_Customer_ThrowsException_IfAlreadyBannedFromBeach() {
        //creo nuovo Ban e Customer (ma Customer già bannato da spiaggia)
        CreateBanCommand command = new CreateBanCommand(2, BanType.BEACH, 10, 99, "Ti sei ripresentato nella struttura dopo il ban");
        Customer customer = new Customer(2, "cust@test.com", "a.a", "Adriano", "Adriani", "+39111", 1, true);
        when(userRepository.findById(eq(2), any())).thenReturn(Optional.of(customer));
        when(banRepository.isBannedFromBeach(eq(2), eq(10), any(TransactionContext.class))).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> banService.createBan(command));
        assertTrue(ex.getMessage().contains("already banned from this beach"));
        verify(banRepository, never()).save(any(), any());
    }

    @Test
    void createBan_Customer_BeachBan_ThrowsException_IfBeachNotFound() {
        //creo nuovo Ban e Customer, ma Beach non esiste
        CreateBanCommand command = new CreateBanCommand(2, BanType.BEACH, 999, 99, "Cinquantesimo ban su una spiaggia diversa, complimenti!");
        Customer customer = new Customer(2, "cust@test.com", "s.s", "Silvia", "Silvietti", "+393232323232", 1, true);
        when(userRepository.findById(eq(2), any())).thenReturn(Optional.of(customer));
        when(beachRepository.findById(eq(999), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non salvare nulla
        assertThrows(IllegalArgumentException.class, () -> banService.createBan(command));
        verify(banRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST FALLIMENTI GENERICI E ADMIN
    // ==========================================

    @Test
    void createBan_ThrowsException_IfUserIsAdmin() {
        //gli Admin non possono essere bannati
        CreateBanCommand command = new CreateBanCommand(3, BanType.APPLICATION, null, 99, "Non mi piaci come collega");
        Admin admin = new Admin(3, "admin@test.com", "admin", "admin", "giovane", false);
        when(userRepository.findById(eq(3), any())).thenReturn(Optional.of(admin));

        //deve lanciare eccezione e non salvare nulla
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> banService.createBan(command));
        assertTrue(ex.getMessage().contains("This type of user cannot be banned"));
        verify(banRepository, never()).save(any(), any());
    }

    @Test
    void createBan_ThrowsException_IfUserNotFound() {
        //creo nuovo Ban, ma User non esiste
        CreateBanCommand command = new CreateBanCommand(999, BanType.APPLICATION, null, 99, "Test");
        when(userRepository.findById(eq(999), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non salvare nulla
        assertThrows(IllegalArgumentException.class, () -> banService.createBan(command));
        verify(banRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST METODI DI LETTURA
    // ==========================================

    @Test
    void isUserBannedFromApp_ReturnsTrueOrFalse() {
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(true);
        assertTrue(banService.isUserBannedFromApp(1));

        when(banRepository.isBannedFromApp(eq(2), any(TransactionContext.class))).thenReturn(false);
        assertFalse(banService.isUserBannedFromApp(2));
    }

    @Test
    void isCustomerBannedFromBeach_ReturnsTrueOrFalse() {
        when(banRepository.isBannedFromBeach(eq(1), eq(10), any(TransactionContext.class))).thenReturn(true);
        assertTrue(banService.isCustomerBannedFromBeach(1, 10));

        when(banRepository.isBannedFromBeach(eq(2), eq(10), any(TransactionContext.class))).thenReturn(false);
        assertFalse(banService.isCustomerBannedFromBeach(2, 10));
    }

    // ==========================================
    // HELPER
    // ==========================================

    private Beach createValidBeach() {
        return new Beach(10, 1, 1,
                new BeachGeneral("Lido", "Desc", "+3900"),
                null, null, null, "Extra", null, null, true, false);
    }
}