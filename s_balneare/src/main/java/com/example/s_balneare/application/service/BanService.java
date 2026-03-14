package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.in.BanUseCase;
import com.example.s_balneare.application.port.in.CreateBanCommand;
import com.example.s_balneare.application.port.out.BanRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.moderation.Ban;

import java.time.Instant;

public class BanService implements BanUseCase {
    private final BanRepository banRepository;
    private final TransactionManager transactionManager;

    public BanService(BanRepository banRepository, TransactionManager transactionManager) {
        this.banRepository = banRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public Integer createBan(CreateBanCommand command) {
        return  transactionManager.executeInTransaction(context -> {
            return banRepository.save(
                    new Ban(0, command.bannedId(), command.banType(), command.bannedFromBeachId(), command.adminId(), command.reason(), Instant.now()), context);
        });
    }

    //TODO: finire aggiungi metodi


}
