package com.example.s_balneare.application.port.in.beach;

import com.example.s_balneare.application.service.beach.CloseBeachService;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di chiusura spiaggia.
 * Implementata in:
 *
 * @see CloseBeachService CloseBeachService
 */
public interface CloseBeachUseCase {
    void closeBeach(Integer beachId, Integer ownerId);
}