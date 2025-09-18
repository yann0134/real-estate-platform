package com.realestate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "listing_media")
public class ListingMedia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Media URL is required")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type = MediaType.IMAGE;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    @JsonIgnore
    private Listing listing;

    public enum MediaType {
        IMAGE, VIDEO
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public ListingMedia() {}

    public ListingMedia(String url, MediaType type, Listing listing) {
        this.url = url;
        this.type = type;
        this.listing = listing;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
}