package com.example.lago.exception;


 // Eccezione lanciata quando si tenta di registrare un'email già presente nel sistema.
 
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email già registrata: " + email);
    }

}