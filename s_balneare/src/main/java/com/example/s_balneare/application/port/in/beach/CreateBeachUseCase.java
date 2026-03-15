package com.example.s_balneare.application.port.in.beach;

import com.example.s_balneare.application.service.beach.CreateBeachService;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di creare una spiaggia.
 * <p>Essa ha bisogno di un oggetto Address e di un oggetto Beach.
 * <p>Implementata in:
 *
 * @see CreateBeachService CreateBeachService
 */
public interface CreateBeachUseCase {
    Integer createBeach(CreateBeachCommand command);
}