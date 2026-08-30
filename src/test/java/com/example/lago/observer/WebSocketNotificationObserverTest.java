package com.example.lago.observer;

import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Unit test per l'observer che gestisce le notifiche in tempo reale via WebSocket 
@ExtendWith(MockitoExtension.class)
public class WebSocketNotificationObserverTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketNotificationObserver observer;

    // Costruisce una prenotazione completa di tour, agenzia e utenti 
    private Booking creaBooking() {
        User cliente = new User("Mario", "Rossi", "mario@test.it", "pass", User.Role.USER);
        cliente.setId(1L);
        User fornitore = new User("Luigi", "Verdi", "luigi@test.it", "pass", User.Role.PROVIDER);
        fornitore.setId(2L);

        Agency agency = new Agency("Como Boat", "desc", "Como", "031000", fornitore);
        Tour tour = new Tour("Giro del lago", "desc", new BigDecimal("50"), 60, agency);

        Booking booking = new Booking(cliente, tour, LocalDate.now().plusDays(3), 2);
        booking.setId(1L);
        return booking;
    }

    @Test
    void update_eventoCreata() {
        Booking booking = creaBooking();

        observer.update(booking, "CREATA");

        verify(messagingTemplate, times(1)).convertAndSendToUser(
            eq("luigi@test.it"), eq("/queue/notifications"), any(Map.class));
    }

    @Test
    void update_eventoConfermata() {
        Booking booking = creaBooking();

        observer.update(booking, "CONFERMATA");

        verify(messagingTemplate, times(1)).convertAndSendToUser(
            eq("mario@test.it"), eq("/queue/bookings"), any(Map.class));
    }

    @Test
    void update_eventoRifiutata() {
        Booking booking = creaBooking();

        observer.update(booking, "RIFIUTATA");

        verify(messagingTemplate, times(1)).convertAndSendToUser(
            eq("mario@test.it"), eq("/queue/bookings"), any(Map.class));
    }

    @Test
    void update_eventoSconosciuto() {
        Booking booking = creaBooking();

        observer.update(booking, "EVENTO_INESISTENTE");

        verifyNoInteractions(messagingTemplate);
    }
}