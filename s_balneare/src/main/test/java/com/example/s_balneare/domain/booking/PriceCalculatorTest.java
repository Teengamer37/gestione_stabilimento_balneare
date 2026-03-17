package com.example.s_balneare.domain.booking;

import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.SpotType;
import com.example.s_balneare.domain.layout.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {
    private Beach testBeach;
    private final LocalDate summerDate = LocalDate.of(2026, 7, 15);
    private final LocalDate winterDate = LocalDate.of(2026, 1, 15);

    @BeforeEach
    void setUp() {
        //configurazione prezzi (Pricing) per gli extra e parcheggio
        //lettino=5.0, sdraio=4.0, sedia=3.0, parking=10.0, camerino=20.0
        Pricing summerPricing = Pricing.create(5.0, 4.0, 3.0, 10.0, 20.0);

        //configurazione tariffe Zone (ZoneTariffs)
        List<ZoneTariff> tariffs = List.of(
                ZoneTariff.create("Prima Fila", 20.0, 35.0), // ombrellone=20, tenda=35
                ZoneTariff.create("Seconda Fila", 15.0, 25.0) // ombrellone=15, tenda=25
        );

        //configurazione stagione (Season)
        Season summerSeason = Season.builder()
                .name("Estate 2026")
                .startDate(LocalDate.of(2026, 6, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .pricing(summerPricing)
                .zoneTariffs(tariffs)
                .build();

        //configurazione layout fisico (Spot e Zone)
        Spot spot1 = new Spot(1, SpotType.UMBRELLA, 1, 1);
        Spot spot2 = new Spot(2, SpotType.TENT, 1, 2);
        Spot spot3 = new Spot(3, SpotType.UMBRELLA, 2, 1);
        Spot spotOrphan = new Spot(4, SpotType.UMBRELLA, 3, 1);

        Zone zone1 = new Zone("Prima Fila", List.of(spot1, spot2));
        Zone zone2 = new Zone("Seconda Fila", List.of(spot3));
        Zone zone3 = new Zone("Zona Misteriosa", List.of(spotOrphan));

        // 5. Creazione della Spiaggia (Beach)
        BeachGeneral general = new BeachGeneral("Lido Test", "I Marco sono ben accetti", "+39000000");
        testBeach = new Beach(
                1, 1, 1, general, BeachInventory.empty(), BeachServices.none(), Parking.empty(),
                "", List.of(summerSeason), List.of(zone1, zone2, zone3), false, false
        );
    }

    // ==========================================
    // TEST CALCOLO CON SUCCESSO
    // ==========================================

    @Test
    void calculateTotal_ReturnsCorrectSum_WithAllElementsIncluded() {
        // Preparazione: un booking con spot, extra e parcheggi
        BookingParking parking = new BookingParking(1, 1, 1);

        Booking booking = new Booking(1, 1, 100, null, null, summerDate,
                List.of(1, 2), //spot 1 (ombrellone prima fila = 20) + spot 2 (tenda prima fila = 35) -> 55.0
                1,             //extra sdraio (1 * 4.0 = 4.0)
                2,             //extra lettini (2 * 5.0 = 10.0)
                3,             //extra sedie (3 * 3.0 = 9.0)
                1,             //extra camerini (1 * 20.0 = 20.0)
                parking, 0.0, BookingStatus.PENDING);

        //calcolo totale atteso:
        //spots: 20 + 35 = 55
        //extras: 4 + 10 + 9 + 20 = 43
        //parking: 30
        //totale = 55 + 43 + 30 = 128.0
        double total = PriceCalculator.calculateTotal(booking, testBeach);

        assertEquals(128.0, total);
    }

    @Test
    void calculateTotal_ReturnsCorrectSum_WithOnlySpotsAndNoExtrasOrParking() {
        //booking solo con uno spot, senza extra e senza parcheggio (null)
        Booking booking = new Booking(2, 1, 100, null, null, summerDate,
                List.of(3), //spot 3 (ombrellone seconda fila = 15) -> 15.0
                0, 0, 0, 0, null, 0.0, BookingStatus.PENDING);

        double total = PriceCalculator.calculateTotal(booking, testBeach);

        assertEquals(15.0, total);
    }

    // ==========================================
    // TEST ECCEZIONI
    // ==========================================

    @Test
    void calculateTotal_ThrowsException_IfDateHasNoActiveSeason() {
        Booking booking = new Booking(3, 1, 100, null, null, winterDate,
                List.of(1), 0, 0, 0, 0, null, 0.0, BookingStatus.PENDING);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                PriceCalculator.calculateTotal(booking, testBeach)
        );
        assertTrue(ex.getMessage().contains("no season active"));
    }

    @Test
    void calculateTotal_ThrowsException_IfSpotDoesNotExistInBeach() {
        Booking booking = new Booking(4, 1, 100, null, null, summerDate,
                List.of(999), 0, 0, 0, 0, null, 0.0, BookingStatus.PENDING);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                PriceCalculator.calculateTotal(booking, testBeach)
        );
        assertTrue(ex.getMessage().contains("does not exist in the layout"));
    }

    @Test
    void calculateTotal_ThrowsException_IfZoneHasNoTariffInActiveSeason() {
        Booking booking = new Booking(5, 1, 100, null, null, summerDate,
                List.of(4), 0, 0, 0, 0, null, 0.0, BookingStatus.PENDING);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                PriceCalculator.calculateTotal(booking, testBeach)
        );
        assertTrue(ex.getMessage().contains("no tariff defined for zone"));
    }
}