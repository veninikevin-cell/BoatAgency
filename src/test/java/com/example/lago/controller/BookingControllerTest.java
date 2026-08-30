package com.example.lago.controller;

import com.example.lago.config.SecurityConfig;
import com.example.lago.dto.BookingDTO;
import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.security.CustomUserDetailsService;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Unit test per il controller delle prenotazioni 
@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // TEST per il form di prenotazione
    @Test
    @WithMockUser(roles = "USER")
    void bookingForm_tourEsistente() throws Exception {
        User user = new User();
        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", user);
        Tour tour = new Tour("Giro", "desc", new BigDecimal("50"), 60, agency);
        tour.setAvailableDays("LUN,MER,VEN");

        Booking occupata = new Booking(user, tour, LocalDate.now().plusDays(2), 2);
        occupata.setStatus(Booking.Status.CONFIRMED);

        when(userService.getCurrentUser(any())).thenReturn(user);
        when(bookingService.findTourById(1L)).thenReturn(Optional.of(tour));
        when(bookingService.getConfirmedBookingsForTour(1L)).thenReturn(List.of(occupata));

        mockMvc.perform(get("/bookings/new/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("booking/booking-form"))
            .andExpect(model().attributeExists("dto"))
            .andExpect(model().attributeExists("dateOccupate"))
            .andExpect(model().attribute("availableDays", "LUN,MER,VEN"))
            .andExpect(model().attribute("tourId", 1L));
    }

    // TEST per la creazione di una prenotazione
    @Test
    @WithMockUser(roles = "USER")
    void createBooking_datiValidi_redirectMieProenotazioni() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/bookings/new/1")
                .with(csrf())
                .param("bookingDate", LocalDate.now().plusDays(5).toString())
                .param("numPeople", "3")
                .param("notes", "Nessuna allergia"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bookings/my"));

        verify(bookingService, times(1)).createBooking(any(BookingDTO.class), eq(user));
    }

    // TEST per la visualizzazione delle prenotazioni dell'utente
    @Test
    @WithMockUser(roles = "USER")
    void myBookings_mostraElenco() throws Exception {
        User user = new User();
        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", user);
        Tour tour = new Tour("Giro del lago", "desc", new BigDecimal("50"), 60, agency);
        Booking booking = new Booking(user, tour, LocalDate.now().plusDays(4), 2);
        booking.setStatus(Booking.Status.PENDING);

        when(userService.getCurrentUser(any())).thenReturn(user);
        when(bookingService.getBookingsByUser(user)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/my"))
            .andExpect(status().isOk())
            .andExpect(view().name("booking/my-bookings"))
            .andExpect(model().attributeExists("bookings"));
    }
    @Test
    void myBookings_nonAutenticato_redirectAlLogin() throws Exception {
        mockMvc.perform(get("/bookings/my"))
            .andExpect(status().is3xxRedirection());
    }

    // TEST per la conferma di una prenotazione da parte del fornitore 
    @Test
    @WithMockUser(roles = "PROVIDER")
    void confirmBooking_provider_redirectDashboard() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/bookings/confirm/1").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/provider/dashboard"));

        verify(bookingService, times(1)).confirmBooking(1L, user);
    }

    @Test
    @WithMockUser(roles = "USER")
    void confirmBooking_ruoloUser_accessoNegato() throws Exception {
        mockMvc.perform(post("/bookings/confirm/1").with(csrf()))
            .andExpect(status().isForbidden());
    }
    // TEST per il rifiuto di una prenotazione da parte del fornitore 
    @Test
    @WithMockUser(roles = "PROVIDER")
    void rejectBooking_provider_redirectDashboard() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/bookings/reject/1").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/provider/dashboard"));

        verify(bookingService, times(1)).rejectBooking(1L, user);
    }


    // TEST per l'annullamento di una prenotazione da parte dell'utente
    @Test
    @WithMockUser(roles = "USER")
    void cancelBooking_utente_redirectMieProenotazioni() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/bookings/cancel/1").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bookings/my"));

        verify(bookingService, times(1)).cancelBooking(1L, user);
    }

}