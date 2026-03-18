package com.example.s_balneare.application.service.moderation;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.moderation.ReportRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.moderation.Report;
import com.example.s_balneare.domain.moderation.ReportStatus;
import com.example.s_balneare.domain.moderation.ReportTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageReportServiceTest {
    //mock della porta di uscita (il database fittizio)
    @Mock
    private ReportRepository reportRepository;

    //System Under Test (SUT)
    private ManageReportService manageReportService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        manageReportService = new ManageReportService(reportRepository, transactionManager);
    }

    // ==========================================
    // TEST DI LETTURA
    // ==========================================

    @Test
    void getReport_Succeeds_WhenReportExists() {
        //creo un report valido
        Report report = createValidReport(1, ReportStatus.PENDING);
        when(reportRepository.findById(eq(1), any())).thenReturn(Optional.of(report));

        //eseguo il metodo
        Report result = manageReportService.getReport(1);

        //verifico che il report sia stato ritornato correttamente
        assertEquals(report, result);
        verify(reportRepository).findById(eq(1), any());
    }

    @Test
    void getReport_ThrowsException_WhenReportDoesNotExist() {
        //creo un report non esistente
        when(reportRepository.findById(eq(99), any())).thenReturn(Optional.empty());

        //eseguo il metodo e verifico che lanci un'eccezione
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                manageReportService.getReport(99)
        );
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void getPendingReports_ReturnsListFromRepository() {
        //creo una lista di report validi
        List<Report> expectedList = List.of(createValidReport(1, ReportStatus.PENDING), createValidReport(2, ReportStatus.APPROVED));
        when(reportRepository.findByStatus(eq(ReportStatus.PENDING), any())).thenReturn(List.of(expectedList.getFirst()));

        //eseguo il metodo
        List<Report> result = manageReportService.getPendingReports();

        //verifico che vada a ritornare solo il report PENDING
        assertEquals(1, result.size());
        assertEquals(ReportStatus.PENDING, result.getFirst().getStatus());
        verify(reportRepository).findByStatus(eq(ReportStatus.PENDING), any());
    }

    @Test
    void getUserReports_CombinesReporterAndReportedLists() {
        int userId = 10;
        //l'utente è stato segnalato in questo report
        List<Report> reportedList = List.of(new Report(1, 20, userId, ReportTargetType.USER, "Non l'ha raccontata giusta", Instant.now(), ReportStatus.PENDING, 100));
        //l'utente ha creato questa segnalazione
        List<Report> reporterList = List.of(new Report(2, userId, 30, ReportTargetType.USER, "Ora reporto io", Instant.now(), ReportStatus.PENDING, 101));
        when(reportRepository.findByReportedId(eq(userId), any())).thenReturn(reportedList);
        when(reportRepository.findByReporterId(eq(userId), any())).thenReturn(reporterList);

        //eseguo il metodo
        List<Report> result = manageReportService.getUserReports(userId);

        //verifico che la lista finale contenga esattamente la somma delle due liste
        assertEquals(2, result.size());
        verify(reportRepository).findByReportedId(eq(userId), any());
        verify(reportRepository).findByReporterId(eq(userId), any());
    }

    // ==========================================
    // 2. TEST DI APPROVAZIONE E RIFIUTO
    // ==========================================

    @Test
    void approveReport_Succeeds_ChangesStatusAndUpdatesDb() {
        //creo un report in stato PENDING
        Report pendingReport = createValidReport(1, ReportStatus.PENDING);
        when(reportRepository.findById(eq(1), any())).thenReturn(Optional.of(pendingReport));

        //chiamo il metodo
        manageReportService.approveReport(1);

        //verifico le modifiche
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).updateStatus(captor.capture(), any());
        assertEquals(ReportStatus.APPROVED, captor.getValue().getStatus());
    }

    @Test
    void approveReport_ThrowsException_IfAlreadyProcessed() {
        //creo un report in stato APPROVED
        Report approvedReport = createValidReport(1, ReportStatus.APPROVED);
        when(reportRepository.findById(eq(1), any())).thenReturn(Optional.of(approvedReport));

        //deve lanciare eccezione e non modificare nulla
        assertThrows(IllegalStateException.class, () -> manageReportService.approveReport(1));
        verify(reportRepository, never()).updateStatus(any(), any());
    }

    @Test
    void rejectReport_Succeeds_ChangesStatusAndUpdatesDb() {
        //creo un report in stato PENDING
        Report pendingReport = createValidReport(2, ReportStatus.PENDING);
        when(reportRepository.findById(eq(2), any())).thenReturn(Optional.of(pendingReport));

        //chiamo il metodo
        manageReportService.rejectReport(2);

        //verifico le modifiche
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).updateStatus(captor.capture(), any());
        assertEquals(ReportStatus.REJECTED, captor.getValue().getStatus());
    }

    @Test
    void rejectReport_ThrowsException_IfAlreadyProcessed() {
        //creo un report in stato REJECTED
        Report rejectedReport = createValidReport(2, ReportStatus.REJECTED);
        when(reportRepository.findById(eq(2), any())).thenReturn(Optional.of(rejectedReport));

        //deve lanciare eccezione e non modificare nulla
        assertThrows(IllegalStateException.class, () -> manageReportService.rejectReport(2));
        verify(reportRepository, never()).updateStatus(any(), any());
    }

    // ==========================================
    // HELPER
    // ==========================================

    private Report createValidReport(int id, ReportStatus status) {
        return new Report(
                id,
                10,
                20,
                ReportTargetType.USER,
                "Non ha messo il mio cavallo in un posto sicuro",
                Instant.now(),
                status,
                100
        );
    }
}