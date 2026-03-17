package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.out.beach.BeachCatalogQuery;
import com.example.s_balneare.application.port.out.beach.BeachSummary;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.common.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrowseBeachServiceTest {
    Address address = Address.create("Via A", "2/b", "Roma", "49100", "Italy");

    //mock della porta di uscita (il database fittizio)
    @Mock
    private BeachCatalogQuery beachCatalogQuery;

    //System Under Test (SUT)
    private BrowseBeachService browseBeachService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TestTransactionManager transactionManager = new TestTransactionManager();
        browseBeachService = new BrowseBeachService(beachCatalogQuery, transactionManager);
    }

    // ==========================================
    // TEST METODI DI BUSINESS
    // ==========================================

    @Test
    void getActiveBeaches_CallsQueryWithNullKeyword_AndReturnsList() {
        //passo 1: preparo i dati fittizi che il finto DB deve restituire
        BeachSummary dummyBeach = new BeachSummary(1, address, "Nomeee", "Roma", "+39111", BeachServices.none(), "");
        List<BeachSummary> expectedList = List.of(dummyBeach);

        //"quando qualcuno chiama searchActiveBeaches con keyword null, restituisci expectedList"
        when(beachCatalogQuery.searchActiveBeaches(isNull(), any())).thenReturn(expectedList);

        //passo 2: chiamo il metodo del service
        List<BeachSummary> result = browseBeachService.getActiveBeaches();

        //passo 3: verifico che il risultato sia quello atteso
        assertEquals(1, result.size());
        assertEquals("Nomeee", result.getFirst().name());

        //verifico che il service abbia effettivamente interrogato la query port esattamente 1 volta con "null"
        verify(beachCatalogQuery, times(1)).searchActiveBeaches(isNull(), any());
    }

    @Test
    void searchActiveBeaches_CallsQueryWithKeyword_AndReturnsFilteredList() {
        //passo 1: preparo i dati fittizi che il finto DB deve restituire
        String keyword = "Napoli";
        BeachSummary dummyBeach = new BeachSummary(2, address, "La Stella","Napoli", "+39222", BeachServices.none(), "");
        List<BeachSummary> expectedList = List.of(dummyBeach);

        //"quando qualcuno chiama searchActiveBeaches con keyword, restituisci expectedList"
        when(beachCatalogQuery.searchActiveBeaches(eq(keyword), any())).thenReturn(expectedList);

        //passo 2: chiamo il metodo del service
        List<BeachSummary> result = browseBeachService.searchActiveBeaches(keyword);

        //passo 3: verifico che il risultato sia quello atteso
        assertEquals(1, result.size());
        assertEquals("Napoli", result.getFirst().city());

        //verifico che la chiamata sia partita con il parametro corretto
        verify(beachCatalogQuery, times(1)).searchActiveBeaches(eq(keyword), any());
    }

    @Test
    void searchActiveBeaches_ReturnsEmptyList_WhenQueryFindsNothing() {
        String keyword = "Arabizinbunza";

        //DB non trova nulla
        when(beachCatalogQuery.searchActiveBeaches(eq(keyword), any())).thenReturn(List.of());

        //estraggo il risultato
        List<BeachSummary> result = browseBeachService.searchActiveBeaches(keyword);

        //verifico di aver ricevuto null
        assertTrue(result.isEmpty());
        verify(beachCatalogQuery, times(1)).searchActiveBeaches(eq(keyword), any());
    }
}