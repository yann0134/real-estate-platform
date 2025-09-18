package com.realestate.controller;

import com.realestate.dto.AppointmentDTO;
import com.realestate.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Gestion des rendez-vous pour les biens immobiliers")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Prendre un rendez-vous pour visiter un bien",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AppointmentDTO> createAppointment(
            @Valid @RequestBody AppointmentDTO appointmentDTO,
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(appointmentService.createAppointment(appointmentDTO, token));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Mettre à jour le statut d'un rendez-vous",
        description = "Permet de confirmer, annuler ou compléter un rendez-vous",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status, token));
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obtenir la liste des rendez-vous de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<AppointmentDTO>> getUserAppointments(
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(appointmentService.getUserAppointments(token));
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Obtenir les rendez-vous pour un bien spécifique")
    public ResponseEntity<List<AppointmentDTO>> getPropertyAppointments(
            @PathVariable Long propertyId) {
        
        return ResponseEntity.ok(appointmentService.getPropertyAppointments(propertyId));
    }

    @GetMapping("/available-slots")
    @Operation(summary = "Obtenir les créneaux disponibles pour un bien")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @RequestParam Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        return ResponseEntity.ok(appointmentService.getAvailableSlots(propertyId, startDate, endDate));
    }
}
