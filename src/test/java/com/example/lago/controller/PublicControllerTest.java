package com.example.lago.controller;

import com.example.lago.config.SecurityConfig;
import com.example.lago.dto.ReviewDTO;
import com.example.lago.exception.AgencyNotFoundException;
import com.example.lago.model.Agency;
import com.example.lago.model.User;
import com.example.lago.security.CustomUserDetailsService;
import com.example.lago.service.AgencyService;
import com.example.lago.service.ReviewService;
import com.example.lago.service.UserService;

import jakarta.servlet.ServletException;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// Unit test per la classe PublicController 
@WebMvcTest(PublicController.class)
@Import(SecurityConfig.class)
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgencyService agencyService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    //TEST per agencyPage
    @Test
    void agencyPage_utenteAnonimo_nonPuoRecensire() throws Exception {
        User owner = new User();
        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", owner);
        agency.setId(1L);

        when(agencyService.findById(1L)).thenReturn(Optional.of(agency));
      
        mockMvc.perform(get("/public/agency/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("agency/agency-page"))
            .andExpect(model().attributeExists("agency"))
            .andExpect(model().attributeExists("reviews"))
            .andExpect(model().attributeExists("tours"))
            .andExpect(model().attribute("canReview", false));
    }

    @Test
    @WithMockUser(roles = "USER")
    void agencyPage_utenteAbilitato_puoRecensire() throws Exception {
        User user = new User();
        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", user);
        agency.setId(1L);

        when(agencyService.findById(1L)).thenReturn(Optional.of(agency));
        when(reviewService.getAverageRating(agency)).thenReturn(4.5);
        when(userService.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewService.canReview(user, agency)).thenReturn(true);

        mockMvc.perform(get("/public/agency/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("agency/agency-page"))
            .andExpect(model().attribute("canReview", true))
            .andExpect(model().attribute("media", 4.5));
    }

    @Test
    void agencyPage_agenziaInesistente_lanciaEccezione() throws Exception {
        when(agencyService.findById(99L)).thenReturn(Optional.empty());

        ServletException ex = assertThrows(ServletException.class,
            () -> mockMvc.perform(get("/public/agency/99")));

        assertInstanceOf(AgencyNotFoundException.class, ex.getCause());
    }

    // TEST per saveReview
    @Test
    @WithMockUser(roles = "USER")
    void saveReview_datiValidi_redirectPaginaAgenzia() throws Exception {
        User user = new User();
        when(userService.getCurrentUser(any())).thenReturn(user);

        mockMvc.perform(post("/public/agency/1/review")
                .with(csrf())
                .param("rating", "5")
                .param("comment", "Esperienza fantastica"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/public/agency/1"));

        verify(reviewService, times(1)).addReview(any(ReviewDTO.class), eq(user));
    }

    @Test
    void saveReview_nonAutenticato_redirectAlLogin() throws Exception {
        mockMvc.perform(post("/public/agency/1/review")
                .with(csrf())
                .param("rating", "5")
                .param("comment", "Test"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "mail@test.com", roles = "USER")
    void saveReview_utenteHaGiaRecensito_mostraErrore() throws Exception {
        User user = new User();
        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", user);
        agency.setId(1L);

        when(userService.getCurrentUser(any())).thenReturn(user);
        when(agencyService.findById(1L)).thenReturn(Optional.of(agency));
        when(reviewService.canReview(user, agency)).thenReturn(false);  // ← nuovo stub necessario

        doThrow(new IllegalArgumentException("Hai già recensito questa agenzia"))
            .when(reviewService).addReview(any(ReviewDTO.class), any(User.class));

        mockMvc.perform(post("/public/agency/1/review")
                .with(csrf())
                .param("rating", "5")
                .param("comment", "Bellissimo tour!"))
            .andExpect(status().isOk())
            .andExpect(view().name("agency/agency-page"))
            .andExpect(model().attributeExists("agency"))
            .andExpect(model().attributeExists("dto"))
            .andExpect(model().attribute("canReview", false))   // ← cambiato da true a false
            .andExpect(model().attribute("error", "Hai già recensito questa agenzia"));

        verify(reviewService, times(1)).addReview(any(ReviewDTO.class), eq(user));
}

}

