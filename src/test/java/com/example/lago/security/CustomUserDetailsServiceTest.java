package com.example.lago.security;

import com.example.lago.model.User;
import com.example.lago.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Unit test per il servizio di caricamento delle credenziali utente
@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_utenteEsistente() {
        User user = new User("nome", "cogn", "nome@test.it", "passwordCif", User.Role.USER);

        when(userRepository.findByEmail("nome@test.it")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("nome@test.it");

        assertNotNull(result);
        assertEquals("nome@test.it", result.getUsername());
        assertEquals("passwordCif", result.getPassword());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_utenteProvider() {
        User user = new User("nome", "cogn", "nome@test.it", "passwordCif", User.Role.PROVIDER);

        when(userRepository.findByEmail("nome@test.it")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("nome@test.it");

        assertTrue(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER")));
    }

    @Test
    void loadUserByUsername_utenteInesistenteEccezione() {
        when(userRepository.findByEmail("p@test.it")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
            () -> customUserDetailsService.loadUserByUsername("p@test.it"));
    }
}