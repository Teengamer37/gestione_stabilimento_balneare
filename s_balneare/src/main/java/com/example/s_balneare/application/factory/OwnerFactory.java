package com.example.s_balneare.application.factory;

//TODO: fatta

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.OwnerUser;

public class OwnerFactory extends UserFactory {

    @Override
    public AppUser CreateUser(RegistrationRequest request) {
        return new OwnerUser(request.getId()) ;
    }
}