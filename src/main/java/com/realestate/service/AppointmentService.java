package com.realestate.service;

import com.realestate.dto.AppointmentDTO;
import com.realestate.entity.Appointment;
import com.realestate.entity.AppointmentStatus;
import com.realestate.entity.Property;
import com.realestate.entity.User;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.exception.UnauthorizedException;
import com.realestate.repository.AppointmentRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.event.AppointmentCreatedEvent;
import com.realestate.event.AppointmentUpdatedEvent;
import com.realestate.repository.UserRepository;
import com.realestate.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private static final int APPOINTMENT_DURATION_MINUTES = 60;

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO, String token) {
        // Récupérer l'utilisateur à partir du token
        String userEmail = tokenProvider.getUsernameFromToken(token.substring(7));
        User visitor = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier que le bien existe
        Property property = propertyRepository.findById(appointmentDTO.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Bien immobilier non trouvé"));

        // Vérifier que le créneau est disponible
        if (!isTimeSlotAvailable(property.getId(), appointmentDTO.getStartTime())) {
            throw new IllegalStateException("Ce créneau n'est plus disponible");
        }

        // Créer le rendez-vous
        Appointment appointment = new Appointment();
        appointment.setProperty(property);
        appointment.setVisitor(visitor);
        appointment.setStartTime(appointmentDTO.getStartTime());
        appointment.setEndTime(appointmentDTO.getStartTime().plusMinutes(APPOINTMENT_DURATION_MINUTES));
        appointment.setMessage(appointmentDTO.getMessage());
        appointment.setStatus(AppointmentStatus.PENDING);

        // Sauvegarder le rendez-vous
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Publier un événement de création de rendez-vous
        eventPublisher.publishEvent(new AppointmentCreatedEvent(this, savedAppointment));
        
        return convertToDTO(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getUserAppointments(String token) {
        String userEmail = tokenProvider.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return appointmentRepository.findByVisitorIdOrderByStartTimeDesc(user.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPropertyAppointments(Long propertyId) {
        return appointmentRepository.findByPropertyIdOrderByStartTimeAsc(propertyId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTO updateAppointmentStatus(Long id, String status, String token) {
        String userEmail = tokenProvider.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé"));

        // Vérifier que l'utilisateur est le propriétaire du bien ou le visiteur
        if (!appointment.getProperty().getOwner().getId().equals(user.getId()) && 
            !appointment.getVisitor().getId().equals(user.getId())) {
            throw new UnauthorizedException("Non autorisé à modifier ce rendez-vous");
        }

        // Sauvegarder l'ancien statut
        AppointmentStatus oldStatus = appointment.getStatus();
        
        // Mettre à jour le statut
        appointment.setStatus(AppointmentStatus.valueOf(status.toUpperCase()));
        
        // Sauvegarder les modifications
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Si le rendez-vous est confirmé, annuler les autres rendez-vous en conflit
        if (updatedAppointment.getStatus() == AppointmentStatus.CONFIRMED) {
            cancelConflictingAppointments(updatedAppointment);
        }
        
        // Si le statut a changé, publier un événement de mise à jour
        if (oldStatus != updatedAppointment.getStatus()) {
            eventPublisher.publishEvent(new AppointmentUpdatedEvent(this, updatedAppointment));
        }

        return convertToDTO(updatedAppointment);
    }

    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableSlots(Long propertyId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implémentation pour obtenir les créneaux disponibles
        // Cette méthode devrait vérifier les créneaux déjà pris et les exclure
        // Retourne une liste de LocalDateTime disponibles
        return List.of(); // Implémentation simplifiée
    }

    private boolean isTimeSlotAvailable(Long propertyId, LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);
        return appointmentRepository.countConflictingAppointments(propertyId, startTime, endTime) == 0;
    }

    private void cancelConflictingAppointments(Appointment confirmedAppointment) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            confirmedAppointment.getProperty().getId(),
            confirmedAppointment.getStartTime(),
            confirmedAppointment.getEndTime()
        );

        conflicts.forEach(conflict -> {
            if (!conflict.getId().equals(confirmedAppointment.getId())) {
                conflict.setStatus(AppointmentStatus.CANCELLED);
                // notificationService.notifyAppointmentCancellation(conflict);
            }
        });

        appointmentRepository.saveAll(conflicts);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = modelMapper.map(appointment, AppointmentDTO.class);
        dto.setPropertyId(appointment.getProperty().getId());
        dto.setPropertyTitle(appointment.getProperty().getTitle());
        dto.setVisitorId(appointment.getVisitor().getId());
        dto.setVisitorName(appointment.getVisitor().getFirstName() + " " + appointment.getVisitor().getLastName());
        dto.setVisitorEmail(appointment.getVisitor().getEmail());
        return dto;
    }
}
