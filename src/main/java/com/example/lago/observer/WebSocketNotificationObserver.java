package com.example.lago.observer;

import com.example.lago.model.Booking;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

// observer che manda notifiche websocket in tempo reale - si registra su
// BookingEventManager. Le destinazioni STOMP sono diverse per provider (/queue/notifications)
// e user (/queue/bookings)
@Component
public class WebSocketNotificationObserver implements BookingObserver {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationObserver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void update(Booking booking, String evento) {
        switch (evento) {
            case "CREATA":
                // notifica il PROVIDER (nuova prenotazione da gestire)
                String provEmail = booking.getTour()
                    .getAgency().getOwner().getEmail();
                messagingTemplate.convertAndSendToUser(
                    provEmail,
                    "/queue/notifications",
                    Map.of(
                        "bookingId", booking.getId(),
                        "userName", booking.getUser().getFirstName(),
                        "tourName", booking.getTour().getName()
                    )
                );
                break;
            case "CONFERMATA":
            case "RIFIUTATA":
                // notifica l'USER (esito della sua richiesta)
                String userEmail = booking.getUser().getEmail();
                messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/bookings",
                    Map.of("bookingId", booking.getId(), "status", evento)
                );
                break;
        }}}