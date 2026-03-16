package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateAdminRequest;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Admin;

/**
 * Implementazione dell'interfaccia che permette la creazione dell‘Admin facendo collaborare l'app Java e il Database.<br>
 * Estende la classe CreateUserService.
 *
 * @see CreateUserService CreateUserService
 * @see CreateAdminRequest CreateAdminRequest
 */
public class CreateAdminService extends CreateUserService<Admin, CreateAdminRequest> {
    public CreateAdminService(UserRepository<Admin> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }

    @Override
    protected Admin registerUser(CreateAdminRequest request, TransactionContext context) {
        return new Admin(
                0,
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname(),
                true
        );
    }
}