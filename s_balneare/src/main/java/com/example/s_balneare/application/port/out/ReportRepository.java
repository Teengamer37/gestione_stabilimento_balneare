package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;

import java.util.List;
import java.util.Optional;

// I report sono letti e giudicati dagli admin i quali devono poter accedere a tutti i report in qualsiasi stato  di qualsiasi utente.
// Devono poter visualizzare tutti i report di un utente in qualsiasi stato.
// Devono poter visualizzare tutti i report in qualsiasi stato

public interface ReportRepository {
    public Integer save(Report report, TransactionContext context);
    public Optional<Report> findById(Integer id, TransactionContext context);
    public List<Report> findAll(TransactionContext context);
    public List<Report> findByReporterId(Integer id, TransactionContext context);
    public List<Report> findByReporterIdAndStatus(Integer reporterId, ReportStatus status, TransactionContext context);
    public List<Report> findByReportedId(Integer id, TransactionContext context);
    public List<Report> findByReportedIdAndStatus(Integer reportedId, ReportStatus status, TransactionContext context);
    public List<Report> findByStatus(ReportStatus status, TransactionContext context);
    public void updateStatus(Report report, TransactionContext context);
}
