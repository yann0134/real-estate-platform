package com.realestate.controller;

import com.realestate.dto.PropertyDTO;
import com.realestate.entity.PropertyType;
import com.realestate.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "API pour la gestion des biens immobiliers")
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    @Operation(
        summary = "Rechercher des biens immobiliers",
        description = "Retourne une page de biens immobiliers correspondant aux critères de recherche"
    )
    public ResponseEntity<Page<PropertyDTO>> searchProperties(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minSurface,
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) PropertyType type,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(
            propertyService.searchProperties(query, city, minPrice, maxPrice, minSurface, rooms, type, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un bien immobilier")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Créer un nouveau bien immobilier",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PropertyDTO> createProperty(
            @RequestBody PropertyDTO propertyDTO,
            @RequestHeader("Authorization") String token) {
        
        return new ResponseEntity<>(
            propertyService.createProperty(propertyDTO, token),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Mettre à jour un bien immobilier",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @RequestBody PropertyDTO propertyDTO,
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDTO, token));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Supprimer un bien immobilier",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProperty(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        propertyService.deleteProperty(id, token);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Ajouter des images à un bien immobilier",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PropertyDTO> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(propertyService.addImagesToProperty(id, files, token));
    }

    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obtenir la liste des biens de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PropertyDTO>> getUserProperties(
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(propertyService.getUserProperties(token));
    }
}
