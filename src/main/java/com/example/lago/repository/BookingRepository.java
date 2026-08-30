package com.example.lago.repository;

import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
// Accesso ai dati delle prenotazioni, filtrabili per utente, tour o stato. 

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);
    List<Booking> findByTour(Tour tour);
    List<Booking> findByTourAndStatus(Tour tour, Booking.Status status);
}