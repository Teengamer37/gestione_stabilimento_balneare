package com.example.s_balneare.application.port.in.moderation;

import com.example.s_balneare.application.service.moderation.ManageReportService;
import com.example.s_balneare.domain.moderation.Report;

import java.util.List;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di manipolazione Report.
 * <p>Implementata in:
 *
 * @see ManageReportService CreateReportService
 */
public interface ManageReportUseCase {
    //letture
    Report getReport(Integer reportId);
    List<Report> getPendingReports();
    List<Report> getUserReports(Integer userId);

    //manipolazione report
    void approveReport(Integer reportId);
    void rejectReport(Integer reportId);
}