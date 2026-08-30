package com.example.lago.service;

import com.example.lago.dto.ReviewDTO;
import com.example.lago.exception.AgencyNotFoundException;
import com.example.lago.exception.ReviewNotAllowedException;
import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Review;
import com.example.lago.model.User;
import com.example.lago.repository.AgencyRepository;
import com.example.lago.repository.BookingRepository;
import com.example.lago.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// creazione e consultazione recensioni
@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final AgencyRepository agencyRepo;
    private final BookingRepository bookingRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         AgencyRepository agencyRepo,
                         BookingRepository bookingRepo) {
        this.reviewRepo = reviewRepo;
        this.agencyRepo = agencyRepo;
        this.bookingRepo = bookingRepo;
    }

    // usata sia da canReview che da addReview
    private boolean haCompletatoTourConAgenzia(User user, Agency agency) {
        List<Booking> prenotazioni = bookingRepo.findByUser(user);
        return prenotazioni.stream()
            .filter(b -> b.getStatus() == Booking.Status.CONFIRMED)
            .filter(b -> b.getTour().getAgency().getId().equals(agency.getId()))
            .anyMatch(b -> !b.getBookingDate().isAfter(LocalDate.now()));
    }

    public Review addReview(ReviewDTO dto, User user) {
        Agency agency = agencyRepo.findById(dto.getAgencyId())
            .orElseThrow(() -> new AgencyNotFoundException(dto.getAgencyId()));

        if (reviewRepo.existsByUserAndAgency(user, agency))
            throw new IllegalArgumentException("Hai già lasciato una recensione per questa agenzia");

        if (!haCompletatoTourConAgenzia(user, agency)) {
            throw new ReviewNotAllowedException("nessun tour completato con questa agenzia");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Il voto deve essere tra 1 e 5");
        }

        return reviewRepo.save(new Review(user, agency, dto.getRating(), dto.getComment()));
    }

    public List<Review> getReviewsByAgency(Agency agency) {
        return reviewRepo.findByAgencyOrderByCreatedAtDesc(agency);
    }

    public double getAverageRating(Agency agency) {
        List<Review> reviews = getReviewsByAgency(agency);
        return reviews.isEmpty() ? 0.0
            : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public boolean canReview(User user, Agency agency) {
        return !reviewRepo.existsByUserAndAgency(user, agency)
            && haCompletatoTourConAgenzia(user, agency);
    }
}