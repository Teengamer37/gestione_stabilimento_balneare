package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.domain.moderation.ReportTargetType;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    public Integer save(Report report, TransactionContext context);
    public Optional<Report> findById(Integer id, TransactionContext context);
    public List<Report> findAll(TransactionContext context);
    public List<Report> findByReporterId(Integer id, TransactionContext context);
    public List<Report> findByReportedId(Integer id, TransactionContext context);
    public List<Report> findByReportedType(ReportTargetType reportTargetType, TransactionContext context);
    public List<Report> findByStatus(ReportStatus status, TransactionContext context);
    public void updateStatus(ReportStatus status, TransactionContext context, Integer Id);
}
