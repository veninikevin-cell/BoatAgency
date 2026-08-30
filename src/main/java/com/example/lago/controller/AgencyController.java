package com.example.lago.controller;

import com.example.lago.dto.AgencyDTO;
import com.example.lago.dto.TourDTO;
import com.example.lago.model.Agency;
import com.example.lago.model.Booking;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.service.AgencyService;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// controller MVC riservato ai fornitori: dashboard, gestione agenzia e tour.
// rotte protette da ruolo PROVIDER, vedi SecurityConfig
@Controller
@RequestMapping("/provider")
public class AgencyController {

    private final AgencyService agencyService;
    private final UserService userService;
    private final BookingService bookingService;

    public AgencyController(AgencyService agencyService,
                             UserService userService,
                             BookingService bookingService) {
        this.agencyService = agencyService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getCurrentUser(userDetails);
        Optional<Agency> agency = agencyService.findByOwner(user);

        model.addAttribute("user", user);
        model.addAttribute("hasAgency", agency.isPresent());

        agency.ifPresent(ag -> {
            List<Tour> tours = agencyService.getToursByAgency(ag);
            List<Booking> bookings = tours.stream()
                .flatMap(t -> bookingService.getBookingsByTour(t).stream())
                .toList();

            model.addAttribute("agency", ag);
            model.addAttribute("tours", tours);
            model.addAttribute("bookings", bookings);
            model.addAttribute("totalePrenotazioni", bookings.size());

            model.addAttribute("confermate", contaStato(bookings, Booking.Status.CONFIRMED));
            model.addAttribute("inAttesa", contaStato(bookings, Booking.Status.PENDING));
            model.addAttribute("rifiutate", contaStato(bookings, Booking.Status.REJECTED));
        });

        return "provider/dashboard";
    }

    // estratto per evitare 3 stream identici sopra in dashboard()
    private long contaStato(List<Booking> bookings, Booking.Status stato) {
        return bookings.stream().filter(b -> b.getStatus() == stato).count();
    }

    @GetMapping("/agency")
    public String agencyForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getCurrentUser(userDetails);
        Optional<Agency> agency = agencyService.findByOwner(user);

        AgencyDTO dto = new AgencyDTO();
        agency.ifPresent(ag -> {
            dto.setName(ag.getName());
            dto.setDescription(ag.getDescription());
            dto.setZone(ag.getZone());
            dto.setPhone(ag.getPhone());
            dto.setWebsite(ag.getWebsite());
            dto.setProfileImageUrl(ag.getProfileImageUrl());
            dto.setLatitude(ag.getLatitude());
            dto.setLongitude(ag.getLongitude());
        });

        model.addAttribute("dto", dto);
        model.addAttribute("hasAgency", agency.isPresent());
        return "provider/agency-form";
    }

    @PostMapping("/agency")
public String saveAgency(@AuthenticationPrincipal UserDetails userDetails,
                          @ModelAttribute AgencyDTO dto, Model model) {
    User user = userService.getCurrentUser(userDetails);
    try {
        if (agencyService.hasAgency(user)) {
            agencyService.updateAgency(dto, user);
        } else {
            agencyService.createAgency(dto, user);
        }
        return "redirect:/provider/dashboard";
        }catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dto", dto);      // ← AGGIUNGI
            model.addAttribute("hasAgency", agencyService.hasAgency(user));  // ← AGGIUNGI (il template lo usa)
            return "provider/agency-form";
        }
    }

    @GetMapping("/tours/new")
    public String tourForm(Model model) {
        model.addAttribute("dto", new TourDTO());
        return "provider/tour-form";
    }

   @PostMapping("/tours/new")
    public String saveTour(@AuthenticationPrincipal UserDetails userDetails,
                           @ModelAttribute TourDTO dto, Model model) {
        User user = userService.getCurrentUser(userDetails);
        try {
            agencyService.addTour(dto, user);
            return "redirect:/provider/dashboard";
        }catch(IllegalArgumentException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("dto", dto);  
        return "provider/tour-form";
        }
    }

    // l'autorizzazione (il tour deve appartenere all'agenzia) è già verificata nel service
    @PostMapping("/tours/delete/{id}")
    public String deleteTour(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        agencyService.deleteTour(id, userService.getCurrentUser(userDetails));
        return "redirect:/provider/dashboard";
    }}