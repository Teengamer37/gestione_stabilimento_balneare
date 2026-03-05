package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AdminUserRepository;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;


public class AdminUserService extends AppUserService {
    public AdminUserService(AdminUserRepository appUserRepository) {
        super(appUserRepository);
    }

    @Override //ogni classe figlia deve averlo per poter gestire funzionalità esclusive
    protected AppUser getUserOrThrow(Integer id){
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
        if (!(user instanceof AdminUser appUser)){
            throw new RuntimeException("ERROR: user is not a AdminUser");
        }
        return appUser;
    }
}