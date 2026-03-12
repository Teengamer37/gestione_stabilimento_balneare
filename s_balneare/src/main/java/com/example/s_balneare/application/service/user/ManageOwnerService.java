package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.user.Owner;

public class ManageOwnerService extends ManageUserService<Owner> {
    public ManageOwnerService(UserRepository<Owner> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }
}