package com.example.lago.controller;

import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import com.example.lago.service.AgencyService;
import com.example.lago.strategy.NomeAgenziaFilter;
import com.example.lago.strategy.PrezzoFilter;
import com.example.lago.strategy.TourFilterStrategy;
import com.example.lago.strategy.ZonaFilter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// home page pubblica - filtri tour tramite Strategy (vedi package strategy/)
@Controller
public class HomeController {

    private final AgencyService agencyService;

    public HomeController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

   @GetMapping("/")
public String home(
        @RequestParam(required = false) String zona,
        @RequestParam(required = false) BigDecimal maxPrezzo,
        @RequestParam(required = false) String nomeAgenzia,
        Model model) {

    List<Agency> agenzie = agencyService.getAllAgencies();

    List<Tour> tours = agenzie.stream()
        .flatMap(a -> agencyService.getToursByAgency(a).stream())
        .collect(Collectors.toList());

    // applica le strategie in sequenza (ordine non rilevante, sono filtri indipendenti)
    List<TourFilterStrategy> filtri = new ArrayList<>();
    filtri.add(new ZonaFilter(zona));
    filtri.add(new PrezzoFilter(maxPrezzo));
    filtri.add(new NomeAgenziaFilter(nomeAgenzia));

    for (TourFilterStrategy filtro : filtri) {
        tours = filtro.filtra(tours);
    }

    model.addAttribute("agenzie", agenzie);
    model.addAttribute("tours", tours);
    model.addAttribute("zona", zona);
    model.addAttribute("maxPrezzo", maxPrezzo);
    model.addAttribute("nomeAgenzia", nomeAgenzia);

    return "index";
}

    // usato dalla mappa in home per i marker delle agenzie - solo quelle con coordinate
    @GetMapping("/api/agenzie")
@ResponseBody
public List<Map<String, Object>> getAgenziePosizioni() {
    return agencyService.getAllAgencies().stream()
        .filter(a -> a.getLatitude() != null && a.getLongitude() != null)
        .map(a -> {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", a.getId());
            m.put("name", a.getName());
            m.put("zone", a.getZone());
            m.put("latitude", a.getLatitude());
            m.put("longitude", a.getLongitude());
            return m;
        })
        .collect(java.util.stream.Collectors.toList());
}
}