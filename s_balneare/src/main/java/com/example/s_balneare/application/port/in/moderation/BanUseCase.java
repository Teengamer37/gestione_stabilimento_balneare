package com.example.s_balneare.application.port.in.moderation;

/**
 * Interfaccia che definisce lo Use Case di creazione ban.
 * Aggiunge metodi di ricerca se un utente ha ricevuto ban applicazione o ban spiaggia.
 */
public interface BanUseCase {
    //creazione
    Integer createBan(CreateBanCommand command);

    //controlli ban
    boolean isUserBannedFromApp(Integer customerId);
    boolean isCustomerBannedFromBeach(Integer customerId, Integer beachId);
}