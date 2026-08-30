package com.example.lago.exception;


 // Eccezione lanciata quando un utente tenta di lasciare una recensione senza averne il diritto.
 // Es: nessuna prenotazione confermata con quell'agenzia.
 
public class ReviewNotAllowedException extends RuntimeException {

    public ReviewNotAllowedException(String msg) {
        super(msg);
    }
}