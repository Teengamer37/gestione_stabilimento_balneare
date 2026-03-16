package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.user.Owner;

/**
 * Implementazione dell'interfaccia che permette la manipolazione dell'Owner facendo collaborare l'app Java e il Database.
 * <p>Estende ManageUserService.
 *
 * @see ManageUserService ManageUserService
 */
public class ManageOwnerService extends ManageUserService<Owner> {
    public ManageOwnerService(UserRepository<Owner> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }
}