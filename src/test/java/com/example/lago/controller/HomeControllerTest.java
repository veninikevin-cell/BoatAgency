package com.example.lago.controller;

import com.example.lago.config.SecurityConfig;
import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.security.CustomUserDetailsService;
import com.example.lago.service.AgencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Unit test per la classe HomeController
@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgencyService agencyService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // TEST home 
    @Test
    void home_senzaFiltri() throws Exception {
        User owner = new User();
        Agency agency = new Agency("Como", "desc", "Como", "031000", owner);
        Tour tour = new Tour("tour", "desc", new BigDecimal("50"), 60, agency);
        tour.setZoneDeparture("Como");

        when(agencyService.getAllAgencies()).thenReturn(List.of(agency));
        when(agencyService.getToursByAgency(agency)).thenReturn(List.of(tour));

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("agenzie"))
            .andExpect(model().attributeExists("tours"));
    }

    @Test
    void home_conFiltroZona() throws Exception {
        User owner = new User();
        Agency agency = new Agency("ag", "desc", "Como", "031000", owner);

        Tour tourComo = new Tour("acqua", "desc", new BigDecimal("50"), 60, agency);
        tourComo.setZoneDeparture("Como");
        Tour tourBellagio = new Tour("onda", "desc", new BigDecimal("80"), 90, agency);
        tourBellagio.setZoneDeparture("Bellagio");

        when(agencyService.getAllAgencies()).thenReturn(List.of(agency));
        when(agencyService.getToursByAgency(agency)).thenReturn(List.of(tourComo, tourBellagio));

        mockMvc.perform(get("/").param("zona", "Como"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("zona", "Como"))
            .andExpect(model().attribute("tours", List.of(tourComo)));
    }

    @Test
    void home_conTuttiIFiltri() throws Exception {
        User owner = new User();
        Agency agency = new Agency("ag", "desc", "Como", "031000", owner);

        Tour Cheaptour = new Tour("acqua", "desc", new BigDecimal("50"), 60, agency);
        Cheaptour.setZoneDeparture("Como");
        Tour tourCostoso = new Tour("onda", "desc", new BigDecimal("500"), 180, agency);
        tourCostoso.setZoneDeparture("Como");

        when(agencyService.getAllAgencies()).thenReturn(List.of(agency));
        when(agencyService.getToursByAgency(agency)).thenReturn(List.of(Cheaptour, tourCostoso));

        mockMvc.perform(get("/")
                .param("zona", "Como")
                .param("maxPrezzo", "100")
                .param("nomeAgenzia", "ag"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("tours", List.of(Cheaptour)));
    }
}