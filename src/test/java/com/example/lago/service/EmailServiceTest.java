package com.example.lago.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.lago.model.User;
import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mail;
    @InjectMocks
    private EmailService emailService;

    @Test
    void inviaConfermaPrenotazione(){
        User user = new User();
        user.setFirstName("nome");
        user.setEmail("test@test.com");
        Tour tour = new Tour();
        tour.setName("barca");
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTour(tour);
        booking.setBookingDate(LocalDate.of(2026, 10, 15));
        booking.setNumPeople(3);

        emailService.inviaConfermaPrenotazione(booking);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mail).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("test@test.com", msg.getTo()[0]);
        assertTrue(msg.getText().contains("barca"));
    }

    @Test
    void avvisaProvider(){
        User owner = new User();
        owner.setFirstName("nome");
        owner.setEmail("test@test.com");
        Agency agency = new Agency("Como Boat", "desc", "Como", "0000100", owner);
        Tour tour = new Tour("barca", "d", new BigDecimal("50"), 60, agency);
        User cliente = new User();
        cliente.setFirstName("cliente");
        cliente.setLastName("cogn");
        Booking booking = new Booking();
        booking.setUser(cliente);
        booking.setTour(tour);
        booking.setBookingDate(LocalDate.of(2026, 10, 15));
        booking.setNumPeople(3);

        emailService.avvisaProvider(booking);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mail).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("test@test.com", msg.getTo()[0]);
        assertTrue(msg.getText().contains("barca"));
        assertTrue(msg.getText().contains("cliente"));
}

    @Test
    void inviaConferma(){
        User user = new User();
        user.setFirstName("nome");
        user.setEmail("test@test.com");
        Tour tour = new Tour();
        tour.setName("barca");
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTour(tour);
        booking.setBookingDate(LocalDate.of(2026, 10, 15));
        booking.setNumPeople(3);

        emailService.inviaConferma(booking);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mail).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("test@test.com", msg.getTo()[0]);
        assertTrue(msg.getText().contains("barca"));
        assertTrue(msg.getText().contains("CONFERMATA"));
}

    @Test
    void inviaRifiuto(){
        User user = new User();
        user.setFirstName("nome");
        user.setEmail("test@test.com");
        Tour tour = new Tour();
        tour.setName("barca");
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTour(tour);
        booking.setBookingDate(LocalDate.of(2026, 10, 15));
        booking.setNumPeople(3);

        emailService.inviaRifiuto(booking);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mail).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("test@test.com", msg.getTo()[0]);
        assertTrue(msg.getText().contains("rifiutata"));
}
}
