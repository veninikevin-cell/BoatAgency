package com.example.lago.observer;

import com.example.lago.model.Booking;

// Observer per gli eventi prenotazione: implementata da EmailNotificationObserver
// e WebSocketNotificationObserver. evento è una stringa libera per ora
// (CREATA/CONFERMATA/RIFIUTATA)
public interface BookingObserver {

    void update(Booking booking, String evento);
}