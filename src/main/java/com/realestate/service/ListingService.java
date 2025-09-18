package com.realestate.service;

import com.realestate.entity.Listing;
import com.realestate.entity.User;
import com.realestate.repository.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class ListingService {

    private static final Logger logger = LoggerFactory.getLogger(ListingService.class);

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Listing createListing(Listing listing, User user) {
        listing.setUser(user);
        listing.setStatus(Listing.Status.ACTIVE);
        Listing savedListing = listingRepository.save(listing);
        logger.info("Listing created successfully: {} by user {}", savedListing.getId(), user.getId());
        return savedListing;
    }

    public Page<Listing> getAllActiveListings(Pageable pageable) {
        return listingRepository.findByStatus(Listing.Status.ACTIVE, pageable);
    }

    public Page<Listing> getUserListings(User user, Pageable pageable) {
        return listingRepository.findByUser(user, pageable);
    }

    public Optional<Listing> findById(Long id) {
        return listingRepository.findById(id);
    }

    public Page<Listing> searchWithFilters(
            String cityName,
            Listing.PropertyType propertyType,
            Listing.TransactionType transactionType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minSurface,
            Integer maxSurface,
            Integer minRooms,
            Integer maxRooms,
            Pageable pageable) {
        
        return listingRepository.findWithFilters(
                Listing.Status.ACTIVE,
                cityName,
                propertyType,
                transactionType,
                minPrice,
                maxPrice,
                minSurface,
                maxSurface,
                minRooms,
                maxRooms,
                pageable);
    }

    public Listing updateListing(Listing listing, User user) {
        if (!listing.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to update this listing");
        }
        
        Listing updatedListing = listingRepository.save(listing);
        logger.info("Listing updated successfully: {} by user {}", updatedListing.getId(), user.getId());
        return updatedListing;
    }

    public void deleteListing(Long id, User user) {
        Optional<Listing> listing = listingRepository.findById(id);
        if (listing.isEmpty()) {
            throw new RuntimeException("Listing not found");
        }
        
        if (!listing.get().getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to delete this listing");
        }
        
        listingRepository.deleteById(id);
        logger.info("Listing deleted: {} by user {}", id, user.getId());
    }

    public Listing changeStatus(Long id, Listing.Status status, User user) {
        Optional<Listing> listingOpt = listingRepository.findById(id);
        if (listingOpt.isEmpty()) {
            throw new RuntimeException("Listing not found");
        }
        
        Listing listing = listingOpt.get();
        if (!listing.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to change listing status");
        }
        
        listing.setStatus(status);
        Listing updatedListing = listingRepository.save(listing);
        logger.info("Listing status changed to {} for listing {} by user {}", status, id, user.getId());
        return updatedListing;
    }
}