package com.example.lago.dto;

import java.math.BigDecimal;

public class TourDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private int durationMinutes;
    private String zoneDeparture;
    private String timeSlot;
    private String availableDays;
    
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
    }}