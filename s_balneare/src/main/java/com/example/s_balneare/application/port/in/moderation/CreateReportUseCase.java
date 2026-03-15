package com.example.s_balneare.application.port.in.moderation;

import com.example.s_balneare.application.service.moderation.CreateReportService;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di manipolazione Report (creazione).
 * <p>Implementata in:
 *
 * @see CreateReportService CreateReportService
 */
public interface CreateReportUseCase {
    Integer createReport(CreateReportCommand command);
}