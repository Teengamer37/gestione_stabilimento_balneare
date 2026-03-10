package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.User;

//TODO: da riguardare correttezza e possibile eliminazione
public abstract class UserFactory {
    public abstract User createUser(RegistrationRequest request);
}