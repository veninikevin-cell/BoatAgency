package com.example.lago.service;

import com.example.lago.dto.AgencyDTO;
import com.example.lago.dto.TourDTO;
import com.example.lago.exception.AgencyNotFoundException;
import com.example.lago.exception.TourNotFoundException;
import com.example.lago.exception.UnauthorizedActionException;
import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.repository.AgencyRepository;
import com.example.lago.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// gestione agenzie/tour: creazione profilo, pubblicazione tour, cancellazioni
@Service
public class AgencyService {

    private final AgencyRepository agencyRepo;
    private final TourRepository tourRepository;

    public AgencyService(AgencyRepository agencyRepo, TourRepository tourRepository) {
        this.agencyRepo = agencyRepo;
        this.tourRepository = tourRepository;
    }

    // Crea l'agenzia del fornitore. Un fornitore può avere una sola agenzia 
    public Agency createAgency(AgencyDTO dto, User owner) {
        if (agencyRepo.existsByOwner(owner)) {
            throw new IllegalArgumentException("Hai già registrato un'agenzia");
        }

        Agency agency = new Agency(dto.getName(), dto.getDescription(), dto.getZone(), dto.getPhone(), owner);
        agency.setWebsite(dto.getWebsite());
        agency.setProfileImageUrl(dto.getProfileImageUrl());
        agency.setLatitude(dto.getLatitude());
        agency.setLongitude(dto.getLongitude());

        return agencyRepo.save(agency);
    }

    public Agency updateAgency(AgencyDTO dto, User owner) {
        Agency agency = agencyRepo.findByOwner(owner)
                .orElseThrow(() -> new AgencyNotFoundException("Agenzia non trovata"));

        agency.setName(dto.getName());
        agency.setDescription(dto.getDescription());
        agency.setZone(dto.getZone());
        agency.setPhone(dto.getPhone());
        agency.setWebsite(dto.getWebsite());
        agency.setProfileImageUrl(dto.getProfileImageUrl());
        agency.setLatitude(dto.getLatitude());
        agency.setLongitude(dto.getLongitude());

        return agencyRepo.save(agency);
    }

    // Aggiunge un nuovo tour 
    public Tour addTour(TourDTO dto, User owner) {
        Agency agency = agencyRepo.findByOwner(owner)
                .orElseThrow(() -> new AgencyNotFoundException("Devi prima creare un profilo agenzia"));

        Tour tour = new Tour(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getDurationMinutes(), agency);
        tour.setZoneDeparture(dto.getZoneDeparture());
        tour.setTimeSlot(dto.getTimeSlot());
        tour.setAvailableDays(dto.getAvailableDays());

        return tourRepository.save(tour);
    }

    public void deleteTour(Long tourId, User owner) {
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new TourNotFoundException(tourId));
        Agency agency = agencyRepo.findByOwner(owner).orElseThrow(() -> new AgencyNotFoundException("Agenzia non trovata"));

        if (!tour.getAgency().getId().equals(agency.getId()))
            throw new UnauthorizedActionException("il tour non appartiene alla tua agenzia");

        tourRepository.delete(tour);
    }

    public Optional<Agency> findByOwner(User owner) {
        return agencyRepo.findByOwner(owner);
    }

    public boolean hasAgency(User owner) {
        return agencyRepo.existsByOwner(owner);
    }

    public List<Agency> getAllAgencies() {
        return agencyRepo.findAll();
    }

    public List<Tour> getToursByAgency(Agency agency) {
        return tourRepository.findByAgency(agency);
    }

    public Optional<Agency> findById(Long id) {
        return agencyRepo.findById(id);
    }
}