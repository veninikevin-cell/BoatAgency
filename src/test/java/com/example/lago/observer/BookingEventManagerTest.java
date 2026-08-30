package com.example.lago.observer;

import com.example.lago.model.Booking;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

// Unit test per il Subject del pattern Observer 
public class BookingEventManagerTest {

    @Test
    void notifica_piuObserverRegistrati() {
        BookingEventManager manager = new BookingEventManager();
        BookingObserver primo = mock(BookingObserver.class);
        BookingObserver secondo = mock(BookingObserver.class);
        Booking booking = new Booking();

        manager.registra(primo);
        manager.registra(secondo);
        manager.notifica(booking, "CONFERMATA");

        verify(primo, times(1)).update(booking, "CONFERMATA");
        verify(secondo, times(1)).update(booking, "CONFERMATA");
    }

    @Test
    void rimuovi_observerRimosso() {
        BookingEventManager manager = new BookingEventManager();
        BookingObserver observer = mock(BookingObserver.class);
        Booking booking = new Booking();

        manager.registra(observer);
        manager.rimuovi(observer);
        manager.notifica(booking, "RIFIUTATA");

        verify(observer, times(0)).update(eq(booking), any());
    }
}