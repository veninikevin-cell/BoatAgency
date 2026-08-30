package com.example.lago.observer;

import com.example.lago.model.Booking;
import com.example.lago.service.EmailService;
import org.springframework.stereotype.Component;

// observer che manda le email sui vari eventi prenotazione - si registra
// su BookingEventManager insieme a WebSocketNotificationObserver.
@Component
public class EmailNotificationObserver implements BookingObserver {

    private final EmailService emailService;

    public EmailNotificationObserver(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void update(Booking booking, String evento) {
        try {
            switch (evento) {
                case "CREATA":
                    emailService.inviaConfermaPrenotazione(booking);
                    emailService.avvisaProvider(booking);
                    break;
                case "CONFERMATA":
                    emailService.inviaConferma(booking);
                    break;
                case "RIFIUTATA":
                    emailService.inviaRifiuto(booking);
                    break;
                default:
                    System.out.println("[EmailObserver] evento sconosciuto: " + evento);
            }
        } catch (Exception e) {
            // non rilanciare 
            System.out.println("[EmailObserver] errore invio email: " + e.getMessage());
        }
    }}