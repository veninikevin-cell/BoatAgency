package com.example.lago.strategy;
 
import com.example.lago.model.Tour;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
 
// filtro per prezzo massimo (inclusivo - <=)
public class PrezzoFilter implements TourFilterStrategy {
 
    private final BigDecimal maxPrezzo;
 
    public PrezzoFilter(BigDecimal maxPrezzo) {
        this.maxPrezzo = maxPrezzo;
    }
 
    @Override
    public List<Tour> filtra(List<Tour> tours) {
        
        if (maxPrezzo == null)
            return tours;
 
        return tours.stream()
            .filter(t -> t.getPrice().compareTo(maxPrezzo) <= 0)
            .collect(Collectors.toList());
    }
}