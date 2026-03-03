package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.Address;

import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo Address
public interface AddressRepository {
    int save(Address address);
    void update(Address address);
    Optional<Address> findById(int id);
    List<Address> findByCity(String city);
    List<Address> findByCountry(String country);
    List<Address> findAll();
    void delete(int id);
}