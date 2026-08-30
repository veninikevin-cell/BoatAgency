package com.example.lago.observer;

import com.example.lago.model.Booking;
import com.example.lago.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

// Unit test per l'observer che gestisce le notifiche via email 
@ExtendWith(MockitoExtension.class)
public class EmailNotificationObserverTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotificationObserver observer;

    @Test
    void update_eventoCreata() {
        Booking booking = new Booking();

        observer.update(booking, "CREATA");

        verify(emailService, times(1)).inviaConfermaPrenotazione(booking);
        verify(emailService, times(1)).avvisaProvider(booking);
    }

    @Test
    void update_eventoConfermata() {
        Booking booking = new Booking();

        observer.update(booking, "CONFERMATA");

        verify(emailService, times(1)).inviaConferma(booking);
    }

    @Test
    void update_eventoRifiutata() {
        Booking booking = new Booking();

        observer.update(booking, "RIFIUTATA");

        verify(emailService, times(1)).inviaRifiuto(booking);
    }
    //TEST creato perche evento è String e non enum
    @Test
    void update_eventoSconosciuto() {
        Booking booking = new Booking();

        observer.update(booking, "EVENTO_INESISTENTE");

        verifyNoInteractions(emailService);
    }

    @Test
    void update_erroreDuranteInvio() {
        Booking booking = new Booking();
        doThrow(new RuntimeException("SMTP non raggiungibile"))
            .when(emailService).inviaConfermaPrenotazione(booking);

        observer.update(booking, "CREATA");

        verify(emailService, times(1)).inviaConfermaPrenotazione(booking);
    }
}