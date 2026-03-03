package com.example.s_balneare.application.service.user;

import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcAdminUserRepository;

public class AdmineUserService extends AppUserService{
    public AdmineUserService(JdbcAdminUserRepository jdbcAdminUserRepository) {super(jdbcAdminUserRepository);}
}
