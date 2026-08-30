package com.example.lago.controller;

import com.example.lago.dto.ReviewDTO;
import com.example.lago.exception.AgencyNotFoundException;
import com.example.lago.model.Agency;
import com.example.lago.model.User;
import com.example.lago.service.AgencyService;
import com.example.lago.service.ReviewService;
import com.example.lago.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// pagina pubblica agenzia, accessibile anche senza login: tour, recensioni
// e form per lasciarne una nuova se l'utente ne ha diritto
@Controller
@RequestMapping("/public")
public class PublicController {

    private final AgencyService agencyService;
    private final ReviewService reviewService;
    private final UserService userService;

    public PublicController(AgencyService agencyService,
                             ReviewService reviewService,
                             UserService userService) {
        this.agencyService = agencyService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // riusata da agencyPage e dal catch di saveReview
    private void popolaModelloAgenzia(Agency agency, Model model) {
        model.addAttribute("agency", agency);
        model.addAttribute("reviews", reviewService.getReviewsByAgency(agency));
        model.addAttribute("media", reviewService.getAverageRating(agency));
        model.addAttribute("tours", agencyService.getToursByAgency(agency));
    }

    @GetMapping("/agency/{id}")
    public String agencyPage(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        Agency agency = agencyService.findById(id)
            .orElseThrow(() -> new AgencyNotFoundException(id));

        popolaModelloAgenzia(agency, model);
        model.addAttribute("dto", new ReviewDTO());

        // userDetails può essere null (pagina accessibile anche a utenti anonimi)
        boolean canReview = false;
        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
            canReview = user != null && reviewService.canReview(user, agency);
        }
        model.addAttribute("canReview", canReview);

        return "agency/agency-page";
    }

    @PostMapping("/agency/{id}/review")
    public String saveReview(@PathVariable Long id,
                              @ModelAttribute ReviewDTO dto,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        User user = userService.getCurrentUser(userDetails);
        dto.setAgencyId(id);

        try {
            reviewService.addReview(dto, user);
            return "redirect:/public/agency/" + id;
        } catch (Exception e) {
            // se siamo qui l'utente è autenticato
            Agency agency = agencyService.findById(id).orElseThrow(() -> new AgencyNotFoundException(id));
            popolaModelloAgenzia(agency, model);
            model.addAttribute("dto", dto);
            boolean canReview = reviewService.canReview(user, agency);
            model.addAttribute("canReview", canReview);
            model.addAttribute("error", e.getMessage());
            return "agency/agency-page";
        }
    }
}