package com.example.lago.controller;

import com.example.lago.dto.BookingDTO;
import com.example.lago.exception.TourNotFoundException;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// ciclo di vita prenotazione lato controller: creazione, consultazione,
// conferma/rifiuto (provider) e cancellazione (utente). validazione business tutta nel service
@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/new/{tourId}")
    public String bookingForm(@PathVariable Long tourId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User user = userService.getCurrentUser(userDetails);

        Tour tour = bookingService.findTourById(tourId)
            .orElseThrow(() -> new TourNotFoundException(tourId));

        // date già confermate, usate lato client per disabilitare i giorni non prenotabili
        List<LocalDate> dateOccupate = bookingService.getConfirmedBookingsForTour(tourId)
            .stream()
            .map(Booking::getBookingDate)
            .collect(Collectors.toList());

        BookingDTO dto = new BookingDTO();
        dto.setTourId(tourId);

        model.addAttribute("dto", dto);
        model.addAttribute("tourId", tourId);
        model.addAttribute("dateOccupate", dateOccupate);
        model.addAttribute("availableDays", tour.getAvailableDays() != null ? tour.getAvailableDays() : "");
        model.addAttribute("user", user);

        return "booking/booking-form";
    }

    @PostMapping("/new/{tourId}")
    public String createBooking(@PathVariable Long tourId,
                                 @ModelAttribute BookingDTO dto,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        User user = userService.getCurrentUser(userDetails);
        dto.setTourId(tourId);

        try {
            bookingService.createBooking(dto, user);
            return "redirect:/bookings/my";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dto", dto);
            return "booking/booking-form";
        }
    }

    @GetMapping("/my")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("bookings", bookingService.getBookingsByUser(userService.getCurrentUser(userDetails)));
        return "booking/my-bookings";
    }

    @PostMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.confirmBooking(id, userService.getCurrentUser(userDetails));
        return "redirect:/provider/dashboard";
    }

    // stesse regole di confirmBooking
    @PostMapping("/reject/{id}")
    public String rejectBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.rejectBooking(id, userService.getCurrentUser(userDetails));
        return "redirect:/provider/dashboard";
    }
    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.cancelBooking(id, userService.getCurrentUser(userDetails));
        return "redirect:/bookings/my";
    }
}