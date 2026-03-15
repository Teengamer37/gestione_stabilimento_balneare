package com.example.s_balneare.application.port.out.moderation;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Ban;

import java.util.List;
import java.util.Optional;

public interface BanRepository {
    public Integer save(Ban ban, TransactionContext context);
    public Optional<Ban> findById(Integer id, TransactionContext context);
    public List<Ban> findAll(TransactionContext context);
    public boolean isBannedFromBeach(Integer customerId, Integer beachId, TransactionContext context);
    public boolean isBannedFromApp(Integer customerId, TransactionContext context);
}
