package com.example.lago.service;
import com.example.lago.repository.UserRepository;
import com.example.lago.dto.RegisterDTO;
import com.example.lago.exception.EmailAlreadyExistsException;
import com.example.lago.exception.UserNotFoundException;
import com.example.lago.model.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Optional;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
//Unit test per la classe UserService
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    // TEST su register 
    @Test
    void register_passwordNonCoincidono(){
        RegisterDTO dto = new RegisterDTO();
        dto.setPassword("password1");
        dto.setConfirmPassword("password2");
        assertThrows(IllegalArgumentException.class,()->
        userService.register(dto));

    }

    @Test
    void register_emailGiaEsistente(){
        RegisterDTO dto = new RegisterDTO();
        dto.setPassword("password");
        dto.setConfirmPassword("password");
        dto.setEmail("test@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> 
        userService.register(dto));
    }

    @Test
    void register_emailUnica(){
        RegisterDTO dto = new RegisterDTO();
        dto.setPassword("password");
        dto.setConfirmPassword("password");
        dto.setEmail("test@example.com");   

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        User user = userService.register(dto);
    
        assertEquals(dto.getFirstName(), user.getFirstName());
        assertEquals(dto.getLastName(), user.getLastName());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
    }

    // TEST su getCurrentUser
    @Test
    void getCurrentUser_utenteNonTrovato(){
        String email = "test@example.com";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            email, "password", new ArrayList<>());


        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
        userService.getCurrentUser(userDetails));
    }

    @Test
    void getCurrentUser_utenteTrovato(){
        String email = "test@example.com";
        User user = new User();
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        email, "password", new ArrayList<>());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User risultato = userService.getCurrentUser(userDetails);
  
        assertEquals(user, risultato);
    }

    // TEST su findByEmail 
    @Test
    void findByEmail_utenteTrovato() {
        String email = "test@example.com";
        User user = new User();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> risultato = userService.findByEmail(email);

        assertTrue(risultato.isPresent());
        assertEquals(user, risultato.get());
    }

    @Test
    void findByEmail_utenteNonTrovato() {
        String email = "assente@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> risultato = userService.findByEmail(email);

        assertTrue(risultato.isEmpty());
    }

    // TEST su findById
    @Test
    void findById_utenteTrovato() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> risultato = userService.findById(id);

        assertTrue(risultato.isPresent());
        assertEquals(user, risultato.get());
    }

    @Test
    void findById_utenteNonTrovato() {
        Long id = 99L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> risultato = userService.findById(id);

        assertTrue(risultato.isEmpty());
    }

    // TEST su emailExists
    @Test
    void emailExists_emailPresente_restituisceTrue() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(userService.emailExists(email));
    }

    @Test
    void emailExists_emailAssente_restituisceFalse() {
        String email = "assente@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertFalse(userService.emailExists(email));
    }
}