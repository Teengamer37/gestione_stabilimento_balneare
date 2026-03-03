package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.booking.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo Booking
public interface BookingRepository {
    int save(Booking booking);
    void delete(int id);
    void update(Booking booking);
    Optional<Booking> findById(int id);

    //trova spot occupati per una data specifica
    List<Integer> findOccupiedSpots(int beachId, LocalDate date);
}