package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.CreateBookingCommand;
import com.example.s_balneare.application.port.in.booking.CreateBookingUseCase;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.booking.ParkingAvailabilityQuery;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.Parking;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;

/**
 * Implementazione dello Use Case di aggiunta prenotazione nel DB:
 * Interagisce con BeachRepository per trovare la spiaggia e per verificare che gli Spot appartengono alla spiaggia stessa;
 * Successivamente usa ParkingAvailabilityQuery per trovare i posti occupati di quel giorno in quella spiaggia;
 * Viene alla fine usata BookingRepository per salvare la nuova prenotazione.
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata
 *
 * @see CreateBookingUseCase CreateBookingUseCase
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager
 * @see BeachRepository BeachRepository
 * @see com.example.s_balneare.application.port.out.booking.BookingRepository BookingRepository
 * @see com.example.s_balneare.application.port.out.booking.ParkingAvailabilityQuery ParkingAvailabilityQuery
 */
public class CreateBookingService implements CreateBookingUseCase {

    private final BeachRepository beachRepository;
    private final BookingRepository bookingRepository;
    private final ParkingAvailabilityQuery parkingQuery;
    private final TransactionManager transactionManager;

    public CreateBookingService(BeachRepository beachRepository,
                                BookingRepository bookingRepository,
                                ParkingAvailabilityQuery parkingQuery,
                                TransactionManager transactionManager) {
        this.beachRepository = beachRepository;
        this.bookingRepository = bookingRepository;
        this.parkingQuery = parkingQuery;
        this.transactionManager = transactionManager;
    }

    @Override
    public Integer createBooking(CreateBookingCommand command) {
        return transactionManager.executeInTransaction(context -> {

            //passo 1: cerco la spiaggia tramite ID
            Beach beach = beachRepository.findById(command.beachId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found"));

            //passo 2: recupero i posti parcheggio occupati in quel giorno
            BookedParkingSpaces booked = parkingQuery.getBookedSpacesForDate(command.beachId(), command.date(), context);

            BookingParking requestedParking = new BookingParking(
                    command.autoPark(), command.motoPark(), command.bikePark(), command.electricPark()
            );

            //passo 3: controllo se ho posti liberi per questa prenotazione (ovvero se posti disponibili >= posti richiesti)
            if (!isParkingAvailable(beach.getParking(), booked, requestedParking)) {
                throw new IllegalStateException("ERROR: Not enough parking capacity for the selected date");
            }

            //passo 4: verifico se gli spot appartengono effettivamente alla spiaggia
            if (!beachRepository.doSpotsBelongToBeach(command.beachId(), command.spotIds(), context)) {
                throw new SecurityException("ERROR: one or more spots do not belong to the beach");
            }

            //passo 5: creo la prenotazione
            Booking booking = new Booking(
                    0,
                    command.beachId(),
                    command.customerId(),
                    command.date(),
                    command.spotIds(),
                    command.extraSdraio(),
                    command.extraLettini(),
                    command.extraSedie(),
                    command.camerini(),
                    requestedParking,
                    BookingStatus.PENDING
            );

            //passo 6: salvo nel database
            return bookingRepository.save(booking, context);
        });
    }

    /**
     * Metodo privato che va a fare questo calcolo:
     * numero parcheggi totali - numero parcheggi prenotati >= numero parcheggi richiesti.
     * Questa operazione viene fatta per ciascuna categoria di parcheggio.
     * @param capacity Parcheggio della spiaggia
     * @param booked Parcheggi prenotati in quella data
     * @param requested Parcheggi richiesti
     * @return una booleana che risponde alla domanda "ci sono parcheggi disponibili per questa prenotazione?"
     */
    private boolean isParkingAvailable(Parking capacity, BookedParkingSpaces booked, BookingParking requested) {
        return (capacity.nAutoPark() - booked.bookedAuto() >= requested.autoPark()) &&
                (capacity.nMotoPark() - booked.bookedMoto() >= requested.motoPark()) &&
                (capacity.nBikePark() - booked.bookedBike() >= requested.bikePark()) &&
                (capacity.nElectricPark() - booked.bookedElectric() >= requested.electricPark());
    }
}