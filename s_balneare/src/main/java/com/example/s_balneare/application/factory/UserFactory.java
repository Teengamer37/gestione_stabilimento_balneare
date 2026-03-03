package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;

public abstract class UserFactory {

    public abstract AppUser CreateUser(RegistrationRequest request);

    public AppUser NewUser(RegistrationRequest request){
        // delega creazione utente
        AppUser user = CreateUser(request);
        // applica logica comune
        fillCommonField(user, request);
        return user;
    }

    protected void fillCommonField(AppUser user, RegistrationRequest request) {
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setUsername(request.getUsername());
    }
}