package com.example.lago.service;

import com.example.lago.dto.AgencyDTO;
import com.example.lago.dto.TourDTO;
import com.example.lago.model.Agency;
import com.example.lago.model.Tour;
import com.example.lago.model.User;
import com.example.lago.repository.AgencyRepository;
import com.example.lago.repository.TourRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.lago.exception.AgencyNotFoundException;
import com.example.lago.exception.TourNotFoundException;
import com.example.lago.exception.UnauthorizedActionException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit test per la classe AgencyService
@ExtendWith(MockitoExtension.class)
public class AgencyServiceTest {
    @Mock
    private AgencyRepository agencyRepository;
    @Mock
    private TourRepository tourRepository;
    @InjectMocks
    private AgencyService agencyService;
    // test per createAgency 
    @Test
    void createAgency_utenteConAgenzia(){
        User owner = new User();
        AgencyDTO dto = new AgencyDTO();
        when(agencyRepository.existsByOwner(owner)).thenReturn(true);
        assertThrows(IllegalArgumentException.class,()->
            agencyService.createAgency(dto, owner)
        );

     }

    @Test
    void createAgency_utenteSenzaAgenzia_creaLagenzia() {
        // ARRANGE
        User owner = new User();

        AgencyDTO dto = new AgencyDTO();
        dto.setName("Como Boat Experience");
        dto.setDescription("I migliori tour del lago");
        dto.setZone("Como");
        dto.setPhone("031000000");

        // l'utente NON ha ancora un'agenzia
        when(agencyRepository.existsByOwner(owner)).thenReturn(false);
        // il finto database, quando "salva", restituisce lo stesso oggetto che gli è stato passato
        when(agencyRepository.save(any(Agency.class))).thenAnswer(i-> i.getArgument(0));

        Agency risultato = agencyService.createAgency(dto, owner);

        assertNotNull(risultato);
        assertEquals("Como Boat Experience", risultato.getName());
        assertEquals("I migliori tour del lago", risultato.getDescription());
        assertEquals("Como", risultato.getZone());
        assertEquals("031000000", risultato.getPhone());

        // verifica che il salvataggio sia stato effettivamente invocato una volta
        verify(agencyRepository, times(1)).save(any(Agency.class));
}

// TEST per updateAgency
    @Test
    void updateAgency_agenziaNonTrovata(){
        AgencyDTO dto = new AgencyDTO();
        User owner = new User();
        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.empty());
        assertThrows(AgencyNotFoundException.class,()->
        agencyService.updateAgency(dto, owner));

    }

    @Test
    void updateAgency_agenziaTrovata(){
        AgencyDTO dto = new AgencyDTO();
        dto.setName("boat");
        dto.setDescription("tour");
        dto.setZone("como");
        dto.setPhone("031000000");
        User owner = new User();
        Agency agencyPre = new Agency("nomePre","descPre","zonePre","0000",owner);

        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.of(agencyPre));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(i-> i.getArgument(0));

        Agency result = agencyService.updateAgency(dto,owner);

        assertNotNull(result);
        assertEquals("boat",result.getName());
        assertEquals("tour",result.getDescription());
        assertEquals("como",result.getZone());
        assertEquals("031000000",result.getPhone());

        verify(agencyRepository, times(1)).save(any(Agency.class));


    }

// TEST per addTour 
    @Test
    void addTour_agenziaNonTrovata(){
        TourDTO dto = new TourDTO();
        User owner = new User();
        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.empty());
        assertThrows(AgencyNotFoundException.class,()->
        agencyService.addTour(dto, owner));
    }

    @Test
    void addTour_agenziaTrovata(){
        TourDTO dto = new TourDTO();
        dto.setName("tour");
        dto.setDescription("descrizione");
        dto.setPrice(new java.math.BigDecimal("100.00"));
        dto.setDurationMinutes(120);
        User owner = new User();
        Agency agency = new Agency("nome","desc","zone","0000",owner);
        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.of(agency));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i-> i.getArgument(0));

        Tour risultato = agencyService.addTour(dto, owner);

        assertNotNull(risultato);
        assertEquals("tour", risultato.getName());
        assertEquals("descrizione", risultato.getDescription());
        assertEquals(new java.math.BigDecimal("100.00"), risultato.getPrice());
        assertEquals(120, risultato.getDurationMinutes());

        verify(tourRepository, times(1)).save(any(Tour.class));

    }

// TEST per deleteTour
    @Test
    void deleteTour_TourNonTrovato(){
        Long tourId = 1L;
        User owner = new User();
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());
        assertThrows(TourNotFoundException.class,()->
        agencyService.deleteTour(tourId, owner));
    }

    @Test
    void deleteTour_agenziaNonTrovata(){
        Long tourId = 1L;
        User owner = new User();
        Tour tour = new Tour();

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour)); // il tour ESISTE
        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.empty()); // ma l'agenzia NON esiste
        assertThrows(AgencyNotFoundException.class,()->
        agencyService.deleteTour(tourId, owner));
    }
//tour non appartiene all'agenzia
    @Test
    void deleteTour_nonAutorizzato_lanciaUnauthorized() {
        Long tourId = 1L;
        User owner = new User();
        Agency agency1 = new Agency();
        Agency agency2 = new Agency();
        agency1.setId(1L);
        agency2.setId(2L);
        Tour tour = new Tour("nome", "desc", new java.math.BigDecimal("100.00"), 120, agency2); // tour appartiene ad un'altra agenzia

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.of(agency1));

        assertThrows(UnauthorizedActionException.class,
        () -> agencyService.deleteTour(tourId, owner));
    }


    @Test
    void deleteTour_Autorizzato(){
        Long tourId=2L;
        User owner = new User();
        Agency agency = new Agency();
        agency.setId(1L);
        Tour tour = new Tour("nome", "desc", new java.math.BigDecimal("100.00"), 120, agency);
        tour.setId(tourId);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.of(agency));

        agencyService.deleteTour(tourId, owner);
        
        verify(tourRepository, times(1)).delete(tour);
    }

// TEST per findByOwner
    @Test
    void findByOwner_restituisceLagenzia() {
        User owner = new User();
        Agency agency = new Agency("nome", "desc", "zona", "000", owner);

        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.of(agency));

        Optional<Agency> risultato = agencyService.findByOwner(owner);

        assertTrue(risultato.isPresent());
        assertEquals(agency, risultato.get());
    }

    @Test
    void findByOwner_nessunaAgenzia_restituisceOptionalVuoto() {
        User owner = new User();

        when(agencyRepository.findByOwner(owner)).thenReturn(Optional.empty());

        Optional<Agency> risultato = agencyService.findByOwner(owner);

        assertTrue(risultato.isEmpty());
    }

// TEST per hasAgency
    @Test
    void hasAgency_utenteConAgenzia_restituisceTrue() {
        User owner = new User();

        when(agencyRepository.existsByOwner(owner)).thenReturn(true);

        assertTrue(agencyService.hasAgency(owner));
    }

    @Test
    void hasAgency_utenteSenzaAgenzia_restituisceFalse() {
        User owner = new User();

        when(agencyRepository.existsByOwner(owner)).thenReturn(false);

        assertFalse(agencyService.hasAgency(owner));
    }

// TEST per getAllAgencies 
    @Test
    void getAllAgencies_restituisceLaListaCompleta() {
        Agency agency1 = new Agency("nome1", "desc1", "zona1", "111", new User());
        Agency agency2 = new Agency("nome2", "desc2", "zona2", "222", new User());

        when(agencyRepository.findAll()).thenReturn(java.util.List.of(agency1, agency2));

        java.util.List<Agency> risultato = agencyService.getAllAgencies();

        assertEquals(2, risultato.size());
        assertTrue(risultato.contains(agency1));
        assertTrue(risultato.contains(agency2));
    }

// TEST per getToursByAgency
    @Test
    void getToursByAgency_restituisceITourDellagenzia() {
        Agency agency = new Agency("nome", "desc", "zona", "000", new User());
        Tour tour1 = new Tour("tour1", "desc1", new java.math.BigDecimal("50"), 60, agency);
        Tour tour2 = new Tour("tour2", "desc2", new java.math.BigDecimal("80"), 90, agency);

        when(tourRepository.findByAgency(agency)).thenReturn(java.util.List.of(tour1, tour2));

        java.util.List<Tour> risultato = agencyService.getToursByAgency(agency);

        assertEquals(2, risultato.size());
        assertTrue(risultato.contains(tour1));
        assertTrue(risultato.contains(tour2));
    }

// TEST per findById 
    @Test
    void findById_agenziaTrovata() {
        Long id = 1L;
        Agency agency = new Agency("nome", "desc", "zona", "000", new User());
        agency.setId(id);

        when(agencyRepository.findById(id)).thenReturn(Optional.of(agency));

        Optional<Agency> risultato = agencyService.findById(id);

        assertTrue(risultato.isPresent());
        assertEquals(agency, risultato.get());
    }

    @Test
    void findById_agenziaNonTrovata_restituisceOptionalVuoto() {
        Long id = 99L;

        when(agencyRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Agency> risultato = agencyService.findById(id);

        assertTrue(risultato.isEmpty());
    }
}