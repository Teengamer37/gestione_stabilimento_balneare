package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.application.port.out.CustomerUserRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.user.CustomerUser;

//TODO: da fare

public class CustomerUserService extends AppUserService {
    public CustomerUserService(CustomerUserRepository appUserRepository) {
        super(appUserRepository);
    }

    public void updateTelephoneNumber(CustomerUser user, String phoneNumber) {
        //
        appUserRepository.update(user, null);
    }
    public void updateAddress(CustomerUser user, Address address) {
        //chiamo update su address
    }
}