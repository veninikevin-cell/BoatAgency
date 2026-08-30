package com.example.lago.controller;

import com.example.lago.config.SecurityConfig;
import com.example.lago.dto.RegisterDTO;
import com.example.lago.security.CustomUserDetailsService;
import com.example.lago.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Unit test per la classe AuthController 
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    //TEST per showLogin
    @Test
    void showLogin_mostraLaPaginaDiLogin() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/login"));
    }

    //TEST per showRegister
    @Test
    void showRegister_mostraIlFormDiRegistrazione() throws Exception {
        mockMvc.perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/register"))
            .andExpect(model().attributeExists("dto"));
    }

    //TEST per handleRegister
    @Test
    void handleRegister_registrazioneRiuscita_redirectAlLogin() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("firstName", "nome")
                .param("lastName", "cogn")
                .param("email", "nome@cogn.com")
                .param("password", "Password111")
                .param("confirmPassword", "Password111")
                .param("accountType", "USER"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    void handleRegister_erroreDiValidazione_restaSulForm() throws Exception {
        doThrow(new IllegalArgumentException("Le password non coincidono"))
            .when(userService).register(any(RegisterDTO.class));

        mockMvc.perform(post("/register")
                .with(csrf())
                .param("firstName", "nome")
                .param("lastName", "cogn")
                .param("email", "nome@cogn.com")
                .param("password", "Password111")
                .param("confirmPassword", "Password112 ")
                .param("accountType", "USER"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/register"))
            .andExpect(model().attributeExists("error"));
    }
}