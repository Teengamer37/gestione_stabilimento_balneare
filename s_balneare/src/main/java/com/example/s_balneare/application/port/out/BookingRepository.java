package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.booking.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo Booking
public interface BookingRepository {
    Integer save(Booking booking);
    void delete(Integer id);
    void update(Booking booking);
    Optional<Booking> findById(Integer id);

    //trova spot occupati per una data specifica
    List<Integer> findOccupiedSpots(Integer beachId, LocalDate date);
}