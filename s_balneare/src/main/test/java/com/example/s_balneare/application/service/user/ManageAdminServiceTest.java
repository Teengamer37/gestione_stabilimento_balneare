package com.example.s_balneare.application.service.user;

import com.example.s_balneare.domain.user.Admin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ManageAdminServiceTest extends ManageUserServiceTest<Admin, ManageAdminService> {
    //implementazione del metodo createService() che restituisce un'istanza di ManageAdminService
    @Override
    protected ManageAdminService createService() {
        //inizializzo il servizio specifico con i suoi repository mockati
        return new ManageAdminService(userRepository, transactionManager);
    }

    @Override
    protected Admin createValidUser() {
        //factory per un Admin fittizio
        return new Admin(1, "admin@test.com", "admin_user", "Admin", "Test", true);
    }

    // ==========================================
    // TEST SPECIFICI PER MANAGE ADMIN SERVICE
    // ==========================================

    @Test
    void inherited_updatePassword_WorksForAdminAndDisablesOtp() {
        //creo Admin attivo con OTP attivo
        Admin admin = createValidUser();
        when(userRepository.findById(eq(1), any())).thenReturn(java.util.Optional.of(admin));

        //chiamo il metodo
        service.updatePassword(1, "Password123", "newAdminPassword", true);

        //verifico che la password sia stata aggiornata
        verify(userRepository).updatePassword(eq(1), anyString(), any());

        //verifico che l'Admin passato al metodo update abbia OTP=false
        org.mockito.ArgumentCaptor<Admin> captor = org.mockito.ArgumentCaptor.forClass(Admin.class);
        verify(userRepository).update(captor.capture(), any());
        assertFalse(captor.getValue().isOTP());
    }
}