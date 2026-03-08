package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.booking.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database su oggetti di tipo Booking.
 * Implementata in:
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.JdbcBookingRepository JdbcBookingRepository
 */
public interface BookingRepository {
    Integer save(Booking booking);
    void delete(Integer id);
    void update(Booking booking);
    Optional<Booking> findById(Integer id);

    //trova spot occupati per una data specifica
    List<Integer> findOccupiedSpots(Integer beachId, LocalDate date);
}