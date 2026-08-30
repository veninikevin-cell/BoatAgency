package com.example.lago.controller;

import com.example.lago.config.SecurityConfig;
import com.example.lago.dto.TourDTO;
import com.example.lago.model.User;
import com.example.lago.security.CustomUserDetailsService;
import com.example.lago.service.AgencyService;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Unit test per la classe AgencyController 

@WebMvcTest(AgencyController.class)
@Import(SecurityConfig.class)
public class AgencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgencyService agencyService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // TEST per la dashboard fornitore
    @Test
    @WithMockUser(roles = "PROVIDER")
    void dashboard_senzaAgenzia() throws Exception{
        User user= new User();
        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.findByOwner(user)).thenReturn(Optional.empty());

        mockMvc.perform(get("/provider/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/dashboard"))
            .andExpect(model().attribute("hasAgency", false));
    }
    
    @Test
    @WithMockUser(roles="PROVIDER")
    void dashboard_conAgenzia() throws Exception{
        User user = new User();
        Agency agency = new Agency("co", "desc", "Como", "0000100", user);
        Tour t1 = new Tour("t1", "d", new BigDecimal("50"), 60, agency);
        t1.setId(1L);
        Booking b1 = new Booking(user, t1, LocalDate.now().plusDays(1), 2);
        Booking b2 = new Booking(user, t1, LocalDate.now().plusDays(2), 2);
        Booking b3 = new Booking(user, t1, LocalDate.now().plusDays(3), 2);
        Booking b4 = new Booking(user, t1, LocalDate.now().plusDays(4), 2);
        b1.setStatus(Booking.Status.CONFIRMED);
        b2.setStatus(Booking.Status.PENDING);
        b3.setStatus(Booking.Status.REJECTED);
        b4.setStatus(Booking.Status.CONFIRMED);

        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.findByOwner(user)).thenReturn(Optional.of(agency));
        when(agencyService.getToursByAgency(agency)).thenReturn(List.of(t1));
        when(bookingService.getBookingsByTour(t1)).thenReturn(List.of(b1, b2, b3, b4));

        mockMvc.perform(get("/provider/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/dashboard"))
            .andExpect(model().attribute("hasAgency",true))
            .andExpect(model().attribute("totalePrenotazioni", 4));
    }

    //TEST che tocca la regola in SecurityConfig di accesso
    @Test
    @WithMockUser(roles = "USER")
    void dashboard_ruoloUser_accessoNegato() throws Exception {
        mockMvc.perform(get("/provider/dashboard"))
            .andExpect(status().isForbidden());
    }

    @Test
    void dashboard_nonAutenticato_redirectAlLogin() throws Exception {
        mockMvc.perform(get("/provider/dashboard"))
            .andExpect(status().is3xxRedirection());
    }

    // TEST per la creazione di un tour
    @Test
    @WithMockUser(roles = "PROVIDER")
    void saveTour_conSuccesso() throws Exception {
        User user = new User();

        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/provider/tours/new")
                .with(csrf())
                .param("name", "Tour Test")
                .param("description", "Des")
                .param("price", "100.00")
                .param("durationMinutes", "120")
                .param("zoneDeparture", "Como"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/provider/dashboard"));
    }

    @Test
    @WithMockUser(roles = "PROVIDER")
    void saveTour_datiInvalidi() throws Exception {
        User user = new User();

        when(userService.getCurrentUser(any())).thenReturn(user);
        doThrow(new IllegalArgumentException("Dati non validi")).when(agencyService).addTour(any(TourDTO.class), any(User.class));

        mockMvc.perform(post("/provider/tours/new")
                .with(csrf())
                .param("name", "")
                .param("description", "Des")
                .param("price", "100.00")
                .param("durationMinutes", "120")
                .param("zoneDeparture", "Como"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/tour-form"))
            .andExpect(model().attributeExists("error"));
    }
    

    // TEST per l'eliminazione di un tour
    @Test
    @WithMockUser(roles = "PROVIDER")
    void deleteTour_tourEsistente_redirectDashboard() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/provider/tours/delete/1")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/provider/dashboard"));

        verify(agencyService, times(1)).deleteTour(1L, user);
    }

    // TEST per saveAgency
    @Test
    @WithMockUser(roles = "PROVIDER")
    void saveAgency_successo_nonEsistente() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.hasAgency(user)).thenReturn(false);

        mockMvc.perform(post("/provider/agency")
                .with(csrf())
                .param("name", "Agenzia Test")
                .param("description", "Descrizione test")
                .param("zone", "Zona test")
                .param("phone", "123456789")
                .param("website", "https://www.test.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/provider/dashboard"));
    }

        @Test
    @WithMockUser(roles = "PROVIDER")
    void saveAgency_successo_giaEsistente() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.hasAgency(user)).thenReturn(true);

        mockMvc.perform(post("/provider/agency")
                .with(csrf())
                .param("name", "Agenzia Test")
                .param("description", "Descrizione test")
                .param("zone", "Zona test")
                .param("phone", "123456789")
                .param("website", "https://www.test.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/provider/dashboard"));
    }

    @Test
    @WithMockUser(roles = "PROVIDER")
    void saveAgency_errore_mostraForm() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.hasAgency(user)).thenReturn(false);
        when(agencyService.createAgency(any(), any())).thenThrow(new IllegalArgumentException("Hai già registrato un'agenzia"));

        mockMvc.perform(post("/provider/agency")
                .with(csrf())
                .param("name", "Agenzia Test")
                .param("zone", "Como"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/agency-form"))
            .andExpect(model().attributeExists("error"));
    }
    // TEST per il form agenzia
    @Test
    @WithMockUser(roles = "PROVIDER")
    void agencyForm_senzaAgenzia_mostraFormVuoto() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.findByOwner(user)).thenReturn(Optional.empty());

        mockMvc.perform(get("/provider/agency"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/agency-form"))
            .andExpect(model().attribute("hasAgency", false));
    }

    
    @Test
    @WithMockUser(roles = "PROVIDER")
    void agencyForm_conAgenzia_mostraFormPrecompilato() throws Exception {
        User user = new User();
        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", user);

        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.findByOwner(user)).thenReturn(Optional.of(agency));

        mockMvc.perform(get("/provider/agency"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/agency-form"))
            .andExpect(model().attribute("hasAgency", true));
    }

    // TEST per il form di creazione tour 
    @Test
    @WithMockUser(roles = "PROVIDER")
    void tourForm_mostraFormVuoto() throws Exception {
        mockMvc.perform(get("/provider/tours/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("provider/tour-form"))
            .andExpect(model().attributeExists("dto"));
    }
}