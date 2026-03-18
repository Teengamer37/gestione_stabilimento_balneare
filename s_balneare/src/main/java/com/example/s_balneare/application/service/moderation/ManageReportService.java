package com.example.s_balneare.application.service.moderation;

import com.example.s_balneare.application.port.in.moderation.ManageReportUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.moderation.ReportRepository;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementazione dell'interfaccia che permette la navigazione dell'Admin sui Report.<br>
 * Usa ReportRepository per aggiornare i report verificati dall’Admin.<br>
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see ManageReportUseCase ManageReportUseCase
 * @see ReportRepository ReportRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class ManageReportService implements ManageReportUseCase {
    private final ReportRepository reportRepository;
    private final TransactionManager transactionManager;

    public ManageReportService(ReportRepository reportRepository, TransactionManager transactionManager) {
        this.reportRepository = reportRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Recupera Report specifico dal DB.
     *
     * @param reportId ID del report da recuperare
     * @return oggetto Report con quell'ID
     * @throws IllegalArgumentException se il report non esiste nel DB
     */
    @Override
    public Report getReport(Integer reportId) {
        return transactionManager.executeInTransaction(context -> {
            return reportRepository.findById(reportId, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: this report does not exist"));
        });
    }

    /**
     * Recupera tutti i report non ancora verificati dall‘Admin (stato PENDING).
     *
     * @return lista di report non ancora verificati
     */
    @Override
    public List<Report> getPendingReports() {
        return transactionManager.executeInTransaction(context -> {
            return reportRepository.findByStatus(ReportStatus.PENDING, context);
        });
    }

    /**
     * Recupera tutti i Report fatti da quell‘utente o che riguardano uno specifico utente.
     *
     * @param userId ID dell'utente
     * @return lista di report fatti da o riguardanti quell’utente
     */
    @Override
    public List<Report> getUserReports(Integer userId) {
        return transactionManager.executeInTransaction(context -> {
            List<Report> reported = reportRepository.findByReportedId(userId, context);
            List<Report> reporter = reportRepository.findByReporterId(userId, context);

            Set<Report> combined = new LinkedHashSet<>();
            combined.addAll(reported);
            combined.addAll(reporter);

            return new ArrayList<>(combined);
        });
    }

    /**
     * Approva un report.
     *
     * @param reportId ID del report da approvare
     */
    @Override
    public void approveReport(Integer reportId) {
        transactionManager.executeInTransaction(context -> {
            Report report = getReport(reportId);
            report.approve();
            reportRepository.updateStatus(report, context);
        });
    }

    /**
     * Rifiuta un report.
     *
     * @param reportId ID del report da rifiutare
     */
    @Override
    public void rejectReport(Integer reportId) {
        transactionManager.executeInTransaction(context -> {
            Report report = getReport(reportId);
            report.reject();
            reportRepository.updateStatus(report, context);
        });
    }
}