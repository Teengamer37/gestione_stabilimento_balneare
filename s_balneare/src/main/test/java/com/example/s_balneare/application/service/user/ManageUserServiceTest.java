package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Admin;
import com.example.s_balneare.domain.user.Owner;
import com.example.s_balneare.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class ManageUserServiceTest<T extends User, S extends ManageUserService<T>> {
    //mock della porta di uscita (il database fittizio)
    @Mock
    protected UserRepository<T> userRepository;

    //System Under Test (SUT)
    protected S service;

    //uso la utility per bypassare SQL
    protected TransactionManager transactionManager;

    private final String RAW_PASSWORD = "Password123";
    private final String HASHED_PASSWORD = BCrypt.withDefaults().hashToString(12, RAW_PASSWORD.toCharArray());

    //factory methods per le sottoclassi
    protected abstract S createService();
    protected abstract T createValidUser();

    @BeforeEach
    void setUp() {
        transactionManager = new TestTransactionManager();
        service = createService();

        //setup comune per i mock: findPassword deve sempre ritornare una password crittografata
        when(userRepository.findPassword(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(HASHED_PASSWORD));
    }

    // ==========================================
    // 1. TEST UPDATE PASSWORD
    // ==========================================

    @Test
    void updatePassword_Succeeds_AndDoesNotUpdateOtp_WhenOtpIsFalse() {
        //creo utente
        T user = createValidUser();
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(user));

        //chiamo metodo per aggiornare la password
        service.updatePassword(1, RAW_PASSWORD, "vitaBella!", false);

        //verifico che la password venga aggiornata nel DB
        verify(userRepository).updatePassword(eq(1), anyString(), any());

        //verifico che l'utente non venga ricaricato e aggiornato per l'OTP
        verify(userRepository, never()).update(any((Class<T>) User.class), any(TransactionContext.class));
    }

    @Test
    void updatePassword_Succeeds_AndUpdatesOtp_WhenOtpIsTrue() {
        //creo utente
        T user = createValidUser();
        //se non è Owner o Admin, questo test deve terminare
        if (!(user instanceof Owner || user instanceof Admin)) return;
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(user));

        //chiamo metodo per aggiornare la password
        service.updatePassword(1, RAW_PASSWORD, "bellaVita!", true);

        //verifico che la password sia stata aggiornata
        verify(userRepository).updatePassword(eq(1), anyString(), any());

        //verifico che l'utente sia stato aggiornato per disattivare l'OTP
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass((Class<T>) user.getClass());
        verify(userRepository).update(captor.capture(), any());
        assertFalse(captor.getValue().isOTP());
    }

    @Test
    void updatePassword_ThrowsException_IfOldPasswordIsIncorrect() {
        //mi assicuro che, se non inserisco la vecchia password correttamente, mi venga lanciata un'eccezione...
        assertThrows(IllegalArgumentException.class, () ->
                service.updatePassword(1, "bellaVita!", "vitaBella!", false)
        );
        //...e che non venga aggiornata la password nel DB
        verify(userRepository, never()).updatePassword(any(), any(), any());
    }

    // ==========================================
    // 2. TEST UPDATE DATAS & EMAIL
    // ==========================================

    @Test
    void updateDatas_Succeeds_AndSavesUser() {
        //creo utente
        T user = createValidUser();
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(user));

        //chiamo metodo per aggiornare dati utente
        service.updateDatas(1, "Roberto", "Benigni", "r.benigni");

        // Assert
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass((Class<T>) user.getClass());
        verify(userRepository).update(captor.capture(), any());

        T savedUser = captor.getValue();
        assertEquals("Roberto", savedUser.getName());
        assertEquals("Benigni", savedUser.getSurname());
        assertEquals("r.benigni", savedUser.getUsername());
    }

    @Test
    void updateEmail_Succeeds_AfterVerifyingPassword() {
        // Arrange
        T user = createValidUser();
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(user));

        // Act
        service.updateEmail(1, "new.email@test.com", RAW_PASSWORD);

        // Assert
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass((Class<T>) user.getClass());
        verify(userRepository).update(captor.capture(), any());

        assertEquals("new.email@test.com", captor.getValue().getEmail());
    }

    @Test
    void updateEmail_ThrowsException_IfPasswordIsIncorrect() {
        // Arrange
        T user = createValidUser();
        when(userRepository.findById(eq(1), any())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                service.updateEmail(1, "new.email@test.com", "wrong_password")
        );
        verify(userRepository, never()).update(any(), any());
    }
}