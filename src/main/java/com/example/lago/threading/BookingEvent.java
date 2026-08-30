package com.example.lago.threading;

import com.example.lago.model.Booking;

 // Rappresenta un evento sul ciclo di vita di una prenotazione in attesa
 // di essere elaborato dal thread consumer. Incapsula la prenotazione
 // interessata e il tipo di evento, così che la notifica
 // agli observer possa avvenire in modo asincrono.
 
public class BookingEvent {
    private final Booking booking;
    private final String evento;

    public BookingEvent(Booking booking, String evento) {
        this.booking = booking;
        this.evento = evento;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getEvento() {
        return evento;
    }
}