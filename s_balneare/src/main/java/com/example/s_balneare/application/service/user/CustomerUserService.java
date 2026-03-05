package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.CustomerUserRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

//TODO: da fare

public class CustomerUserService extends AppUserService {
    public CustomerUserService(CustomerUserRepository appUserRepository) {
        super(appUserRepository);
    }

    public void updateTelephoneNumber(Integer id, String phoneNumber) {
        CustomerUser appUser = getUserOrThrow(id);
        appUser.changePhoneNumber(phoneNumber);
        appUserRepository.update(appUser);
    }

    public void setCustomerActive(Integer id, Boolean active) {
        CustomerUser appUser = getUserOrThrow(id);
        appUser.setActive(active);
        appUserRepository.update(appUser);
    }

     @Override
     protected CustomerUser getUserOrThrow(Integer id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
        if (!(user instanceof CustomerUser appUser)){
             throw new RuntimeException("ERROR: user is not a CustomerUser");
        }
        return appUser;
     }

}