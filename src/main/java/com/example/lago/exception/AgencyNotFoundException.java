package com.example.lago.exception;


// Eccezione lanciata quando un'agenzia non viene trovata nel database.
 
public class AgencyNotFoundException extends RuntimeException {

    public AgencyNotFoundException(Long id) {
        super("Agenzia non trovata con id: " + id);
    }

    public AgencyNotFoundException(String msg) {
        super(msg);
    }
}