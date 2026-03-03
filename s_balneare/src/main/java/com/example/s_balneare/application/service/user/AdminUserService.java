package com.example.s_balneare.application.service.user;

import com.example.s_balneare.domain.user.CustomerUser;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcAdminUserRepository;

//TODO: da fare

public class AdminUserService extends AppUserService {
    public AdminUserService(JdbcAdminUserRepository jdbcAdminUserRepository) {super(jdbcAdminUserRepository);}
}