package com.example.lago.service;

import com.example.lago.dto.ReviewDTO;
import com.example.lago.exception.AgencyNotFoundException;
import com.example.lago.exception.ReviewNotAllowedException;
import com.example.lago.model.Agency;
import com.example.lago.model.User;
import com.example.lago.model.Booking;
import com.example.lago.model.Review;
import com.example.lago.model.Tour;
import com.example.lago.repository.AgencyRepository;
import com.example.lago.repository.ReviewRepository;
import com.example.lago.repository.BookingRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.List;


 //Unit test per la classe ReviewService

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AgencyRepository agencyRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReviewService reviewService;
    // casi di TEST per addReview 
    @Test
    void addReview_agenziaNotFounded(){
        ReviewDTO dto = new ReviewDTO();
        User user = new User();
        Long agencyId = 2L;
        dto.setAgencyId(agencyId);

        when(agencyRepository.findById(agencyId)).thenReturn(Optional.empty());

        assertThrows(AgencyNotFoundException.class, () ->
        reviewService.addReview(dto, user));
    }

    @Test
    void addReview_RecensioneGiaLasciata(){
        ReviewDTO dto = new ReviewDTO();
        User user = new User();
        Long agencyId = 2L;
        dto.setAgencyId(agencyId);
        Agency agency = new Agency();
        agency.setId(agencyId);
    

        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(agency));
        when(reviewRepository.existsByUserAndAgency(user,agency)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
             reviewService.addReview(dto,user));
    }

    @Test
    void addReview_soloPrenotazionePending_lanciaReviewNotAllowed(){
        Long agencyId = 2L;
        ReviewDTO dto = new ReviewDTO();
        dto.setAgencyId(agencyId);
        User user = new User();
        Agency agency = new Agency();
        agency.setId(agencyId);

        Tour tour = new Tour("nome", "desc", new java.math.BigDecimal("50"), 60, agency);
        Booking booking = new Booking(user, tour, java.time.LocalDate.now().minusDays(1), 2);
        booking.setStatus(Booking.Status.PENDING); // non CONFIRMED, quindi viene scartato dal filtro

        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(agency));
        when(reviewRepository.existsByUserAndAgency(user, agency)).thenReturn(false);
        when(bookingRepository.findByUser(user)).thenReturn(java.util.List.of(booking));

        assertThrows(ReviewNotAllowedException.class, () ->
        reviewService.addReview(dto, user));
    }

    @Test
    void addReview_votoNonValido(){
        Long agencyId = 2L;
        ReviewDTO dto = new ReviewDTO();
        dto.setAgencyId(agencyId);
        User user = new User();
        Agency agency = new Agency();
        agency.setId(agencyId);

        Tour tour= new Tour("nome", "desc", new java.math.BigDecimal("50"), 60, agency);
        Booking booking = new Booking(user, tour, java.time.LocalDate.now().minusDays(1), 2);
        booking.setStatus(Booking.Status.CONFIRMED);
        dto.setRating(7);

        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(agency));
        when(reviewRepository.existsByUserAndAgency(user, agency)).thenReturn(false);
        when(bookingRepository.findByUser(user)).thenReturn(java.util.List.of(booking));

        assertThrows(IllegalArgumentException.class, ()->
        reviewService.addReview(dto, user));
    }

    @Test
    void addReview_succcesso(){
        Long agencyId = 2L;
        ReviewDTO dto = new ReviewDTO();
        dto.setAgencyId(agencyId);
        User user = new User();
        Agency agency = new Agency();
        agency.setId(agencyId);
        dto.setComment("B");

        Tour tour= new Tour("nome", "desc", new java.math.BigDecimal("50"), 60, agency);
        Booking booking = new Booking(user, tour, java.time.LocalDate.now().minusDays(1), 2);
        booking.setStatus(Booking.Status.CONFIRMED);
        dto.setRating(3);

        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(agency));
        when(reviewRepository.existsByUserAndAgency(user, agency)).thenReturn(false);
        when(bookingRepository.findByUser(user)).thenReturn(java.util.List.of(booking));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.addReview(dto, user);

        assertNotNull(result);
        assertEquals(3, result.getRating());                 
        assertEquals("B", result.getComment());
        assertEquals(agency, result.getAgency());
        assertEquals(user, result.getUser());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // TEST per getReviewsByAgency
    @Test
    void getReviewsByAgency_restituisceLeRecensioni() {
        Agency agency = new Agency();
        agency.setId(1L);
        Review review = new Review();

        when(reviewRepository.findByAgencyOrderByCreatedAtDesc(agency))
        .thenReturn(java.util.List.of(review));

        List<Review> result = reviewService.getReviewsByAgency(agency);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(review, result.get(0));
    }

// TEST per getAverageRating 
    @Test
    void getAverageRating_nessunaRecensione_restituisceZero() {
        Agency agency = new Agency();
        agency.setId(1L);

        when(reviewRepository.findByAgencyOrderByCreatedAtDesc(agency))
        .thenReturn(java.util.List.of());

        double media = reviewService.getAverageRating(agency);

        assertEquals(0.0, media);
    }

    @Test
    void getAverageRating_conRecensioni_calcolaLaMediaCorretta() {
        Agency agency = new Agency();
        agency.setId(1L);

        Review review1 = new Review();
        review1.setRating(4);
        Review review2 = new Review();
        review2.setRating(2);

        when(reviewRepository.findByAgencyOrderByCreatedAtDesc(agency))
        .thenReturn(java.util.List.of(review1, review2));

        double media = reviewService.getAverageRating(agency);

        assertEquals(3.0, media); 
    }

// TEST per canReview
    @Test
    void canReview_giaRecensita_restituisceFalse() {
        User user = new User();
        Agency agency = new Agency();
        agency.setId(1L);

        when(reviewRepository.existsByUserAndAgency(user, agency)).thenReturn(true);

        assertFalse(reviewService.canReview(user, agency));
    }

    @Test
    void canReview_nessunTourCompletato_restituisceFalse() {
        User user = new User();
        Agency agency = new Agency();
        agency.setId(1L);

        when(reviewRepository.existsByUserAndAgency(user, agency)).thenReturn(false);
        when(bookingRepository.findByUser(user)).thenReturn(java.util.List.of());

        assertFalse(reviewService.canReview(user, agency));
    }

    @Test
    void canReview_tourCompletatoEnonRecensita_restituisceTrue() {
        User user = new User();
        Agency agency = new Agency();
        agency.setId(1L);

        Tour tour = new Tour("nome", "desc", new java.math.BigDecimal("50"), 60, agency);
        Booking booking = new Booking(user, tour, java.time.LocalDate.now().minusDays(1), 2);
        booking.setStatus(Booking.Status.CONFIRMED);

        when(reviewRepository.existsByUserAndAgency(user, agency)).thenReturn(false);
        when(bookingRepository.findByUser(user)).thenReturn(java.util.List.of(booking));

        assertTrue(reviewService.canReview(user, agency));
    }
}