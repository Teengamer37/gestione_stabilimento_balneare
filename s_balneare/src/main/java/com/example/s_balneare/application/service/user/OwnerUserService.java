package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;
import com.example.s_balneare.domain.user.OwnerUser;

public class OwnerUserService extends AppUserService<OwnerUser> {
    public OwnerUserService(OwnerUserRepository appUserRepository) {
        super(appUserRepository);
    }
}