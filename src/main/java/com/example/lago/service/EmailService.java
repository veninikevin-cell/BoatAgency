package com.example.lago.service;

import com.example.lago.model.Booking;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


 // Servizio per l'invio di email tramite SMTP Gmail.
 // Usato per notificare utenti e provider sulle prenotazioni.
 
@Service
public class EmailService {

    private final JavaMailSender mail;

    public EmailService(JavaMailSender mail) {
        this.mail = mail;
    }

    // email all'utente quando prenota
    public void inviaConfermaPrenotazione(Booking booking) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getUser().getEmail());
        msg.setSubject("Prenotazione ricevuta - BoatAgency");
        msg.setText(
            "Ciao " + booking.getUser().getFirstName() + ",\n\n" +
            "La tua prenotazione per il tour \"" +
            booking.getTour().getName() + "\" è stata ricevuta.\n" +
            "Data: " + booking.getBookingDate() + "\n" +
            "Persone: " + booking.getNumPeople() + "\n" +
            "Stato: IN ATTESA DI CONFERMA\n\n" +
            "Riceverai una email quando il fornitore accetterà o rifiuterà.\n\n" +
            "BoatAgency"
        );
        mail.send(msg);
    }

    // email al provider quando arriva una nuova prenotazione
    public void avvisaProvider(Booking booking) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getTour().getAgency().getOwner().getEmail());
        msg.setSubject("Nuova prenotazione - " + booking.getTour().getName());
        msg.setText(
            "Ciao " + booking.getTour().getAgency().getOwner().getFirstName() + ",\n\n" +
            "Hai ricevuto una nuova prenotazione!\n\n" +
            "Tour: " + booking.getTour().getName() + "\n" +
            "Utente: " + booking.getUser().getFirstName() +
            " " + booking.getUser().getLastName() + "\n" +
            "Data: " + booking.getBookingDate() + "\n" +
            "Persone: " + booking.getNumPeople() + "\n\n" +
            "Accedi alla dashboard per accettare o rifiutare.\n\n" +
            "BoatAgency"
        );
        mail.send(msg);
    }

    // email all'utente per quando il provider accetta
    public void inviaConferma(Booking booking) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getUser().getEmail());
        msg.setSubject("Prenotazione CONFERMATA - BoatAgency");
        msg.setText(
            "Ciao " + booking.getUser().getFirstName() + ",\n\n" +
            "La tua prenotazione è stata CONFERMATA!\n\n" +
            "Tour: " + booking.getTour().getName() + "\n" +
            "Data: " + booking.getBookingDate() + "\n" +
            "Persone: " + booking.getNumPeople() + "\n\n" +
            "Ti aspettiamo!\n\n" +
            "BoatAgency"
        );
        mail.send(msg);
    }

    // email all'utente quando il provider rifiuta
    public void inviaRifiuto(Booking booking) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getUser().getEmail());
        msg.setSubject("Prenotazione rifiutata - BoatAgency");
        msg.setText(
            "Ciao " + booking.getUser().getFirstName() + ",\n\n" +
            "Purtroppo la tua prenotazione per il tour \"" +
            booking.getTour().getName() + "\" è stata rifiutata.\n\n" +
            "Puoi cercare altri tour disponibili sul sito.\n\n" +
            "BoatAgency"
        );
        mail.send(msg);
    }
}