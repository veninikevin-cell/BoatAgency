package com.example.lago.dto;

import java.time.LocalDate;

public class BookingDTO {

    private Long tourId;
    private LocalDate bookingDate;
    private int numPeople;
    private String notes;
    
    public Long getTourId() {
        return tourId;
    }
    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    public int getNumPeople() {
        return numPeople;
    }
    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    
}