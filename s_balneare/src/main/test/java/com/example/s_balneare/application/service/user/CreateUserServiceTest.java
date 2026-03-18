package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateUserRequest;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) //permette di definire when() senza usarli in ogni test (vedi setUp())
public abstract class CreateUserServiceTest<
        T extends User,
        R extends CreateUserRequest,
        S extends CreateUserService<T, R>> {
    //mock della porta di uscita (il database fittizio)
    @Mock
    protected UserRepository<T> userRepository;

    //System Under Test (SUT)
    protected S service;

    //uso la utility per bypassare SQL
    protected TransactionManager transactionManager;

    //factory methods per le sottoclassi
    protected abstract S createService();
    protected abstract R createValidRequest();
    protected abstract T createExpectedUser();

    @BeforeEach
    void setUp() {
        transactionManager = new TestTransactionManager();
        service = createService();

        //setup comune: il salvataggio deve sempre ritornare un ID fittizio
        when(userRepository.save((T) any(User.class), anyString(), any(TransactionContext.class))).thenReturn(1);
    }

    // ==========================================
    // TEST METODO DI BUSINESS: register()
    // ==========================================

    @Test
    void register_Succeeds_AndHashesPassword() {
        //creo richiesta valida + password
        R request = createValidRequest();
        String rawPassword = "mySecurePassword123!";

        //utilizzo un ArgumentCaptor per catturare l'utente e la password che vengono passati al repository
        ArgumentCaptor<T> userCaptor = ArgumentCaptor.forClass((Class<T>) createExpectedUser().getClass());
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);

        //eseguo metodo in comune
        int userId = service.register(request, rawPassword);

        //verifico di aver ricevuto l'ID generato dal repository
        assertEquals(1, userId);

        //verifico che il metodo save sia stato chiamato esattamente una volta
        verify(userRepository, times(1)).save(userCaptor.capture(), passwordCaptor.capture(), any(TransactionContext.class));

        //verifico che la password salvata sia un hash BCrypt valido e non la password in chiaro
        String savedHashedPassword = passwordCaptor.getValue();
        assertNotNull(savedHashedPassword);
        assertNotEquals(rawPassword, savedHashedPassword);
        assertTrue(savedHashedPassword.startsWith("$2a$12$"));
    }

    @Test
    void register_RollsBack_IfRepositorySaveFails() {
        //creo richiesta valida + password
        R request = createValidRequest();
        String rawPassword = "password";

        //simulo un'eccezione dal database (violazione di vincolo UNIQUE)
        when(userRepository.save(any(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("ERROR: Username is already in use"));

        //deve lanciare eccezione e non salvare nulla
        assertThrows(IllegalArgumentException.class, () ->
                service.register(request, rawPassword)
        );
    }
}