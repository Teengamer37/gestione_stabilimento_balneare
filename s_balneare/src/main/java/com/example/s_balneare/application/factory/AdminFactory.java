package com.example.s_balneare.application.factory;

//TODO: fatta

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;

public class AdminFactory extends UserFactory {

    @Override
    public AppUser CreateUser(RegistrationRequest request) {
        return new AdminUser(request.getId()) ;
    }
}