package com.example.lago.exception;


 // Eccezione lanciata quando una prenotazione non è consentita.
 
public class BookingNotAllowedException extends RuntimeException {

    public BookingNotAllowedException(String motivo) {
        super("Prenotazione non consentita: " + motivo);
    }
}