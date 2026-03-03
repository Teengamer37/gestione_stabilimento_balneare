package com.example.s_balneare.application.service.user;

import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcOwnerUserRepository;

public class OwnerUserService extends  AppUserService{
    public OwnerUserService(JdbcOwnerUserRepository jdbcOwnerUserRepository) {super(jdbcOwnerUserRepository);}
}
