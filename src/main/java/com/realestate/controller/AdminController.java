package com.realestate.controller;

import com.realestate.dto.PropertyDTO;
import com.realestate.dto.UserDTO;
import com.realestate.entity.Property;
import com.realestate.entity.User;
import com.realestate.dto.AdminStatsDTO;
import com.realestate.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "API pour les administrateurs")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Récupérer tous les utilisateurs (avec pagination)")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Récupérer un utilisateur par son ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.updateUser(id, userDTO));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/properties")
    @Operation(summary = "Récupérer toutes les propriétés (avec pagination et filtres)")
    public ResponseEntity<Page<PropertyDTO>> getAllProperties(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId,
            Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllProperties(status, ownerId, pageable));
    }

    @GetMapping("/properties/{id}")
    @Operation(summary = "Récupérer une propriété par son ID")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getPropertyById(id));
    }

    @PutMapping("/properties/{id}")
    @Operation(summary = "Mettre à jour une propriété")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id, 
            @RequestBody PropertyDTO propertyDTO) {
        return ResponseEntity.ok(adminService.updateProperty(id, propertyDTO));
    }

    @DeleteMapping("/properties/{id}")
    @Operation(summary = "Supprimer une propriété")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        adminService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Récupérer les statistiques de la plateforme")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }
}
