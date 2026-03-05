package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;
import com.example.s_balneare.domain.user.OwnerUser;

public class OwnerUserService extends AppUserService {
    public OwnerUserService(OwnerUserRepository appUserRepository) {
        super(appUserRepository);
    }

    @Override //ogni classe figlia deve averlo per poter gestire funzionalità esclusive
    protected OwnerUser getUserOrThrow(Integer id){
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
        if (!(user instanceof OwnerUser appUser)){
            throw new RuntimeException("ERROR: user is not a OwnerUser");
        }
        return appUser;
    }
}