package com.example.lago.service;

import com.example.lago.repository.BookingRepository;
import com.example.lago.repository.TourRepository;
import com.example.lago.threading.BookingEvent;
import com.example.lago.threading.BookingQueue;
import com.example.lago.exception.BookingNotFoundException;
import com.example.lago.exception.BookingNotAllowedException;
import com.example.lago.exception.TourNotFoundException;
import com.example.lago.exception.UnauthorizedActionException;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.dto.BookingDTO;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


 //Unit test per la classe BookingService
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TourRepository tourRepository;
    @Mock
    private BookingQueue bookingQueue;

    @InjectMocks
    private BookingService bookingService;

    // TEST per il metodo createBooking 
    @Test
    void createBooking_tourNotFound(){
        BookingDTO dto = new BookingDTO();
        dto.setTourId(1L);
        User user = new User();

        when(tourRepository.findById(dto.getTourId())).thenReturn(Optional.empty());
        assertThrows(TourNotFoundException.class, () -> bookingService.createBooking(dto, user));
    }

    @Test
    void createBooking_datiValidi_creaLaPrenotazione() throws InterruptedException {
        Long tourId = 1L;
        BookingDTO dto = new BookingDTO();
        dto.setTourId(tourId);
        dto.setNumPeople(3);
        dto.setBookingDate(java.time.LocalDate.now().plusDays(5));
        dto.setNotes("Nessuna allergia");

        User user = new User();
        Tour tour = new Tour("Giro lago", "desc", new java.math.BigDecimal("50"), 60, null);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));

        Booking risultato = bookingService.createBooking(dto, user);

        assertNotNull(risultato);
        assertEquals(3, risultato.getNumPeople());
        assertEquals("Nessuna allergia", risultato.getNotes());

        verify(bookingQueue, times(1)).produci(any(BookingEvent.class));
    }

    @Test
    void createBooking_interruzione() throws InterruptedException {
        BookingDTO dto = new BookingDTO();
        User user = new User();
        Tour tour = new Tour("Giro lago", "desc", new java.math.BigDecimal("50"), 60, null);
        dto.setTourId(1L);

        when(tourRepository.findById(dto.getTourId())).thenReturn(Optional.of(tour));
        doThrow(new InterruptedException()).when(bookingQueue).produci(any(BookingEvent.class));

        Booking risultato = bookingService.createBooking(dto, user);

        assertNotNull(risultato);
    }

     //TEST per il metodo confirmBooking.
    @Test
    void confirmBooking_bookingNotFound(){
        Long id = 1L;
        User provider = new User();

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () ->
        bookingService.confirmBooking(id, provider));
    }

    @Test
    void confirmBooking_bookingFounded() throws InterruptedException {
        Long id = 1L;
        User provider = new User();
        Booking booking = new Booking();
        booking.setId(id);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        Booking risultato = bookingService.confirmBooking(id,provider);
        
        assertNotNull(risultato);
        assertEquals(booking, risultato);
        assertEquals(Booking.Status.CONFIRMED, risultato.getStatus());
        
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingQueue, times(1)).produci(any(BookingEvent.class));
    }

    //TEST per il metodo rejectBooking
    @Test
    void rejectBooking_bookingNotFound(){
        Long id = 1L;
        User provider = new User();

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () ->
        bookingService.rejectBooking(id,provider));
    }

    @Test
    void rejectBooking_bookingFounded() throws InterruptedException {
        Long id = 1L;
        User provider = new User();
        Booking booking = new Booking();
        booking.setId(id);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        Booking result = bookingService.rejectBooking(id,provider);

        assertNotNull(result);
        assertEquals(booking, result);
        assertEquals(Booking.Status.REJECTED, result.getStatus());
        
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingQueue, times(1)).produci(any(BookingEvent.class));
    }

    // casi di TEST per il metodo cancelBooking
    @Test
    void cancelBooking_bookingNotFound(){
        Long id = 1L;
        User user = new User();

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () ->
        bookingService.cancelBooking(id,user));
    }

    @Test
    void cancelBooking_Unauthorized(){
        Long id = 3L; //id della prenotazione da cancellare
        Long id1 = 1L;
        Long id2 = 2L;
        User user1 = new User();
        User user2 = new User();
        user1.setId(id1);
        user2.setId(id2);
        Booking booking = new Booking();
        booking.setId(id);
        booking.setUser(user2);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        assertThrows(UnauthorizedActionException.class, () ->
        bookingService.cancelBooking(id,user1));
    }

    @Test
    void cancelBooking_IllegalArgument(){
        Long idB = 3L;
        Long idU = 1L;
        User user = new User();
        user.setId(idU);
        Booking booking = new Booking();
        booking.setId(idB);
        booking.setUser(user);
        booking.setStatus(Booking.Status.CONFIRMED);

        when(bookingRepository.findById(idB)).thenReturn(Optional.of(booking));
        assertThrows(BookingNotAllowedException.class, () ->
        bookingService.cancelBooking(idB,user));
    }

    @Test
    void cancelBooking_succesfull(){
        Long idB = 3L;
        Long idU = 1L;
        User user = new User();
        user.setId(idU);
        Booking booking = new Booking();
        booking.setId(idB);
        booking.setUser(user);
        booking.setStatus(Booking.Status.PENDING);

        when(bookingRepository.findById(idB)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.cancelBooking(idB,user);

        assertNotNull(result);
        assertEquals(booking,result);
        assertEquals(Booking.Status.CANCELLED,result.getStatus());
        
        verify(bookingRepository, times(1)).save(booking);
    }

    //  TEST per il metodo getBookingsByUser
    @Test
    void getBookingsByUser_successful(){
        User user = new User();
        user.setId(1L);
        Booking booking = new Booking();
        booking.setUser(user);

        when(bookingRepository.findByUser(user)).thenReturn(java.util.List.of(booking));

        List<Booking> result = bookingService.getBookingsByUser(user);

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking,result.get(0));
    }

    // TEST per getBookingsByTour
    @Test
    void getBookingsByTour_successful(){
        Tour tour = new Tour();
        Booking booking = new Booking();
        booking.setTour(tour);

        when(bookingRepository.findByTour(tour)).thenReturn(java.util.List.of(booking));

        List<Booking> result = bookingService.getBookingsByTour(tour);

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking,result.get(0));
    }

    // casi di TEST per il metodo getConfirmedBookingsForTour
    @Test
    void getConfirmedBookingsForTour_tourNotFound(){
        Long tourId = 1L;

        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        assertThrows(TourNotFoundException.class, () ->
        bookingService.getConfirmedBookingsForTour(tourId));
    }

    @Test
    void getConfirmedBookingsForTour_successful(){
        Long tourId = 1L;
        Tour tour = new Tour();
        tour.setId(tourId);
        Booking booking = new Booking();
        booking.setStatus(Booking.Status.CONFIRMED);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(bookingRepository.findByTourAndStatus(tour, Booking.Status.CONFIRMED))
        .thenReturn(java.util.List.of(booking));

        List<Booking> result = bookingService.getConfirmedBookingsForTour(tourId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
        assertEquals(Booking.Status.CONFIRMED, result.get(0).getStatus());
    }

     //TEST per findById
     //qui (e per findTourById_founded) c'é ridondanza ai fini della coverage delle righe

    @Test
    void findById_notFound(){
        Long id= 1L;

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Booking> result = bookingService.findById(id);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_founded(){
        Long id = 1L;
        Booking booking = new Booking();
        booking.setId(id);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.findById(id);
        
        assertNotNull(result);
        assertEquals(result.get(),booking);
    }

    // casi di TEST per findTourById 
    @Test
    void findTourById_tourTrovato() {
        Long id = 1L;
        Tour tour = new Tour();
        tour.setId(id);

        when(tourRepository.findById(id)).thenReturn(Optional.of(tour));

        Optional<Tour> result = bookingService.findTourById(id);

        assertTrue(result.isPresent());
        assertEquals(tour, result.get());
    }

    @Test
    void findTourById_tourNonTrovato() {
        Long id = 99L;

        when(tourRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Tour> result = bookingService.findTourById(id);

        assertTrue(result.isEmpty());
    }
}