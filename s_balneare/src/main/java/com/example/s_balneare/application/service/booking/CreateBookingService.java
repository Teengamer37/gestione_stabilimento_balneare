package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.CreateBookingCommand;
import com.example.s_balneare.application.port.in.booking.CreateBookingUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.AvailabilityQuery;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.Parking;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.booking.PriceCalculator;

/**
 * Implementazione dello Use Case di aggiunta prenotazione (fatta da un Customer) nel DB:
 * <p>Interagisce con BeachRepository per trovare la spiaggia e per verificare che gli Spot appartengono alla spiaggia stessa;
 * <p>Successivamente usa AvailabilityQuery per trovare i posti occupati di quel giorno in quella spiaggia;
 * <p>Usa BanRepository per verificare lo stato degli utenti/spiagge;
 * <p>Viene alla fine usata BookingRepository per salvare la nuova prenotazione.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see CreateBookingUseCase CreateBookingUseCase
 * @see BeachRepository BeachRepository
 * @see BookingRepository BookingRepository
 * @see AvailabilityQuery AvailabilityQuery
 * @see BanRepository BanRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 * @see CreateBookingCommand CreateBookingCommand
 */
public class CreateBookingService implements CreateBookingUseCase {
    private final BeachRepository beachRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilityQuery availabilityQuery;
    private final BanRepository banRepository;
    private final TransactionManager transactionManager;

    public CreateBookingService(BeachRepository beachRepository,
                                BookingRepository bookingRepository,
                                AvailabilityQuery availabilityQuery,
                                BanRepository banRepository,
                                TransactionManager transactionManager) {
        this.beachRepository = beachRepository;
        this.bookingRepository = bookingRepository;
        this.availabilityQuery = availabilityQuery;
        this.banRepository = banRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public Integer createBooking(CreateBookingCommand command) {
        return transactionManager.executeInTransaction(context -> {
            //passo 1: cerco la spiaggia tramite ID
            Beach beach = beachRepository.findById(command.beachId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Beach not found"));
            //controllo se spiaggia attiva
            if (!beach.isActive()) {
                throw new IllegalStateException("ERROR: cannot create bookings for an inactive beach");
            }

            //passo 2: controllo utente non sia bannato dalla spiaggia o dall'app
            if (banRepository.isBannedFromApp(command.customerId(), context)) {
                throw new IllegalStateException("ERROR: customer banned from the app");
            }
            if (banRepository.isBannedFromBeach(command.customerId(), command.beachId(), context)) {
                throw new IllegalStateException("ERROR: customer banned from the beach");
            }

            //passo 3: recupero i posti parcheggio occupati in quel giorno
            BookedParkingSpaces booked = availabilityQuery.getBookedParking(command.beachId(), command.date(), null, context);
            BookingParking requestedParking = new BookingParking(
                    command.autoPark(), command.motoPark(), command.bikePark(), command.electricPark()
            );

            //passo 4: controllo se ho posti liberi per questa prenotazione (ovvero se posti disponibili >= posti richiesti)
            if (!isParkingAvailable(beach.getParking(), booked, requestedParking)) {
                throw new IllegalStateException("ERROR: not enough parking capacity for the selected date");
            }

            //passo 5: controllo disponibilità extra oggetti
            BookedInventory bookedInv = availabilityQuery.getBookedInventory(beach.getId(), command.date(), null, context);
            BeachInventory cap = beach.getBeachInventory();
            if ((cap.countExtraSdraio() - bookedInv.sdraio() < command.extraSdraio()) ||
                    (cap.countExtraLettini() - bookedInv.lettini() < command.extraLettini()) ||
                    (cap.countExtraSedie() - bookedInv.sedie() < command.extraSedie()) ||
                    (cap.countCamerini() - bookedInv.camerini() < command.camerini())) {
                throw new IllegalStateException("ERROR: not enough inventory items available");
            }

            //passo 6: verifico se gli spot appartengono effettivamente alla spiaggia
            if (!beachRepository.doSpotsBelongToBeach(command.beachId(), command.spotIds(), context)) {
                throw new SecurityException("ERROR: one or more spots do not belong to the beach");
            }

            //passo 7: creo la prenotazione
            Booking booking = new Booking(
                    0,
                    command.beachId(),
                    command.customerId(),
                    null,
                    null,
                    command.date(),
                    command.spotIds(),
                    command.extraSdraio(),
                    command.extraLettini(),
                    command.extraSedie(),
                    command.camerini(),
                    requestedParking,
                    0.0,
                    BookingStatus.PENDING
            );

            //passo 8: calcolo il prezzo totale della prenotazione
            booking.updateTotalPrice(PriceCalculator.calculateTotal(booking, beach));

            //passo 9: salvo nel database
            return bookingRepository.save(booking, context);
        });
    }

    /**
     * Metodo privato che va a fare questo calcolo:
     * numero parcheggi totali - numero parcheggi prenotati >= numero parcheggi richiesti.
     * <p>Questa operazione viene fatta per ciascuna categoria di parcheggio.
     *
     * @param capacity  Parcheggio della spiaggia
     * @param booked    Parcheggi prenotati in quella data
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