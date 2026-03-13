package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.CreateBookingCommand;
import com.example.s_balneare.application.port.in.booking.CreateBookingUseCase;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.booking.AvailabilityQuery;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.Parking;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;

/**
 * Implementazione dello Use Case di aggiunta prenotazione nel DB:
 * Interagisce con BeachRepository per trovare la spiaggia e per verificare che gli Spot appartengono alla spiaggia stessa;
 * Successivamente usa AvailabilityQuery per trovare i posti occupati di quel giorno in quella spiaggia;
 * Viene alla fine usata BookingRepository per salvare la nuova prenotazione.
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata
 *
 * @see CreateBookingUseCase CreateBookingUseCase
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager
 * @see BeachRepository BeachRepository
 * @see com.example.s_balneare.application.port.out.booking.BookingRepository BookingRepository
 * @see AvailabilityQuery AvailabilityQuery
 */
public class CreateBookingService implements CreateBookingUseCase {

    private final BeachRepository beachRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilityQuery availabilityQuery;
    private final TransactionManager transactionManager;

    public CreateBookingService(BeachRepository beachRepository,
                                BookingRepository bookingRepository,
                                AvailabilityQuery availabilityQuery,
                                TransactionManager transactionManager) {
        this.beachRepository = beachRepository;
        this.bookingRepository = bookingRepository;
        this.availabilityQuery = availabilityQuery;
        this.transactionManager = transactionManager;
    }

    //TODO: aggiungere controllo se utente bannato dalla spiaggia! (dopo aver fatto BanRepository)
    //TODO: modificare tutta la logica create per UC-05 e UC-06 lato Owner
    @Override
    public Integer createBooking(CreateBookingCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //passo 1: cerco la spiaggia tramite ID
            Beach beach = beachRepository.findById(command.beachId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found"));

            //passo 2: recupero i posti parcheggio occupati in quel giorno
            BookedParkingSpaces booked = availabilityQuery.getBookedParking(command.beachId(), command.date(), null, context);
            BookingParking requestedParking = new BookingParking(
                    command.autoPark(), command.motoPark(), command.bikePark(), command.electricPark()
            );

            //passo 3: controllo se ho posti liberi per questa prenotazione (ovvero se posti disponibili >= posti richiesti)
            if (!isParkingAvailable(beach.getParking(), booked, requestedParking)) {
                throw new IllegalStateException("ERROR: not enough parking capacity for the selected date");
            }

            //passo 4: controllo disponibilità extra oggetti
            BookedInventory bookedInv = availabilityQuery.getBookedInventory(beach.getId(), command.date(), null, context);
            BeachInventory cap = beach.getBeachInventory();
            if ((cap.countExtraSdraio() - bookedInv.sdraio() < command.extraSdraio()) ||
                    (cap.countExtraLettini() - bookedInv.lettini() < command.extraLettini()) ||
                    (cap.countExtraSedie() - bookedInv.sedie() < command.extraSedie()) ||
                    (cap.countCamerini() - bookedInv.camerini() < command.camerini())) {
                throw new IllegalStateException("ERROR: not enough inventory items available");
            }

            //passo 5: verifico se gli spot appartengono effettivamente alla spiaggia
            if (!beachRepository.doSpotsBelongToBeach(command.beachId(), command.spotIds(), context)) {
                throw new SecurityException("ERROR: one or more spots do not belong to the beach");
            }

            //passo 6: creo la prenotazione
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

            //passo 7: salvo nel database
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