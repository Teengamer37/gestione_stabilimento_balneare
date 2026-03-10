package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.OwnerUserRequest;
import com.example.s_balneare.application.port.out.user.AppUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.OwnerUser;

public class OwnerUserService extends AppUserService<OwnerUser, OwnerUserRequest> {

    public OwnerUserService(AppUserRepository<OwnerUser> appUserRepository, TransactionManager transactionManager) {
        super(appUserRepository, transactionManager);
    }

    @Override
    protected OwnerUser registerUser(OwnerUserRequest request, TransactionContext context) {
        return new OwnerUser(0, request.getEmail(), request.getUsername(), request.getName(), request.getSurname());
    }
}