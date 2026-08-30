package com.example.lago.repository;

import com.example.lago.model.Agency;
import com.example.lago.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
// Accesso ai dati delle agenzie: una per ogni fornitore proprietario. 

public interface AgencyRepository extends JpaRepository<Agency, Long> {

    Optional<Agency> findByOwner(User owner);
    boolean existsByOwner(User owner);
}