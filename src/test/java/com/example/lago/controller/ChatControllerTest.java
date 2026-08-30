package com.example.lago.controller;

import com.example.lago.config.SecurityConfig;
import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.repository.ChatMessageRepository;
import com.example.lago.security.CustomUserDetailsService;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Unit test per la classe ChatController 
@WebMvcTest(ChatController.class)
@Import(SecurityConfig.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @MockitoBean
    private ChatMessageRepository chatMessageRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // TEST chatPage
    @WithMockUser(roles = "USER")
    void chatPage_apertaDallUtente() throws Exception {
        User cliente = new User("Mario", "Rossi", "mario@test.it", "pass", User.Role.USER);
        User fornitore = new User("Luigi", "Verdi", "luigi@test.it", "pass", User.Role.PROVIDER);

        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", fornitore);
        Tour tour = new Tour("Giro del lago", "desc", new BigDecimal("50"), 60, agency);
        Booking booking = new Booking(cliente, tour, LocalDate.now().plusDays(3), 2);

        when(userService.getCurrentUser(any())).thenReturn(cliente);
        when(chatMessageRepository.findByBookingIdOrderBySentAtAsc(1L)).thenReturn(List.of());
        when(bookingService.findById(1L)).thenReturn(Optional.of(booking));

        mockMvc.perform(get("/chat/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("chat/chat"))
            .andExpect(model().attribute("bookingId", 1L))
            .andExpect(model().attributeExists("currentUser"))
            .andExpect(model().attribute("receiverEmail", "luigi@test.it"));
    }

    @Test
    @WithMockUser(roles = "PROVIDER")
    void chatPage_apertaDalFornitore() throws Exception {
        User cliente = new User("Mario", "Rossi", "mario@test.it", "pass", User.Role.USER);
        User fornitore = new User("Luigi", "Verdi", "luigi@test.it", "pass", User.Role.PROVIDER);

        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", fornitore);
        Tour tour = new Tour("Giro del lago", "desc", new BigDecimal("50"), 60, agency);
        Booking booking = new Booking(cliente, tour, LocalDate.now().plusDays(3), 2);

        when(userService.getCurrentUser(any())).thenReturn(fornitore);
        when(chatMessageRepository.findByBookingIdOrderBySentAtAsc(1L)).thenReturn(List.of());
        when(bookingService.findById(1L)).thenReturn(Optional.of(booking));

        mockMvc.perform(get("/chat/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("chat/chat"))
            .andExpect(model().attribute("receiverEmail", "mario@test.it"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void chatPage_prenotazioneInesistente() throws Exception {
        User cliente = new User("Mario", "Rossi", "mario@test.it", "pass", User.Role.USER);

        when(userService.getCurrentUser(any())).thenReturn(cliente);
        when(chatMessageRepository.findByBookingIdOrderBySentAtAsc(99L)).thenReturn(List.of());
        when(bookingService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/chat/99"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("receiverEmail", ""));
    }

    @Test
    void chatPage_nonAutenticato_redirectAlLogin() throws Exception {
        mockMvc.perform(get("/chat/1"))
            .andExpect(status().is3xxRedirection());
    }
}