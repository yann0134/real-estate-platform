package com.realestate.controller;

import com.realestate.dto.UserDTO;
import com.realestate.dto.UserProfileDTO;
import com.realestate.entity.User;
import com.realestate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion des utilisateurs et des profils")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Récupérer le profil de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserProfileDTO> getCurrentUser(
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(userService.getCurrentUserProfile(token));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Mettre à jour le profil de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserProfileDTO> updateCurrentUser(
            @Valid @RequestBody UserProfileDTO userProfileDTO,
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(userService.updateCurrentUserProfile(userProfileDTO, token));
    }

    @GetMapping("/me/properties")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Récupérer les biens de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PropertyDTO>> getUserProperties(
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(userService.getUserProperties(token));
    }

    @GetMapping("/me/favorites")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Récupérer les biens favoris de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PropertyDTO>> getUserFavorites(
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(userService.getUserFavorites(token));
    }

    @PostMapping("/me/favorites/{propertyId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Ajouter un bien aux favoris",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long propertyId,
            @RequestHeader("Authorization") String token) {
        
        userService.addFavorite(propertyId, token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/favorites/{propertyId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Retirer un bien des favoris",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long propertyId,
            @RequestHeader("Authorization") String token) {
        
        userService.removeFavorite(propertyId, token);
        return ResponseEntity.noContent().build();
    }
}
