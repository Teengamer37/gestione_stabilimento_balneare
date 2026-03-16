package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.user.Admin;

/**
 * Implementazione dell'interfaccia che permette la manipolazione dell'Admin facendo collaborare l'app Java e il Database.
 * <p>Estende ManageUserService.
 *
 * @see ManageUserService ManageUserService
 */
public class ManageAdminService extends ManageUserService<Admin> {
    public ManageAdminService(UserRepository<Admin> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }
}