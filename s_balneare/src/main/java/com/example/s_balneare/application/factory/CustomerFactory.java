package com.example.s_balneare.application.factory;

//TODO: fatta

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

public class CustomerFactory extends UserFactory{

    @Override
    public AppUser CreateUser(RegistrationRequest request) {
        CustomerUser user =  new CustomerUser(request.getId()) ;
        user.setPhoneNumber(request.getPhoneNumber());
        user.setActive(request.isActive());
        user.setAddressId(request.getAddressId());
        return user;
    }
}