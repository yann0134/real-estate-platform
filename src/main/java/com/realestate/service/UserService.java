package com.realestate.service;

import com.realestate.dto.PropertyDTO;
import com.realestate.dto.UserProfileDTO;
import com.realestate.entity.Property;
import com.realestate.entity.Favorite;
import com.realestate.entity.Listing;
import com.realestate.entity.User;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.exception.UnauthorizedException;
import com.realestate.repository.ListingRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import com.realestate.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements AuthUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ListingRepository listingRepository;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    
    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Définir le rôle admin pour le premier utilisateur
        if (userRepository.count() == 0) {
            user.setRole(User.Role.ADMIN);
            logger.info("Creating first user as ADMIN: {}", user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    public UserProfileDTO getCurrentUserProfile(String token) {
        User user = getCurrentUser(token);
        return modelMapper.map(user, UserProfileDTO.class);
    }

    public UserProfileDTO updateCurrentUserProfile(UserProfileDTO userProfileDTO, String token) {
        User user = getCurrentUser(token);
        
        // Mettre à jour les champs autorisés
        user.setFirstName(userProfileDTO.getFirstName());
        user.setLastName(userProfileDTO.getLastName());
        user.setPhone(userProfileDTO.getPhone());
        
        if (userProfileDTO.getPassword() != null && !userProfileDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userProfileDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserProfileDTO.class);
    }

    public List<PropertyDTO> getUserProperties(String token) {
        User user = getCurrentUser(token);
        return propertyRepository.findByOwnerId(user.getId())
                .stream()
                .map(property -> modelMapper.map(property, PropertyDTO.class))
                .collect(Collectors.toList());
    }

    public List<PropertyDTO> getUserFavorites(String token) {
        User user = getCurrentUser(token);
        return user.getFavorites()
                .stream()
                .map(favorite -> {
                    Listing listing = favorite.getListing();
                    PropertyDTO dto = new PropertyDTO();
                    dto.setId(listing.getId());
                    dto.setTitle(listing.getTitle());
                    dto.setDescription(listing.getDescription());
                    dto.setPrice(listing.getPrice());
                    dto.setSurface(listing.getSurfaceArea() != null ? listing.getSurfaceArea().doubleValue() : null);
                    dto.setRooms(listing.getRooms());
                    dto.setBedrooms(listing.getBedrooms());
                    dto.setBathrooms(listing.getBathrooms());
                    dto.setAddress(listing.getAddress());
                    dto.setCity(listing.getCity() != null ? listing.getCity().getName() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void addFavorite(Long listingId, String token) {
        User user = getCurrentUser(token);
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));
        
        // Vérifier si le favori existe déjà
        boolean alreadyFavorited = user.getFavorites().stream()
                .anyMatch(favorite -> favorite.getListing().getId().equals(listingId));
                
        if (!alreadyFavorited) {
            Favorite favorite = new Favorite(user, listing);
            user.getFavorites().add(favorite);
            userRepository.save(user);
        }
    }

    public void removeFavorite(Long listingId, String token) {
        User user = getCurrentUser(token);
        user.getFavorites().removeIf(favorite -> favorite.getListing().getId().equals(listingId));
        userRepository.save(user);
    }
    
    private User getCurrentUser(String token) {
        String email = jwtUtil.getEmailFromToken(token.substring(7));
        return (User) loadUserByUsername(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        logger.info("User deleted: {}", id);
    }
}