package com.example.lago.repository;

import com.example.lago.model.Agency;
import com.example.lago.model.Review;
import com.example.lago.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
// Accesso alle recensioni: per agenzia (ordinate per data) o verifica esistenza per utente+agenzia. 

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // tutte le recensioni di una agenzia
    List<Review> findByAgencyOrderByCreatedAtDesc(Agency agency);

    // controlla se utente ha già recensito questa agenzia
    boolean existsByUserAndAgency(User user, Agency agency);
}