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
    Integer save(Ban ban, TransactionContext context);

    //ricerche
    Optional<Ban> findById(Integer id, TransactionContext context);
    List<Ban> findAll(TransactionContext context);
    boolean isBannedFromBeach(Integer customerId, Integer beachId, TransactionContext context);
    boolean isBannedFromApp(Integer customerId, TransactionContext context);
}
