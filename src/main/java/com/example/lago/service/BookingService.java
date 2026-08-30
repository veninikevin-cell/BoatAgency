package com.example.lago.service;

import com.example.lago.dto.BookingDTO;
import com.example.lago.exception.BookingNotFoundException;
import com.example.lago.exception.TourNotFoundException;
import com.example.lago.exception.UnauthorizedActionException;
import com.example.lago.exception.BookingNotAllowedException;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.repository.BookingRepository;
import com.example.lago.repository.TourRepository;
import com.example.lago.threading.BookingEvent;
import com.example.lago.threading.BookingQueue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// ciclo di vita prenotazioni - ogni cambio stato passa da BookingEventManager
// che notifica email + websocket (vedi observer/ per l'implementazione)
@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final TourRepository tourRepository;
    private final BookingQueue bookingQ;

    public BookingService(BookingRepository bookingRepo,
                          TourRepository tourRepository,
                          BookingQueue bookingQ) {
        this.bookingRepo = bookingRepo;
        this.tourRepository = tourRepository;
        this.bookingQ = bookingQ;
    }

    
    // Accoda un evento di prenotazione per la notifica asincrona.
    // L'accodamento non fallisce mai la richiesta dell'utente: in caso
    // di interruzione la prenotazione resta comunque salvata.
     
    private void accodaEvento(Booking booking, String evento) {
        try {
            bookingQ.produci(new BookingEvent(booking, evento));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private Booking trovaBooking(Long id) {
        return bookingRepo.findById(id)
            .orElseThrow(() -> new BookingNotFoundException(id));
    }

    // usato da confirm/reject - i due metodi differiscono solo per stato finale e nome evento
    private Booking cambiaStato(Long id, Booking.Status nuovoStato, String evento) {
        Booking booking = trovaBooking(id);
        booking.setStatus(nuovoStato);
        bookingRepo.save(booking);
        accodaEvento(booking, evento);
        return booking;
    }

    public Booking createBooking(BookingDTO dto, User user) {
        //long inizio = System.currentTimeMillis(); //per grafico mt
        Tour tour = tourRepository.findById(dto.getTourId())
            .orElseThrow(() -> new TourNotFoundException(dto.getTourId()));

        Booking booking = new Booking(user, tour, dto.getBookingDate(), dto.getNumPeople());
        booking.setNotes(dto.getNotes());
        bookingRepo.save(booking);

        accodaEvento(booking, "CREATA");
        //System.out.println("!!!!![MISURA] richiesta:  " + (System.currentTimeMillis() - inizio) + " ms"); //per grafico mt
        return booking;
    }

    public Booking confirmBooking(Long id, User provider) {
        return cambiaStato(id, Booking.Status.CONFIRMED, "CONFERMATA");
    }

    public Booking rejectBooking(Long id, User provider) {
        return cambiaStato(id, Booking.Status.REJECTED, "RIFIUTATA");
    }

    // Annulla una prenotazione propria, solo se ancora in attesa. 
    public Booking cancelBooking(Long id, User user) {
        Booking booking = trovaBooking(id);

        if (!booking.getUser().getId().equals(user.getId()))
            throw new UnauthorizedActionException("non puoi annullare questa prenotazione");

        if (booking.getStatus() != Booking.Status.PENDING) {
            throw new BookingNotAllowedException("è possibile annullare solo prenotazioni ancora in attesa di conferma");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        return bookingRepo.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepo.findByUser(user);
    }

    public List<Booking> getBookingsByTour(Tour tour) {
        return bookingRepo.findByTour(tour);
    }

    public List<Booking> getConfirmedBookingsForTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
            .orElseThrow(() -> new TourNotFoundException(tourId));
        return bookingRepo.findByTourAndStatus(tour, Booking.Status.CONFIRMED);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepo.findById(id);
    }

    public Optional<Tour> findTourById(Long id) {
        return tourRepository.findById(id);
    }
}