package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;
import com.example.s_balneare.infrastructure.persistence.jdbc.user.JdbcUserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che gestisce la manipolazione di vari User tra l'app e il Database.
 * <p>Interfaccia estesa in:
 *
 * @see CustomerRepository CustomerRepository
 * @see OwnerRepository OwnerRepository
 * @see AdminRepository AdminRepository
 * @see JdbcUserRepository Implementato in JdbcUserRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface UserRepository<T extends User> {
    //manipolazione
    Integer save(T user, String password, TransactionContext context);
    void update(T user, TransactionContext context);

    //manipolazione password
    Optional<String> findPassword(Integer id, TransactionContext context);
    Optional<String> findPassword(String identifier, TransactionContext context);
    void updatePassword(Integer id, String password, TransactionContext context);

    //ricerche
    Optional<T> findById(Integer id, TransactionContext context);
    Optional<T> findByIdentifier(String identifier, TransactionContext context);
    Optional<T> findByUsername(String username, TransactionContext context);
    Optional<T> findByEmail(String email, TransactionContext context);
    List<T> findAll(TransactionContext context);
}