package com.example.s_balneare.application.port.in.report;

import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.domain.moderation.ReportTargetType;

import java.time.Instant;

public record CreateReportCommand(
        Integer reporterId,
        String description,
        Instant createdAt,
        Integer bookingId
) {
}
