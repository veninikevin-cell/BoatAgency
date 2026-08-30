package com.example.lago.strategy;
import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class NomeAgenziaFilterTest { //no dip. esterne, no mock

     
    @Test
    void filtra_nomeNullo(){
        Agency agency = new Agency("prova", "desc", "Como", "000", null);
        Tour tour1 = new Tour("tour1", "desc", new BigDecimal("50"), 60, agency);
        Tour tour2 = new Tour("tour2", "desc", new BigDecimal("80"), 90, agency);
        List<Tour> tours = List.of(tour1, tour2);

        NomeAgenziaFilter filtro = new NomeAgenziaFilter(null);
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(2, risultato.size()); // nessun filtro applicato

    }

     @Test
     void filtra_stringaVuota_restituisceTuttiITour() {
        Agency agency = new Agency("ag", "desc", "Como", "000", null);
        Tour tour1 = new Tour("tour1", "desc", new BigDecimal("50"), 60, agency);
        List<Tour> tours = List.of(tour1);

        NomeAgenziaFilter filtro = new NomeAgenziaFilter("");
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(1, risultato.size());
    }

     @Test
     void filtra_conNomeParziale_restituisceToursCorrispondenti() {
       
        Agency agencyComo = new Agency("Como Boat Experience", "desc", "Como", "000", null);
        Agency agencyBellagio = new Agency("Bellagio Tours", "desc", "Bellagio", "111", null);
        Agency agencyVarenna = new Agency("Varenna Tours", "desc", "Varenna", "222", null);

        Tour tourComo = new Tour("Giretto", "desc", new BigDecimal("50"), 60, agencyComo);
        Tour tourBellagio = new Tour("Escursione", "desc", new BigDecimal("80"), 90, agencyBellagio);
        Tour tourVarenna = new Tour("Tour", "desc", new BigDecimal("100"), 120, agencyVarenna);

        List<Tour> tours = List.of(tourComo, tourBellagio, tourVarenna);

        NomeAgenziaFilter filtro = new NomeAgenziaFilter("boat");
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(1, risultato.size());
        assertEquals(tourComo, risultato.get(0)); // solo il tour dell'agenzia con "Boat" nel nome
    }


}
