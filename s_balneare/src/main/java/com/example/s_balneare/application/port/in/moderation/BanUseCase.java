package com.example.s_balneare.application.port.in.moderation;

import com.example.s_balneare.application.service.moderation.BanService;

/**
 * Interfaccia che definisce lo Use Case di creazione ban.<br>
 * Aggiunge metodi di ricerca se un utente ha ricevuto ban applicazione o ban spiaggia.<br>
 * Implementata in:
 *
 * @see BanService BanService
 */
public interface BanUseCase {
    //creazione
    Integer createBan(CreateBanCommand command);

    //controlli ban
    boolean isUserBannedFromApp(Integer customerId);
    boolean isCustomerBannedFromBeach(Integer customerId, Integer beachId);
}