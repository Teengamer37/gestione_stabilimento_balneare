package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.Address;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo Address
public interface AddressRepository {
    Integer save(Address address);
    Integer save(Address address, Connection connection);
    void update(Address address);
    Optional<Address> findById(Integer id);
    List<Address> findByCity(String city);
    List<Address> findByCountry(String country);
    List<Address> findAll();
    void delete(Integer id);
}