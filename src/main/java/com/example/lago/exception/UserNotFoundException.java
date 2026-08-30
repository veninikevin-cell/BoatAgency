package com.example.lago.exception;

 // Eccezione lanciata quando un utente non viene trovato
 
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("Utente non trovato: " + email);
    }
}