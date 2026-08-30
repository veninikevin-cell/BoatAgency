package com.example.lago.security;

import com.example.lago.model.User;
import com.example.lago.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// implementazione richiesta da Spring Security per il login - chiamata
// automaticamente dal framework, riceve l'email dal form e deve tornare
// un UserDetails che Spring usa per verificare la password (vedi SecurityConfig
// per come vengono usati i ruoli come authority)
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Utente non trovato: " + email));

        // ruolo (USER/PROVIDER) usato come authority per l'autorizzazione
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }
}