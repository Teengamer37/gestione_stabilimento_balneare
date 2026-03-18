package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateOwnerRequest;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Owner;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CreateOwnerServiceTest extends CreateUserServiceTest<Owner, CreateOwnerRequest, CreateOwnerService> {
    //implementazione del metodo createService() che restituisce un'istanza di CreateOwnerService
    @Override
    protected CreateOwnerService createService() {
        //inizializzo il servizio specifico con i suoi repository mockati
        return new CreateOwnerService(userRepository, transactionManager);
    }

    //implementazione del metodo createValidRequest() che restituisce una request valida per Owner
    @Override
    protected CreateOwnerRequest createValidRequest() {
        //factory per creare una request di creazione Owner valida
        return new CreateOwnerRequest("owner@test.com", "owner_user", "Proprietario", "Test");
    }

    //implementazione del metodo createExpectedUser() che restituisce un Owner fittizio
    @Override
    protected Owner createExpectedUser() {
        //factory per creare un Owner fittizio
        return new Owner(0, "owner@test.com", "owner_user", "Proprietario", "Test", true, true);
    }

    // ==========================================
    // TEST SPECIFICO PER CREATE OWNER SERVICE
    // ==========================================

    @Test
    void register_CreatesOwner_WithActiveAndOtpTrue() {
        //creo request valida
        CreateOwnerRequest request = createValidRequest();
        String rawPassword = "password";

        //chiamo il metodo
        service.register(request, rawPassword);

        //verifico che l'Owner salvato nel DB abbia i flag 'active' e 'OTP' a true
        ArgumentCaptor<Owner> captor = ArgumentCaptor.forClass(Owner.class);
        verify(userRepository, times(1)).save(captor.capture(), anyString(), any(TransactionContext.class));
        Owner savedOwner = captor.getValue();
        assertTrue(savedOwner.isActive());
        assertTrue(savedOwner.isOTP());
    }
}