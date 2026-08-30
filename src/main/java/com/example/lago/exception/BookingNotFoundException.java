package com.example.lago.exception;

 // Eccezione lanciata quando una prenotazione non viene trovata nel database.

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Prenotazione non trovata con id: " + id);
    }
}