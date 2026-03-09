package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.AdminUserRepository;
import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;


public class AdminUserService extends AppUserService<AdminUser> {

    public AdminUserService(AppUserRepository<AdminUser> appUserRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        super(appUserRepository, addressRepository, transactionManager);
    }

    @Override
    protected AdminUser registerUser(RegistrationRequest request, TransactionContext context) {
        return new AdminUser(0, request.getEmail(), request.getUsername(), request.getName(), request.getSurname());
    }
}