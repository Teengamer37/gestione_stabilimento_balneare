package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateAdminRequest;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Admin;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CreateAdminServiceTest extends CreateUserServiceTest<Admin, CreateAdminRequest, CreateAdminService> {
    //implementazione del metodo createService() che restituisce un'istanza di CreateAdminService
    @Override
    protected CreateAdminService createService() {
        //inizializzo il servizio specifico
        return new CreateAdminService(userRepository, transactionManager);
    }

    //implementazione del metodo createValidRequest() che restituisce una request valida per Admin
    @Override
    protected CreateAdminRequest createValidRequest() {
        //factory per una request valida per Admin
        return new CreateAdminRequest("admin@test.com", "admin_user", "Admin", "Test");
    }

    //implementazione del metodo createExpectedUser() che restituisce un Admin fittizio
    @Override
    protected Admin createExpectedUser() {
        //factory per un Admin fittizio
        return new Admin(0, "admin@test.com", "admin_user", "Admin", "Test", true);
    }

    // ==========================================
    // TEST SPECIFICO PER CREATE ADMIN SERVICE
    // ==========================================

    @Test
    void register_CreatesAdmin_WithOtpTrue() {
        //creo request valida
        CreateAdminRequest request = createValidRequest();
        String rawPassword = "temporaryPassword";

        //chiamo il metodo
        service.register(request, rawPassword);

        //verifico che l'Admin salvato nel DB abbia il flag 'OTP' impostato a true
        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(userRepository, times(1)).save(captor.capture(), anyString(), any(TransactionContext.class));
        Admin savedAdmin = captor.getValue();
        assertTrue(savedAdmin.isOTP());
    }
}