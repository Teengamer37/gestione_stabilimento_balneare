package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.AppUser;

//TODO: da riguardare correttezza e possibile eliminazione
public abstract class UserFactory {
    public abstract AppUser createUser(RegistrationRequest request);
}