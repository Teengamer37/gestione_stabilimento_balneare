package com.example.s_balneare.application.service.report;

import com.example.s_balneare.application.port.in.report.ManageReportUseCase;
import com.example.s_balneare.application.port.out.ReportRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;

import java.util.List;

/**
 * Implementazione dell'interfaccia che permette la navigazione dell'admin sui report
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class ManageReportService implements ManageReportUseCase {
    private final ReportRepository reportRepository;
    private final TransactionManager transactionManager;

    public ManageReportService(ReportRepository reportRepository, TransactionManager transactionManager) {
        this.reportRepository = reportRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public Report getReport(Integer reportId) {
        return transactionManager.executeInTransaction(context -> {
            return reportRepository.findById(reportId, context)
                    .orElseThrow(()->new IllegalArgumentException("ERROR: This report does not exist"));
        });
    }

    @Override
    public List<Report> getPendingReports() {
        return transactionManager.executeInTransaction(context -> {
            return reportRepository.findByStatus(ReportStatus.PENDING, context);
        });
    }

    @Override
    public List<Report> getUserReports(Integer userId) {
        transactionManager.executeInTransaction(context -> {
            List<Report> reported = reportRepository.findByReportedId(userId, context);
            List<Report> reporter = reportRepository.findByReporterId(userId, context);
            reported.addAll(reporter);
            return reported;
        });
        return List.of();
    }

    @Override
    public void approveReport(Integer reportId) {
        transactionManager.executeInTransaction(context -> {
            Report report = getReport(reportId);
            report.approve();
            reportRepository.updateStatus(report, context);
        });
    }

    @Override
    public void rejectReport(Integer reportId) {
        transactionManager.executeInTransaction(context -> {
            Report report = getReport(reportId);
            report.reject();
            reportRepository.updateStatus(report, context);
        });
    }
}
