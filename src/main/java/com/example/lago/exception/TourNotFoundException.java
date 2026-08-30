package com.example.lago.exception;


 // Eccezione lanciata quando un tour non viene trovato nel database.
 
public class TourNotFoundException extends RuntimeException {

    public TourNotFoundException(Long id) {
        super("Tour non trovato con id: " + id);
    }
}