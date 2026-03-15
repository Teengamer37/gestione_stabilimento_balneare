package com.example.s_balneare.application.port.out.common;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcAddressRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare oggetti di tipo Address.
 * <p>Implementata in:
 *
 * @see JdbcAddressRepository JdbcAddressRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface AddressRepository {
    //manipolazione
    Integer save(Address address, TransactionContext context);
    void update(Address address, TransactionContext context);
    void delete(Integer id, TransactionContext context);

    //ricerche
    Optional<Address> findById(Integer id, TransactionContext context);
    List<Address> findByCity(String city, TransactionContext context);
    List<Address> findByCountry(String country, TransactionContext context);
    List<Address> findAll(TransactionContext context);
}