package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

import java.util.Optional;

public interface CustomerUserRepository extends AppUserRepository {
    Optional<AppUser> findByPhoneNumber(String phoneNumber);
}