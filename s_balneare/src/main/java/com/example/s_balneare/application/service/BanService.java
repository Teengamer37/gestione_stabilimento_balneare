package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.in.BanUseCase;
import com.example.s_balneare.application.port.in.CreateBanCommand;
import com.example.s_balneare.application.port.out.BanRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.moderation.Ban;

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
                    new Ban(0, command.bannedId(), command.banType(), command.bannedFromBeachId(), command.adminId(), command.reason()), context);
        });
    }

    //TODO: non so quali altri metodi poter inserire?? getBan? manca getUser?
    // sia in user che in ban non ci sono metodi nel service che utilizzano i find del jdbc


}
