package com.example.lago.strategy;
 
import com.example.lago.model.Tour;
import java.util.List;
 
// Strategy per il filtraggio dei tour - ogni implementazione è un criterio
// di ricerca diverso
public interface TourFilterStrategy {
 
    List<Tour> filtra(List<Tour> tours);
}
 