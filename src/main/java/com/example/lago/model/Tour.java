package com.example.lago.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

 // Rappresenta un tour offerto da un'agenzia
 
@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private String zoneDeparture;

    private String timeSlot;
    private String availableDays;
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // costruttore vuoto
    public Tour() {}

    // costruttore con parametri
    public Tour(String name, String description,
                BigDecimal price, int durationMinutes, Agency agency) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.agency = agency;
    }

    // metodo di utilità per la visualizzazione della durata
    public String getDurationFormatted() {
        int ore = durationMinutes / 60;
        int minuti = durationMinutes % 60;
        if (ore == 0) return minuti + "min";
        if (minuti == 0) return ore + "h";
        return ore + "h " + minuti + "min";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getZoneDeparture() {
        return zoneDeparture;
    }

    public void setZoneDeparture(String zoneDeparture) {
        this.zoneDeparture = zoneDeparture;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
}