package com.example.s_balneare.domain.booking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    //dati booking
    private final Integer id;
    private final Integer beachId;
    private final Integer customerId;
    private final LocalDate date;

    //oggetti booking
    private final List<Integer> spotIds;
    private int extraSdraio;
    private int extraLettini;
    private int extraSedie;
    private int camerini;

    //stato booking
    private BookingStatus status;

    //costruttore completo (per booking caricati dal DB)
    public Booking(Integer id, Integer beachId, Integer customerId, LocalDate date, List<Integer> spotIds,
                   int extraSdraio, int extraLettini, int extraSedie, int camerini, BookingStatus status) {
        this.id = id;

        //check + assegnazione valore
        checkBeachId(beachId);
        this.beachId = beachId;

        checkCustomerId(customerId);
        this.customerId = customerId;

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

        this.status = status != null ? status : BookingStatus.PENDING;
    }

    //costruttore per salvataggio nuovo booking (senza ID, extra a 0, status PENDING)
    public Booking(Integer beachId, Integer customerId, LocalDate date, List<Integer> spotIds) {
        this(0, beachId, customerId, date, spotIds, 0, 0, 0, 0, BookingStatus.PENDING);
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
     * Aggiungi extra sdraio con controlli
     * @param quantity Numero di sdraio da aggiungere
     * @param availableSdraio Numero di sdraio disponibili
     */
    public void addExtraSdraio(int quantity, int availableSdraio) {
        checkStatusPendingOrConfirmed("add extra quantity");
        checkAddedQuantity(quantity, availableSdraio);

        extraSdraio += quantity;
        //controllo stato booking (se CONFIRMED, ritorna a PENDING)
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiungi extra lettini con controlli
     * @param quantity Numero di lettini da aggiungere
     * @param availableLettini Numero di lettini disponibili
     */
    public void addExtraLettini(int quantity, int availableLettini) {
        checkStatusPendingOrConfirmed("add extra quantity");
        checkAddedQuantity(quantity, availableLettini);

        extraLettini += quantity;
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiungi extra sedie con controlli
     * @param quantity Numero di sedie da aggiungere
     * @param availableSedie Numero di sedie disponibili
     */
    public void addExtraSedie(int quantity, int availableSedie) {
        checkStatusPendingOrConfirmed("add extra quantity");
        checkAddedQuantity(quantity, availableSedie);

        extraSedie += quantity;
        revertToPendingIfConfirmed();
    }

    /**
     * Aggiungi camerini con controlli
     * @param quantity Numero di camerini da aggiungere
     * @param availableCamerini Numero di camerini disponibili
     */
    public void addCamerini(int quantity, int availableCamerini) {
        checkStatusPendingOrConfirmed("add extra quantity");
        checkAddedQuantity(quantity, availableCamerini);

        camerini += quantity;
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
    private void checkAddedQuantity(int quantity, int available) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("ERROR: quantity must be > 0");
        }
        if (quantity > available) {
            throw new IllegalArgumentException("ERROR: quantity must be <= available quantity");
        }
    }
    private void checkStatusIsPending(String action) {
        // Corretto il bug originale (era == invece di !=)
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