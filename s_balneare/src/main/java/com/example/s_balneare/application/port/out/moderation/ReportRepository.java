package com.example.s_balneare.application.port.out.moderation;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcReportRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare oggetti di tipo Report.
 * <p>Implementata in:
 *
 * @see JdbcReportRepository JdbcReportRepository
 */
public interface ReportRepository {
    //manipolazione
    Integer save(Report report, TransactionContext context);
    void updateStatus(Report report, TransactionContext context);

    //ricerche
    Optional<Report> findById(Integer id, TransactionContext context);
    List<Report> findAll(TransactionContext context);
    List<Report> findByReporterId(Integer id, TransactionContext context);
    List<Report> findByReportedId(Integer id, TransactionContext context);
    List<Report> findByStatus(ReportStatus status, TransactionContext context);
}