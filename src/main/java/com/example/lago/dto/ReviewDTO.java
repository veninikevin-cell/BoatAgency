package com.example.lago.dto;

public class ReviewDTO {

    private Long agencyId;
    private int rating;
    private String comment;
    
    public Long getAgencyId() {
        return agencyId;
    }
    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    
}
