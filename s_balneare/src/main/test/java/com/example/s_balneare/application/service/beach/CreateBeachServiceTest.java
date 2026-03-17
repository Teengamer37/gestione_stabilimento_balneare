package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.CreateBeachCommand;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBeachServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private BeachRepository beachRepository;

    //System Under Test (SUT)
    private CreateBeachService createBeachService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        createBeachService = new CreateBeachService(addressRepository, beachRepository, transactionManager);
    }

    @Test
    void createBeach_Succeeds_AndSetsActiveTrue_WhenFullyConfigured() {
        //preparo il mock
        CreateBeachCommand command = createValidCommand(true);
        when(beachRepository.findByOwnerId(eq(100), any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(), any())).thenReturn(50);
        when(beachRepository.save(any(), any())).thenReturn(1);

        //eseguo il metodo
        Integer beachId = createBeachService.createBeach(command);

        //uso un ArgumentCaptor per "catturare" l'oggetto Beach che viene passato al metodo create
        //verifico il corretto salvataggio nel DB
        assertEquals(1, beachId);
        ArgumentCaptor<Beach> beachCaptor = ArgumentCaptor.forClass(Beach.class);
        verify(beachRepository).save(beachCaptor.capture(), any());
        assertTrue(beachCaptor.getValue().isActive());
    }

    @Test
    void createBeach_Succeeds_ButSetsActiveFalse_WhenConfigurationIncomplete() {
        //spiaggia non configurata completamente, ma con active = TRUE
        CreateBeachCommand command = new CreateBeachCommand(
                100, createValidGeneral(), null, null, null, null, null, "", true,
                false, "Street", "1", "City", "123", "IT"
        );

        //istruisco il mock
        when(beachRepository.findByOwnerId(eq(100), any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(), any())).thenReturn(50);
        when(beachRepository.save(any(), any())).thenReturn(1);

        //eseguo il metodo
        createBeachService.createBeach(command);

        //verifico, se salvando, la spiaggia è stata automaticamente messa ad active = FALSE
        ArgumentCaptor<Beach> beachCaptor = ArgumentCaptor.forClass(Beach.class);
        verify(beachRepository).save(beachCaptor.capture(), any());
        assertFalse(beachCaptor.getValue().isActive());
    }

    @Test
    void createBeach_ThrowsException_IfOwnerAlreadyHasBeach() {
        CreateBeachCommand command = createValidCommand(true);
        when(beachRepository.findByOwnerId(eq(100), any())).thenReturn(Optional.of(mock(Beach.class)));

        assertThrows(IllegalStateException.class, () -> createBeachService.createBeach(command));
        verify(beachRepository, never()).save(any(), any());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private BeachGeneral createValidGeneral() {
        return new BeachGeneral("Lido Test", "Una descrizione valida", "+393331234567");
    }

    private CreateBeachCommand createValidCommand(boolean active) {
        Pricing pricing = Pricing.create(10, 10, 10, 10, 10);
        List<Season> seasons = List.of(
                Season.builder()
                        .name("Summer")
                        .startDate(LocalDate.now().plusDays(1))
                        .endDate(LocalDate.now().plusMonths(2))
                        .pricing(pricing)
                        .zoneTariffs(List.of(ZoneTariff.create("A", 10, 10)))
                        .build()
        );
        List<Zone> zones = List.of(new Zone("A", List.of()));

        return new CreateBeachCommand(
                100,
                createValidGeneral(),
                BeachInventory.empty(),
                BeachServices.none(),
                Parking.empty(),
                seasons,
                zones,
                "",
                active,
                false,
                "Via Roma", "1", "Roma", "00100", "IT"
        );
    }
}