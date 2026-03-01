package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.booking.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//porta (interfaccia) uscente dall'applicazione per i bookings
//(serve per poi implementare queste funzioni nella classe che userà JDBC e MySQL)
public interface BookingRepository {
    int save(Booking booking);
    void delete(int id);
    void update(Booking booking);
    Optional<Booking> findById(int id);

    //trova spot occupati per una data specifica
    List<Integer> findOccupiedSpots(int beachId, LocalDate date);
}