package com.example.lago.strategy;

import com.example.lago.model.Tour;
import java.util.List;
import java.util.stream.Collectors;

// filtro per zona di partenza 

public class ZonaFilter implements TourFilterStrategy {

    private final String zona;

    public ZonaFilter(String zona) {
        this.zona = zona;
    }

    @Override
    public List<Tour> filtra(List<Tour> tours) {
        if (zona == null || zona.isBlank() || zona.equals("Tutte"))
            return tours;

        return tours.stream()
            .filter(t -> t.getZoneDeparture().equalsIgnoreCase(zona))
            .collect(Collectors.toList());
    }
}