package com.example.lago.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

 // Rappresenta un'agenzia di tour associata a un fornitore
 // Un utente può possedere al massimo un'agenzia e viceversa.
 
@Entity
@Table(name = "agencies")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String zone;

    private String phone;
    private String website;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    //Imposta automaticamente la data di creazione al momento del salvataggio. 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // costruttore vuoto
    public Agency() {}

    // costruttore con parametri
    public Agency(String name, String description,
                  String zone, String phone, User owner) {
        this.name = name;
        this.description = description;
        this.zone = zone;
        this.phone = phone;
        this.owner = owner;
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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
}