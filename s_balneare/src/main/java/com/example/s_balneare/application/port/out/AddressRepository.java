package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare oggetti di tipo Address.
 * Implementata in JdbcAddressRepository
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.JdbcAddressRepository JdbcAddressRepository
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public interface AddressRepository {
    Integer save(Address address, TransactionContext context);
    void update(Address address, TransactionContext context);
    Optional<Address> findById(Integer id, TransactionContext context);
    List<Address> findByCity(String city, TransactionContext context);
    List<Address> findByCountry(String country, TransactionContext context);
    List<Address> findAll(TransactionContext context);
    void delete(Integer id, TransactionContext context);
}