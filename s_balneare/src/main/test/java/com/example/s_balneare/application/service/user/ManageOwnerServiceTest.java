package com.example.s_balneare.application.service.user;

import com.example.s_balneare.domain.user.Owner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ManageOwnerServiceTest extends ManageUserServiceTest<Owner, ManageOwnerService> {
    //implementazione del metodo createService() che restituisce un'istanza di ManageOwnerService
    @Override
    protected ManageOwnerService createService() {
        //inizializzo il servizio specifico con i suoi repository mockati
        return new ManageOwnerService(userRepository, transactionManager);
    }

    //implementazione del metodo createValidUser() che restituisce un Owner fittizio
    @Override
    protected Owner createValidUser() {
        //factory per un Owner fittizio
        return new Owner(1, "owner@test.com", "owner_user", "Proprietario", "Test", true, true);
    }

    // ==========================================
    // TEST SPECIFICI PER MANAGE OWNER SERVICE
    // ==========================================

    @Test
    void inherited_updatePassword_WorksForOwnerAndDisablesOtp() {
        //creo Owner attivo con OTP attivo
        Owner owner = createValidUser();
        when(userRepository.findById(eq(1), any())).thenReturn(java.util.Optional.of(owner));

        //chiamo il metodo
        service.updatePassword(1, "Password123", "newStrongPassword", true);

        //verifico che la password sia stata aggiornata
        verify(userRepository).updatePassword(eq(1), anyString(), any());

        //verifico che l'Owner passato al metodo update abbia OTP=false
        org.mockito.ArgumentCaptor<Owner> captor = org.mockito.ArgumentCaptor.forClass(Owner.class);
        verify(userRepository).update(captor.capture(), any());
        assertFalse(captor.getValue().isOTP());
    }
}