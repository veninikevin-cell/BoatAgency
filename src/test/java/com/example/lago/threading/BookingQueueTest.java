package com.example.lago.threading;

import com.example.lago.model.Booking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Unit test per la coda condivisa degli eventi di prenotazione 
public class BookingQueueTest {

    @Test
    void size_codaAppenaCreata_restituisceZero() {
        BookingQueue queue = new BookingQueue();

        assertEquals(0, queue.size());
    }

    @Test
    void produci_unEvento_aumentaLaDimensione() throws InterruptedException {
        BookingQueue queue = new BookingQueue();

        queue.produci(new BookingEvent(new Booking(), "CREATA"));

        assertEquals(1, queue.size());
    }
    //TEST consuma
    @Test
    void consuma_codaConUnElemento_restituisceLEvento() throws InterruptedException {
        BookingQueue queue = new BookingQueue();
        Booking booking = new Booking();
        booking.setId(1L);
        BookingEvent evento = new BookingEvent(booking, "CREATA");

        queue.produci(evento);
        BookingEvent prelevato = queue.consuma();

        assertNotNull(prelevato);
        assertEquals(booking, prelevato.getBooking());
        assertEquals("CREATA", prelevato.getEvento());
        assertEquals(0, queue.size());
    }

    @Test
    void consuma_piuElementi_rispettaLOrdineDiInserimento() throws InterruptedException {
        BookingQueue queue = new BookingQueue();
        BookingEvent primo = new BookingEvent(new Booking(), "CREATA");
        BookingEvent secondo = new BookingEvent(new Booking(), "CONFERMATA");

        queue.produci(primo);
        queue.produci(secondo);

        assertEquals(primo, queue.consuma());
        assertEquals(secondo, queue.consuma());
        assertEquals(0, queue.size());
    }
}