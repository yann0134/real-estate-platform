package com.realestate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private PropertyType type;
    
    private BigDecimal price;
    private Double surface;
    private Integer rooms;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floor;
    private Integer totalFloors;
    private Integer constructionYear;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private Boolean isFurnished = false;
    private Boolean hasElevator = false;
    private Boolean hasParking = false;
    
    @Enumerated(EnumType.STRING)
    private EnergyEfficiency energyEfficiency;
    
    private Integer co2Emission;
    
    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
    
    @Enumerated(EnumType.STRING)
    private PropertyStatus status = PropertyStatus.AVAILABLE;
    
    // Getters and setters are handled by @Data
}
