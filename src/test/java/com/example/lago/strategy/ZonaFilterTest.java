package com.example.lago.strategy;

import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
 
class ZonaFilterTest {

    @Test
    void filtra_zonaNulla_restituisceTuttiITour() {
        Agency agency = new Agency("nome", "desc", "zona", "000", null);
        Tour tour1 = new Tour("tour1", "desc", new BigDecimal("50"), 60, agency);
        tour1.setZoneDeparture("Como");
        Tour tour2 = new Tour("tour2", "desc", new BigDecimal("80"), 90, agency);
        tour2.setZoneDeparture("Bellagio");

        List<Tour> tours = List.of(tour1, tour2);

        ZonaFilter filtro = new ZonaFilter(null);
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(2, risultato.size());
    }

    @Test
    void filtra_stringaVuota_restituisceTuttiITour() {
        Agency agency = new Agency("nome", "desc", "zona", "000", null);
        Tour tour1 = new Tour("tour1", "desc", new BigDecimal("50"), 60, agency);
        tour1.setZoneDeparture("Como");

        List<Tour> tours = List.of(tour1);

        ZonaFilter filtro = new ZonaFilter("");
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(1, risultato.size());
    }

    @Test
    void filtra_zonaTutte_restituisceTuttiITour() {
        Agency agency = new Agency("nome", "desc", "zona", "000", null);
        Tour tour1 = new Tour("tour1", "desc", new BigDecimal("50"), 60, agency);
        tour1.setZoneDeparture("Como");
        Tour tour2 = new Tour("tour2", "desc", new BigDecimal("80"), 90, agency);
        tour2.setZoneDeparture("Lecco");

        List<Tour> tours = List.of(tour1, tour2);

        ZonaFilter filtro = new ZonaFilter("Tutte");
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(2, risultato.size());
    }

    @Test
    void filtra_conZonaSpecifica_restituisceSoloIToursDiQuellaZona() {
        Agency agency = new Agency("nome", "desc", "zona", "000", null);
        Tour tourComo = new Tour("Giro lago", "desc", new BigDecimal("50"), 60, agency);
        tourComo.setZoneDeparture("Como");
        Tour tourBellagio = new Tour("uscita", "desc", new BigDecimal("80"), 90, agency);
        tourBellagio.setZoneDeparture("Bellagio");

        List<Tour> tours = List.of(tourComo, tourBellagio);

        ZonaFilter filtro = new ZonaFilter("Como");
        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(1, risultato.size());
        assertEquals(tourComo, risultato.get(0));
    }
}