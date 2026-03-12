package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateOwnerRequest;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Owner;

public class CreateOwnerService extends CreateUserService<Owner, CreateOwnerRequest> {

    public CreateOwnerService(UserRepository<Owner> userRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
    }

    @Override
    protected Owner registerUser(CreateOwnerRequest request, TransactionContext context) {
        return new Owner(
                0,
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname(),
                true
        );
    }
}