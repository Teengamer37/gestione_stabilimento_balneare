package com.example.s_balneare.application.port.in.beach;

import com.example.s_balneare.application.port.out.beach.BeachSummary;
import com.example.s_balneare.application.service.beach.BrowseBeachService;

import java.util.List;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di ricerca spiagge.
 * Implementata in:
 *
 * @see BrowseBeachService BrowseBeachService
 */
public interface BrowseBeachUseCase {
    List<BeachSummary> getActiveBeaches();
    List<BeachSummary> searchActiveBeaches(String keyword);
}