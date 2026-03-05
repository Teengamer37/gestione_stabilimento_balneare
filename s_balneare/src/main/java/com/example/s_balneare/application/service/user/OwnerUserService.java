package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.OwnerUserRepository;

//TODO: controllo warning, è finità così avendo tutto implementato in AppUserService

public class OwnerUserService extends AppUserService {
    public OwnerUserService(OwnerUserRepository appUserRepository) {
        super(appUserRepository);
    }
}