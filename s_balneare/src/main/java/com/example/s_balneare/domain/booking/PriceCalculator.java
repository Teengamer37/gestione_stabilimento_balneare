package com.example.s_balneare.domain.booking;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.Pricing;
import com.example.s_balneare.domain.beach.Season;
import com.example.s_balneare.domain.beach.ZoneTariff;
import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.SpotType;
import com.example.s_balneare.domain.layout.Zone;

/// È un Domain Service che calcola il prezzo totale di una prenotazione
public class PriceCalculator {
    /**
     * Calcola il prezzo totale di una prenotazione basandosi sul tariffario della spiaggia.
     *
     * @param booking La prenotazione (con le quantità e gli spot selezionati)
     * @param beach   L'aggregato Beach completo (contenente Stagioni, Zone, Tariffe e Spot fisici)
     * @return il costo totale
     * @throws IllegalStateException    se il Booking è corrotto/non corrisponde alla spiaggia
     * @throws IllegalArgumentException se il Booking contiene Spot che non esistono nella spiaggia
     */
    public static double calculateTotal(Booking booking, Beach beach) {
        //passo 1: trovo la stagione attiva per la data della prenotazione
        Season activeSeason = beach.getSeasons().stream()
                .filter(s -> s.includes(booking.getDate()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("ERROR: no season active for the date " + booking.getDate()));

        Pricing pricing = activeSeason.pricing();
        double total = 0.0;

        //passo 2: calcolo costo degli extra
        total += booking.getExtraSdraio() * pricing.priceSdraio();
        total += booking.getExtraLettini() * pricing.priceLettino();
        total += booking.getExtraSedie() * pricing.priceSedia();
        total += booking.getCamerini() * pricing.priceCamerino();

        //passo 3: calcolo costo Parking (tariffa fissa per veicolo basata su priceParking)
        if (booking.getParking() != null) {
            BookingParking p = booking.getParking();
            int totalVehicles = p.autoPark() + p.motoPark() + p.bikePark() + p.electricPark();
            total += totalVehicles * pricing.priceParking();
        }

        //passo 4: calcolo costo degli Spot
        for (Integer spotId : booking.getSpotIds()) {
            String spotZoneName = null;
            SpotType spotType = null;

            //trovo a quale Zone appartiene questo Spot e di che tipo è
            //i break servono per uscire prima dai cicli for appena trovato lo Spot e la Zone desiderata
            for (Zone zone : beach.getZones()) {
                for (Spot spot : zone.spots()) {
                    if (spot.id() != null && spot.id().equals(spotId)) {
                        spotZoneName = zone.name();
                        spotType = spot.type();
                        break;
                    }
                }
                if (spotZoneName != null) break;
            }

            //passaggio sicurezza: se il frontend ha inviato un ID spot che non esiste fisicamente nella spiaggia
            if (spotZoneName == null || spotType == null) {
                throw new IllegalArgumentException("ERROR: spot ID " + spotId + " does not exist in the layout of this beach");
            }

            //trovo il prezzo per quella specifica Zona in questa Stagione
            final String targetZoneName = spotZoneName;
            ZoneTariff tariff = activeSeason.zoneTariffs().stream()
                    .filter(zt -> zt.zoneName().equals(targetZoneName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("ERROR: no tariff defined for zone '" + targetZoneName + "' in season '" + activeSeason.name() + "'"));

            //aggiungo il prezzo in base allo SpotType (Ombrellone o Tenda)
            if (spotType == SpotType.UMBRELLA) {
                total += tariff.priceOmbrellone();
            } else if (spotType == SpotType.TENT) {
                total += tariff.priceTenda();
            }
        }
        return total;
    }
}