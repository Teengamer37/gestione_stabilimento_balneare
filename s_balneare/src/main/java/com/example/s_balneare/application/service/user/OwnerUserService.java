package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;
import com.example.s_balneare.domain.user.OwnerUser;

public class OwnerUserService extends AppUserService<OwnerUser> {

    public OwnerUserService(AppUserRepository<OwnerUser> appUserRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        super(appUserRepository, addressRepository, transactionManager);
    }

    @Override
    protected OwnerUser registerUser(RegistrationRequest request, TransactionContext context) {
        return new OwnerUser(0, request.getEmail(), request.getUsername(), request.getName(), request.getSurname());
    }
}