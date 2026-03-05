package com.example.s_balneare.application.service.user;

//TODO: controllo warning, è finità così avendo tutto implementato in AppUserService

import com.example.s_balneare.application.port.out.AdminUserRepository;


public class AdminUserService extends AppUserService {
    public AdminUserService(AdminUserRepository appUserRepository) {
        super(appUserRepository);
    }
}