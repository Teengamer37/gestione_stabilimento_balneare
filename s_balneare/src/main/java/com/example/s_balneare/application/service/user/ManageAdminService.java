package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.user.Admin;

public class ManageAdminService extends ManageUserService<Admin> {
    public ManageAdminService(UserRepository<Admin> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }
}