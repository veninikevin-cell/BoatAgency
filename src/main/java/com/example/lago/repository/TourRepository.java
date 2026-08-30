package com.example.lago.repository;

import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
// Accesso ai tour, filtrabili per agenzia e per stato attivo/inattivo. 

public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByAgency(Agency agency);
}