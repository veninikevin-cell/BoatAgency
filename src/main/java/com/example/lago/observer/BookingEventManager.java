package com.example.lago.observer;

import com.example.lago.model.Booking;

import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

// Subject del pattern Observer: tiene la lista di observer registrati e li notifica sugli eventi prenotazione

@Component
public class BookingEventManager {

    private final List<BookingObserver> observers = new ArrayList<>();

    public void registra(BookingObserver observer) {
        observers.add(observer);
}

    public void rimuovi(BookingObserver observer) {
        observers.remove(observer);
    }

    // notifica in ordine di registrazione
    public void notifica(Booking booking, String evento) {
        for (BookingObserver observer : observers) {
            observer.update(booking, evento);
        }
    }}