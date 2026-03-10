package com.example.s_balneare.application.port.in.beach;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di creare una spiaggia.
 * Essa ha bisogno di un oggetto Address e di un oggetto Beach.
 * Implementata in:
 * @see com.example.s_balneare.application.service.beach.CreateBeachService CreateBeachService
 */
public interface CreateBeachUseCase {
    Integer createBeach(CreateBeachCommand command);
}