package com.example.s_balneare.application.port.in.report;

import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;

import java.util.List;

public interface ManageReportUseCase {
    public Report getReport(Integer reportId);
    public List<Report> getPendingReports();
    public List<Report> getUserReports(Integer userId, ReportStatus status);
    public void approveReport(Integer reportId);
    public void rejectReport(Integer reportId);
}
