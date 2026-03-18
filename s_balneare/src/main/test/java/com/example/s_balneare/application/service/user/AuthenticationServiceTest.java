package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.port.out.user.LoginResult;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private UserRepository<User> userRepository;
    @Mock
    private BanRepository banRepository;

    //System Under Test (SUT)
    private AuthenticationService<User> authenticationService;

    //variabili per simulare la gestione delle password
    private final String RAW_PASSWORD = "tenniS1212!";
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        authenticationService = new AuthenticationService<>(userRepository, banRepository, transactionManager);

        //genero un vero hash BCrypt per simulare correttamente la verifica
        hashedPassword = BCrypt.withDefaults().hashToString(12, RAW_PASSWORD.toCharArray());
    }

    // ==========================================
    // TEST PERCORSI DI SUCCESSO
    // ==========================================

    @Test
    void login_Succeeds_ForActiveCustomer() {
        //creo Customer attivo
        String identifier = "customer@test.com";
        Customer activeCustomer = new Customer(1, identifier, "f.feder", "Federico", "Federighi", "+39333", 1, true);
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));
        when(userRepository.findByIdentifier(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(activeCustomer));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(false);

        //login
        LoginResult result = authenticationService.login(identifier, RAW_PASSWORD);

        //verifico che abbia restituito i dati corretti
        assertNotNull(result);
        assertEquals(1, result.userId());
        assertFalse(result.requiresPasswordChange());
        assertEquals(Role.CUSTOMER, result.userRole());
    }

    @Test
    void login_Succeeds_ForAdmin_WithOtp() {
        //creo Admin con OTP attivo
        String identifier = "admin_user";
        Admin adminWithOtp = new Admin(99, "admin@test.com", identifier, "Admin", "Amministratori", true);
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));
        when(userRepository.findByIdentifier(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(adminWithOtp));
        when(banRepository.isBannedFromApp(eq(99), any(TransactionContext.class))).thenReturn(false);

        //login
        LoginResult result = authenticationService.login(identifier, RAW_PASSWORD);

        //verifico che abbia restituito i dati corretti
        assertEquals(99, result.userId());
        assertTrue(result.requiresPasswordChange());
        assertEquals(Role.ADMIN, result.userRole());
    }

    @Test
    void login_Succeeds_ForOwner_WithOtp() {
        //creo Owner con OTP attivo
        String identifier = "owner_user";
        Owner ownerWithOtp = new Owner(99, "owner@test.com", identifier, "Sergio", "Soldi", true, true);
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));
        when(userRepository.findByIdentifier(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(ownerWithOtp));
        when(banRepository.isBannedFromApp(eq(99), any(TransactionContext.class))).thenReturn(false);

        //login
        LoginResult result = authenticationService.login(identifier, RAW_PASSWORD);

        //verifico che abbia restituito i dati corretti
        assertEquals(99, result.userId());
        assertTrue(result.requiresPasswordChange());
        assertEquals(Role.OWNER, result.userRole());
    }

    // ==========================================
    // TEST FALLIMENTI: CREDENZIALI ERRATE
    // ==========================================

    @Test
    void login_ThrowsException_IfPasswordNotFoundInDatabase() {
        //creo mock Utente non registrato
        String identifier = "ghost@test.com";
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.empty());

        //deve lanciare eccezione e non restituire nulla
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.login(identifier, RAW_PASSWORD)
        );
        assertTrue(ex.getMessage().contains("invalid username/email"));
    }

    @Test
    void login_ThrowsException_IfPasswordIsIncorrect() {
        //creo mock Utente registrato, passo però la password sbagliata
        String identifier = "user@test.com";
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));

        //deve lanciare eccezione e non restituire nulla
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.login(identifier, "racchettA1212!")
        );
        assertTrue(ex.getMessage().contains("invalid username or password"));
    }

    @Test
    void login_ThrowsException_IfUserNotFoundAfterPasswordCheck() {
        //creo mock Utente registrato, con password corretta, ma eliminato dal database
        String identifier = "user@test.com";
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));
        when(userRepository.findByIdentifier(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.empty());

        //deve lanciare eccezione e non restituire nulla
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.login(identifier, RAW_PASSWORD)
        );
        assertTrue(ex.getMessage().contains("user not found"));
    }

    // ==========================================
    // TEST FALLIMENTI: BAN E DISATTIVAZIONE
    // ==========================================

    @Test
    void login_ThrowsException_IfUserIsBannedFromApp() {
        //creo Ban per l'utente + mock comportamento DB
        String identifier = "banned@test.com";
        Customer bannedCustomer = new Customer(1, identifier, "banned", "Bad", "Guy", "+39333", 1, true);
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));
        when(userRepository.findByIdentifier(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(bannedCustomer));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(true);

        //deve lanciare eccezione e non restituire nulla
        SecurityException ex = assertThrows(SecurityException.class, () ->
                authenticationService.login(identifier, RAW_PASSWORD)
        );
        assertTrue(ex.getMessage().contains("account banned"));
    }

    @Test
    void login_ThrowsException_IfCustomerIsDeactivated() {
        //creo Customer con account disattivato
        String identifier = "closed@test.com";
        Customer closedCustomer = new Customer(1, identifier, "r.benigni", "Roberto", "Benigni", "+39333", 1, false);
        when(userRepository.findPassword(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(hashedPassword));
        when(userRepository.findByIdentifier(eq(identifier), any(TransactionContext.class))).thenReturn(Optional.of(closedCustomer));
        when(banRepository.isBannedFromApp(eq(1), any(TransactionContext.class))).thenReturn(false);

        //deve lanciare eccezione e non restituire nulla
        SecurityException ex = assertThrows(SecurityException.class, () ->
                authenticationService.login(identifier, RAW_PASSWORD)
        );
        assertTrue(ex.getMessage().contains("account deactivated"));
    }
}