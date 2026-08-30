package com.example.lago.model;
 
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
// entità prenotazione - stati in Status, ciclo di vita gestito da BookingService
// che notifica gli observer ad ogni transizione (vedi package observer/)
@Entity
@Table(name = "bookings")
public class Booking {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // un utente ha N prenotazioni, una prenotazione ha 1 utente
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    // stesso discorso per il tour
    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
 
    @Column(nullable = false)
    private LocalDate bookingDate;
 
    @Column(nullable = false)
    private int numPeople;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
 
    @Column(columnDefinition = "TEXT")
    private String notes;
 
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
 
    public enum Status {
        PENDING, CONFIRMED, REJECTED, CANCELLED
    }
 
    public Booking() {}
 
    public Booking(User user, Tour tour,
                   LocalDate bookingDate, int numPeople) {
        this.user = user;
        this.tour = tour;
        this.bookingDate = bookingDate;
        this.numPeople = numPeople;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
}