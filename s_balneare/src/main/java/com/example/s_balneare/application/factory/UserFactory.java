package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;

public abstract class UserFactory {
    // Il metodo ora è unico e atomico
    public abstract AppUser createUser(RegistrationRequest request);
}