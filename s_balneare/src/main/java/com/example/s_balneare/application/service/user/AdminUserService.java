package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.AdminUserRequest;
import com.example.s_balneare.application.port.out.user.AppUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AdminUser;


public class AdminUserService extends AppUserService<AdminUser, AdminUserRequest> {

    public AdminUserService(AppUserRepository<AdminUser> appUserRepository, TransactionManager transactionManager) {
        super(appUserRepository, transactionManager);
    }

    @Override
    protected AdminUser registerUser(AdminUserRequest request, TransactionContext context) {
        return new AdminUser(0, request.getEmail(), request.getUsername(), request.getName(), request.getSurname());
    }
}