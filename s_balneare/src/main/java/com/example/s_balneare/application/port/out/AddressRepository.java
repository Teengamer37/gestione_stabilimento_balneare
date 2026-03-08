package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare oggetti di tipo Address.
 * Implementata in:
 * @see com.example.s_balneare.application.service.AddressService AddressService
 */
public interface AddressRepository {
    Integer save(Address address);
    Integer save(Address address, TransactionContext context);
    void update(Address address);
    Optional<Address> findById(Integer id);
    List<Address> findByCity(String city);
    List<Address> findByCountry(String country);
    List<Address> findAll();
    void delete(Integer id);
}