package com.realestate.service;

import com.realestate.dto.AdminStatsDTO;
import com.realestate.dto.PropertyDTO;
import com.realestate.dto.UserDTO;
import com.realestate.entity.Property;
import com.realestate.entity.PropertyStatus;
import com.realestate.entity.User;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.repository.AppointmentRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // Gestion des utilisateurs
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        // Mise à jour des champs autorisés
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhone(userDTO.getPhone());
        
        if (userDTO.getRole() != null) {
            existingUser.setRole(User.Role.valueOf(userDTO.getRole()));
        }
        
        // Mise à jour du mot de passe si fourni
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        
        // Vérifier si l'utilisateur a des propriétés ou des rendez-vous
        if (!user.getListings().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un utilisateur avec des propriétés associées");
        }
        
        // Annuler les rendez-vous futurs
        appointmentRepository.cancelFutureAppointmentsByUser(id, LocalDate.now());
        
        userRepository.delete(user);
    }

    // Gestion des propriétés
    public Page<PropertyDTO> getAllProperties(String status, Long ownerId, Pageable pageable) {
        if (status != null && ownerId != null) {
            return propertyRepository.findByStatusAndOwnerId(
                    PropertyStatus.valueOf(status.toUpperCase()),
                    ownerId,
                    pageable
            ).map(property -> modelMapper.map(property, PropertyDTO.class));
        } else if (status != null) {
            return propertyRepository.findByStatus(
                    PropertyStatus.valueOf(status.toUpperCase()),
                    pageable
            ).map(property -> modelMapper.map(property, PropertyDTO.class));
        } else if (ownerId != null) {
            return propertyRepository.findByOwnerId(
                    ownerId,
                    pageable
            ).map(property -> modelMapper.map(property, PropertyDTO.class));
        } else {
            return propertyRepository.findAll(pageable)
                    .map(property -> modelMapper.map(property, PropertyDTO.class));
        }
    }

    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété non trouvée avec l'ID : " + id));
        return modelMapper.map(property, PropertyDTO.class);
    }

    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété non trouvée avec l'ID : " + id));

        // Mise à jour des champs autorisés
        existingProperty.setTitle(propertyDTO.getTitle());
        existingProperty.setDescription(propertyDTO.getDescription());
        existingProperty.setPrice(propertyDTO.getPrice());
        existingProperty.setSurface(propertyDTO.getSurface());
        existingProperty.setRooms(propertyDTO.getRooms());
        existingProperty.setBedrooms(propertyDTO.getBedrooms());
        existingProperty.setBathrooms(propertyDTO.getBathrooms());
        existingProperty.setFloor(propertyDTO.getFloor());
        existingProperty.setTotalFloors(propertyDTO.getTotalFloors());
        existingProperty.setConstructionYear(propertyDTO.getConstructionYear());
        existingProperty.setAddress(propertyDTO.getAddress());
        existingProperty.setCity(propertyDTO.getCity());
        existingProperty.setPostalCode(propertyDTO.getPostalCode());
        existingProperty.setCountry(propertyDTO.getCountry());
        existingProperty.setLatitude(propertyDTO.getLatitude());
        existingProperty.setLongitude(propertyDTO.getLongitude());
        existingProperty.setIsFurnished(propertyDTO.getIsFurnished());
        existingProperty.setHasElevator(propertyDTO.getHasElevator());
        existingProperty.setHasParking(propertyDTO.getHasParking());
        
        if (propertyDTO.getStatus() != null) {
            existingProperty.setStatus(propertyDTO.getStatus());
        }
        
        if (propertyDTO.getEnergyEfficiency() != null) {
            existingProperty.setEnergyEfficiency(propertyDTO.getEnergyEfficiency());
        }
        
        if (propertyDTO.getCo2Emission() != null) {
            existingProperty.setCo2Emission(propertyDTO.getCo2Emission());
        }

        Property updatedProperty = propertyRepository.save(existingProperty);
        return modelMapper.map(updatedProperty, PropertyDTO.class);
    }

    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété non trouvée avec l'ID : " + id));
        
        // Annuler les rendez-vous futurs pour cette propriété
        appointmentRepository.cancelFutureAppointmentsByProperty(id);
        
        propertyRepository.delete(property);
    }

    // Statistiques
    public AdminStatsDTO getPlatformStats() {
        AdminStatsDTO stats = new AdminStatsDTO();
        
        // Comptage des utilisateurs
        long totalUsers = userRepository.count();
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(
                LocalDate.now().withDayOfMonth(1).atStartOfDay());
        
        // Comptage des propriétés
        long totalProperties = propertyRepository.count();
        long availableProperties = propertyRepository.countByStatus(PropertyStatus.AVAILABLE);
        
        // Comptage des rendez-vous
        long totalAppointments = appointmentRepository.count();
        long upcomingAppointments = appointmentRepository.countUpcomingAppointments(LocalDate.now());
        
        stats.setTotalUsers(totalUsers);
        stats.setNewUsersThisMonth(newUsersThisMonth);
        stats.setTotalProperties(totalProperties);
        stats.setAvailableProperties(availableProperties);
        stats.setTotalAppointments(totalAppointments);
        stats.setUpcomingAppointments(upcomingAppointments);
        
        return stats;
    }
}
