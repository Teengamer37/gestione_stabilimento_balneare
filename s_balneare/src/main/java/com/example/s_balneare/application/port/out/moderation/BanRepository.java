package com.example.s_balneare.application.port.out.moderation;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Ban;
import com.example.s_balneare.infrastructure.persistence.jdbc.moderation.JdbcBanRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare oggetti di tipo Ban.<br>
 * Implementata in:
 *
 * @see JdbcBanRepository JdbcBanRepository
 */
public interface BanRepository {
    //salvataggio
    public Integer save(Ban ban, TransactionContext context);

    //ricerche
    public Optional<Ban> findById(Integer id, TransactionContext context);
    public List<Ban> findAll(TransactionContext context);
    public boolean isBannedFromBeach(Integer customerId, Integer beachId, TransactionContext context);
    public boolean isBannedFromApp(Integer customerId, TransactionContext context);
}
