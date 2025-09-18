package com.realestate.repository;

import com.realestate.entity.Favorite;
import com.realestate.entity.Listing;
import com.realestate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Page<Favorite> findByUser(User user, Pageable pageable);
    
    Optional<Favorite> findByUserAndListing(User user, Listing listing);
    
    boolean existsByUserAndListing(User user, Listing listing);
    
    void deleteByUserAndListing(User user, Listing listing);
}