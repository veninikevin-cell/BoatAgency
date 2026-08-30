package com.example.lago.exception;

 // Eccezione lanciata quando un utente tenta un'operazione su una risorsa che non gli appartiene.
 
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String msg) {
        super("Operazione non autorizzata: " + msg);
    }
}