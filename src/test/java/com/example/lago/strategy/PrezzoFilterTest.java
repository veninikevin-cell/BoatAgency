package com.example.lago.strategy;

import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrezzoFilterTest {

    @Test
    void filtra_maxPrezzoNull(){
        Agency agency = new Agency();
        Tour tour1 = new Tour("tour1","desc1",new BigDecimal("50"),120,agency);
        Tour tour2 = new Tour("tour2","desc2",new BigDecimal("500"),100,agency);
        List<Tour> tours = List.of(tour1,tour2);
        PrezzoFilter filtro = new PrezzoFilter(null);

        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(2,risultato.size());
    }

    @Test
    void filtra_conMaxPrezzo(){
        Agency agency = new Agency();
        Tour tour1 = new Tour("tour1","desc1",new BigDecimal("50"),120,agency);
        Tour tour2 = new Tour("tour2","desc2",new BigDecimal("500"),100,agency);
        List<Tour> tours = List.of(tour1,tour2);
        PrezzoFilter filtro = new PrezzoFilter(new BigDecimal("100"));

        List<Tour> risultato = filtro.filtra(tours);

        assertEquals(1,risultato.size());
        assertEquals(tour1,risultato.get(0));

    }
}
