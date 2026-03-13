package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Ban;

import java.util.List;
import java.util.Optional;

public interface BanRepository {
    public Integer save(Ban ban, TransactionContext context);
    public Optional<Ban> findById(Integer id, TransactionContext context);
    public List<Ban> findAll(TransactionContext context);
    //Un utente può avere più ban, ed una spiaggia può avere più utenti bannati
    public List<Ban> findByBannedId(Integer id, TransactionContext context);
    public List<Ban> findByBannedFromBeachId(Integer bannedFromBeachId, TransactionContext context);
    public List<Ban> findByBanType(Integer banType, TransactionContext context);
}
