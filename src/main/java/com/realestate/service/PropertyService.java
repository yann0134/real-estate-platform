package com.realestate.service;

import com.realestate.dto.PropertyDTO;
import com.realestate.entity.Property;
import com.realestate.entity.PropertyType;
import com.realestate.entity.User;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.repository.PropertyRepository;
import com.realestate.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    
    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<PropertyDTO> searchProperties(
            String query, String city, BigDecimal minPrice, BigDecimal maxPrice,
            Double minSurface, Integer rooms, PropertyType type, Pageable pageable) {
        
        logger.info("Searching properties with query: {}, city: {}, minPrice: {}, maxPrice: {}, minSurface: {}, rooms: {}, type: {}",
                query, city, minPrice, maxPrice, minSurface, rooms, type);
        
        return propertyRepository.searchProperties(
                query, city, minPrice, maxPrice, minSurface, rooms, type, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public PropertyDTO getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO, String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        User owner = (User) userService.loadUserByUsername(email);
        
        Property property = convertToEntity(propertyDTO);
        property.setOwner(owner);
        
        Property savedProperty = propertyRepository.save(property);
        logger.info("Created new property with id: {}", savedProperty.getId());
        
        return convertToDto(savedProperty);
    }

    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO, String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        
        // Vérifier que l'utilisateur est le propriétaire
        if (!existingProperty.getOwner().getEmail().equals(email)) {
            throw new SecurityException("You are not authorized to update this property");
        }
        
        // Mettre à jour les champs modifiables
        modelMapper.map(propertyDTO, existingProperty);
        Property updatedProperty = propertyRepository.save(existingProperty);
        
        logger.info("Updated property with id: {}", id);
        return convertToDto(updatedProperty);
    }

    @Transactional
    public void deleteProperty(Long id, String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        
        // Vérifier que l'utilisateur est le propriétaire
        if (!property.getOwner().getEmail().equals(email)) {
            throw new SecurityException("You are not authorized to delete this property");
        }
        
        propertyRepository.delete(property);
        logger.info("Deleted property with id: {}", id);
    }

    @Transactional
    public PropertyDTO addImagesToProperty(Long id, List<MultipartFile> files, String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        
        // Vérifier que l'utilisateur est le propriétaire
        if (!property.getOwner().getEmail().equals(email)) {
            throw new SecurityException("You are not authorized to add images to this property");
        }
        
        // Enregistrer les fichiers et ajouter les URLs à la propriété
        List<String> newImageUrls = files.stream()
                .map(fileStorageService::storeFile)
                .collect(Collectors.toList());
                
        property.getImageUrls().addAll(newImageUrls);
        Property updatedProperty = propertyRepository.save(property);
        
        logger.info("Added {} images to property with id: {}", newImageUrls.size(), id);
        return convertToDto(updatedProperty);
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> getUserProperties(String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        User user = (User) userService.loadUserByUsername(email);
        
        return propertyRepository.findByOwnerId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Méthodes de conversion entre entité et DTO
    private PropertyDTO convertToDto(Property property) {
        PropertyDTO dto = modelMapper.map(property, PropertyDTO.class);
        if (property.getOwner() != null) {
            dto.setOwnerId(property.getOwner().getId());
            dto.setOwnerName(property.getOwner().getFirstName() + " " + property.getOwner().getLastName());
        }
        return dto;
    }

    private Property convertToEntity(PropertyDTO dto) {
        return modelMapper.map(dto, Property.class);
    }
}
