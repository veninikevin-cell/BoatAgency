package com.example.lago.repository;

import com.example.lago.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
// Accesso agli utenti tramite email (usata come identificativo di login). 

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}