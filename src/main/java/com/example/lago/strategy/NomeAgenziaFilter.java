package com.example.lago.strategy;
 
import com.example.lago.model.Tour;
import java.util.List;
import java.util.stream.Collectors;
 
// filtro per nome agenzia, case-insensitive
public class NomeAgenziaFilter implements TourFilterStrategy {
 
    private final String nomeAgenzia;
 
    public NomeAgenziaFilter(String nomeAgenzia) {
        this.nomeAgenzia = nomeAgenzia;
    }
 
    @Override
    public List<Tour> filtra(List<Tour> tours) {
        if (nomeAgenzia == null || nomeAgenzia.isBlank())
            return tours;
 
        return tours.stream()
            .filter(t -> t.getAgency().getName()
                .toLowerCase().contains(nomeAgenzia.toLowerCase()))
            .collect(Collectors.toList());
    }
}