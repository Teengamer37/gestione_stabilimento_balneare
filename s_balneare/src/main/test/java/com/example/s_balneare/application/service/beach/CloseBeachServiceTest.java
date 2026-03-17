package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.application.service.TestTransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseBeachServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private BeachRepository beachRepository;
    @Mock
    private BookingRepository bookingRepository;

    //System Under Test (SUT)
    private CloseBeachService closeBeachService;

    //helper per creare una spiaggia valida per i test
    private Beach createValidActiveBeach(Integer beachId, Integer ownerId) {
        return new Beach(
                beachId, ownerId, 1, new BeachGeneral("Nome", "Desc", "+39111"),
                BeachInventory.empty(), BeachServices.none(), Parking.empty(),
                "", null, null, true, false
        );
    }

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        closeBeachService = new CloseBeachService(beachRepository, bookingRepository, transactionManager);
    }

    // ==========================================
    // TEST PERCORSO CORRETTO
    // ==========================================

    @Test
    void closeBeach_Succeeds_WhenBeachExistsAndOwnerIsCorrect() {
        //preparo il mock
        Integer beachId = 1;
        Integer ownerId = 100;
        Beach activeBeach = createValidActiveBeach(beachId, ownerId);

        //quando il repository viene interrogato per l'ID 1, deve ritornare la spiaggia fittizia
        when(beachRepository.findById(eq(beachId), any())).thenReturn(Optional.of(activeBeach));

        //eseguo il metodo
        closeBeachService.closeBeach(beachId, ownerId);

        //verifico che i metodi corretti siano stati chiamati

        //verifico che il repository delle prenotazioni sia stato istruito a cancellare quelle future
        verify(bookingRepository, times(1)).cancelFutureBookingsForBeach(eq(beachId), any(LocalDate.class), any());

        //uso un ArgumentCaptor per "catturare" l'oggetto Beach che viene passato al metodo update
        //in questo modo, posso verificare che il suo stato sia stato cambiato in closed = true
        ArgumentCaptor<Beach> beachCaptor = ArgumentCaptor.forClass(Beach.class);
        verify(beachRepository, times(1)).update(beachCaptor.capture(), any());

        Beach capturedBeach = beachCaptor.getValue();
        assertTrue(capturedBeach.isClosed());
        assertFalse(capturedBeach.isActive());
    }

    // ==========================================
    // TEST PERCORSI ERRATI
    // ==========================================

    @Test
    void closeBeach_ThrowsException_IfBeachNotFound() {
        //il repository non trova nulla
        when(beachRepository.findById(any(), any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                closeBeachService.closeBeach(99, 100)
        );

        //verifico che non sia stata tentata nessuna operazione di modifica
        verify(bookingRepository, never()).cancelFutureBookingsForBeach(any(), any(), any());
        verify(beachRepository, never()).update(any(), any());
    }

    @Test
    void closeBeach_ThrowsException_IfOwnerIdIsIncorrect() {
        //cerco una spiaggia, ma il proprietario non corrisponde
        Integer beachId = 1;
        Integer realOwnerId = 100;
        Integer fakeOwnerId = 999;
        Beach activeBeach = createValidActiveBeach(beachId, realOwnerId);

        when(beachRepository.findById(eq(beachId), any())).thenReturn(Optional.of(activeBeach));

        assertThrows(SecurityException.class, () ->
                closeBeachService.closeBeach(beachId, fakeOwnerId)
        );

        //verifico che non sia stata tentata nessuna operazione di modifica
        verify(bookingRepository, never()).cancelFutureBookingsForBeach(any(), any(), any());
        verify(beachRepository, never()).update(any(), any());
    }

    @Test
    void closeBeach_DoesNothing_IfBeachIsAlreadyClosed() {
        //spiaggia trovata è già chiusa
        Integer beachId = 1;
        Integer ownerId = 100;
        Beach closedBeach = new Beach(
                beachId, ownerId, 1, new BeachGeneral("Nome", "Desc", "+39111"),
                null, null, null, "", null, null, false, true
        );
        when(beachRepository.findById(eq(beachId), any())).thenReturn(Optional.of(closedBeach));

        //dovrebbe lanciare eccezione dal domain object `beach.closeBeach()`
        assertThrows(IllegalStateException.class, () ->
                closeBeachService.closeBeach(beachId, ownerId)
        );

        //verifico che nessuna operazione distruttiva venga rieseguita
        verify(bookingRepository, never()).cancelFutureBookingsForBeach(any(), any(), any());
    }
}