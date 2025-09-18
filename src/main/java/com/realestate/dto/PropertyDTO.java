package com.realestate.dto;

import com.realestate.entity.EnergyEfficiency;
import com.realestate.entity.PropertyStatus;
import com.realestate.entity.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyDTO {
    private Long id;
    private String title;
    private String description;
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
    private Boolean isFurnished;
    private Boolean hasElevator;
    private Boolean hasParking;
    private EnergyEfficiency energyEfficiency;
    private Integer co2Emission;
    private List<String> imageUrls;
    private PropertyStatus status;
    private Long ownerId;
    private String ownerName;
}
