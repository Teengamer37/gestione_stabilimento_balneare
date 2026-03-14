package com.example.s_balneare.domain.booking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
public class Booking {
    //dati booking
    private final Integer id;
    private final Integer beachId;
    private final Integer customerId;   //se prenotazione online
    private final String callerName;    //se prenotazione telefonica
    private final String callerPhone;   //se prenotazione telefonica
    private final LocalDate date;

    //oggetti booking
    private List<Integer> spotIds;
    private int extraSdraio;
    private int extraLettini;
    private int extraSedie;
    private int camerini;

    private BookingParking parking;

    private double totalPrice;

    //stato booking
    private BookingStatus status;

    //costruttore completo (per booking caricati dal DB)
    public Booking(Integer id, Integer beachId, Integer customerId, String callerName, String callerPhone, LocalDate date, List<Integer> spotIds,
                   int extraSdraio, int extraLettini, int extraSedie, int camerini, BookingParking parking, double totalPrice, BookingStatus status) {
        this.id = id;

        //check + assegnazione valore
        checkBeachId(beachId);
        this.beachId = beachId;

        if(customerId != null) {
            checkCustomerId(customerId);
            this.customerId = customerId;
            this.callerName = null;
            this.callerPhone = null;
        } else {
            this.customerId = null;
            checkCallerName(callerName);
            this.callerName = callerName;
            this.callerPhone = validateCallerPhone(callerPhone);
        }


        checkDate(date);
        this.date = date;

        checkSpotIds(spotIds);
        this.spotIds = new ArrayList<>(spotIds);

        checkInitialQuantity(extraSdraio);
        this.extraSdraio = extraSdraio;

        checkInitialQuantity(extraLettini);
        this.extraLettini = extraLettini;

        checkInitialQuantity(extraSedie);
        this.extraSedie = extraSedie;

        checkInitialQuantity(camerini);
        this.camerini = camerini;

        this.parking = parking;

        checkTotalPrice(totalPrice);
        this.totalPrice = totalPrice;

        this.status = status != null ? status : BookingStatus.PENDING;
    }

    //costruttore per salvataggio nuovo booking (senza ID, extra a 0, status PENDING)
    public Booking(Integer beachId, Integer customerId, String callerName, String callerPhone, LocalDate date, List<Integer> spotIds) {
        this(0, beachId, customerId, callerName, callerPhone, date, spotIds, 0, 0, 0, 0, BookingParking.empty(), 0.0, BookingStatus.PENDING);
    }


    // getters (NO SETTERS)
    public Integer getId() {
        return id;
    }
    public Integer getBeachId() {
        return beachId;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public String getCallerName() {
        return callerName;
    }
    public String getCallerPhone() {
        return callerPhone;
    }
    public LocalDate getDate() {
        return date;
    }
    public List<Integer> getSpotIds() {
        return spotIds;
    }
    public int getExtraSdraio() {
        return extraSdraio;
    }
    public int getExtraLettini() {
        return extraLettini;
    }
    public int getExtraSedie() {
        return extraSedie;
    }
    public int getCamerini() {
        return camerini;
    }
    public BookingParking getParking() {
        return parking;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public BookingStatus getStatus() {
        return status;
    }


    //---- METODI DI BUSINESS ----

    /**
     * Conferma booking solo se status == PENDING
     */
    public void confirmBooking() {
        checkStatusIsPending("be confirmed");
        status = BookingStatus.CONFIRMED;
    }

    /**
     * Rifiuta booking solo se status == PENDING
     */
    public void rejectBooking() {
        checkStatusIsPending("be rejected");
        status = BookingStatus.REJECTED;
    }

    /**
     * Cancella booking solo se status == PENDING || CONFIRMED
     */
    public void cancelBooking() {
        checkStatusPendingOrConfirmed("be cancelled");
        status = BookingStatus.CANCELLED;
    }

    /**
     * Aggiorna extra sdraio con controlli
     * @param quantity Numero di sdraio da aggiungere
     */
    public void updateExtraSdraio(int quantity) {
        checkStatusPendingOrConfirmed("add extra quantity");

        extraSdraio = quantity;
        //controllo stato booking (se CONFIRMED, ritorna a PENDING)
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiorna extra lettini con controlli
     * @param quantity Numero di lettini da aggiungere
     */
    public void updateExtraLettini(int quantity) {
        checkStatusPendingOrConfirmed("add extra quantity");

        extraLettini = quantity;
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiorna extra sedie con controlli
     * @param quantity Numero di sedie da aggiungere
     */
    public void updateExtraSedie(int quantity) {
        checkStatusPendingOrConfirmed("add extra quantity");

        extraSedie = quantity;
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiorna camerini con controlli
     * @param quantity Numero di camerini da aggiungere
     */
    public void updateCamerini(int quantity) {
        checkStatusPendingOrConfirmed("add extra quantity");

        camerini = quantity;
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiorna il prezzo totale del Booking
     * @param totalPrice Prezzo totale aggiornato
     */
    public void updateTotalPrice(double totalPrice) {
        checkTotalPrice(totalPrice);
        this.totalPrice = totalPrice;
    }

    /**
     * Aggiorna gli spot e i parcheggi prenotati nel Booking
     * @param updatedSpotIds Lista di spot aggiornati
     * @param updatedParking Parcheggi aggiornati
     */
    public void updateSpotsAndParking(List<Integer> updatedSpotIds, BookingParking updatedParking) {
        checkStatusPendingOrConfirmed("be updated");
        checkSpotIds(updatedSpotIds);

        //verifico che i nuovi parcheggi siano validi
        if (updatedParking.autoPark() < 0 || updatedParking.motoPark() < 0 ||
                updatedParking.bikePark() < 0 || updatedParking.electricPark() < 0) {
            throw new IllegalArgumentException("ERROR: parking values cannot be negative");
        }
        this.parking = updatedParking;

        //rimuovo tutti gli spot, ne aggiungo di nuovi
        this.spotIds.clear();
        this.spotIds.addAll(updatedSpotIds);

        //se prenotazione confermata, ritorna a pending
        revertToPendingIfConfirmed();
    }

    //Helper interno per i business methods
    private void revertToPendingIfConfirmed() {
        if (status == BookingStatus.CONFIRMED) {
            status = BookingStatus.PENDING;
        }
    }


    //---- METODI CHECKERS ----
    private void checkBeachId(Integer beachId) {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: beachId not valid for booking " + id);
    }
    private void checkCustomerId(Integer customerId) {
        if (customerId == null || customerId <= 0) throw new IllegalArgumentException("ERROR: customerId not valid for booking " + id);
    }
    private void checkCallerName(String callerName) {
        if (callerName == null || callerName.isBlank()) throw new IllegalArgumentException("ERROR: callerName not valid for booking " + id);
    }
    private String validateCallerPhone(String callerPhone) {
        if (callerPhone == null) throw new IllegalArgumentException("ERROR: callerPhone cannot be null");
        String cleaned = callerPhone.replaceAll("\\s", "");
        if (cleaned.isBlank()) throw new IllegalArgumentException("ERROR: callerPhone cannot be blank");
        if (cleaned.length() > 50) throw new IllegalArgumentException("ERROR: callerPhone cannot exceed 50 characters");
        if (!cleaned.matches("^\\+\\d+$")) throw new IllegalArgumentException("ERROR: callerPhone not valid");
        return cleaned;
    }
    private void checkDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("ERROR: date cannot be null for booking " + id);
    }
    private void checkSpotIds(List<Integer> spotIds) {
        if (spotIds == null || spotIds.isEmpty()) throw new IllegalArgumentException("ERROR: at least one spot must be selected for booking " + id);
        for (Integer spotId : spotIds) {
            if (spotId == null || spotId <= 0) throw new IllegalArgumentException("ERROR: at least one spotId is not valid for booking " + id);
        }
    }
    private void checkInitialQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("ERROR: extra quantity must be >= 0 for booking " + id);
        }
    }
    private void checkTotalPrice(double totalPrice) {
        if (totalPrice < 0)
            throw new IllegalArgumentException("ERROR: total price must be >= 0 for booking " + id);
    }
    private void checkStatusIsPending(String action) {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be pending in order to " + action);
        }
    }
    private void checkStatusPendingOrConfirmed(String action) {
        if (status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ERROR: booking " + id + " has to be either pending or confirmed in order to " + action);
        }
    }
}