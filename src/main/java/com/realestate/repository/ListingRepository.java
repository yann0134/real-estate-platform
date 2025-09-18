package com.realestate.repository;

import com.realestate.entity.Listing;
import com.realestate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    
    Page<Listing> findByStatusAndCityNameIgnoreCaseContaining(
            Listing.Status status, String cityName, Pageable pageable);
    
    Page<Listing> findByUser(User user, Pageable pageable);
    
    Page<Listing> findByStatus(Listing.Status status, Pageable pageable);
    
    @Query("SELECT l FROM Listing l WHERE l.status = :status " +
           "AND (:cityName IS NULL OR LOWER(l.city.name) LIKE LOWER(CONCAT('%', :cityName, '%'))) " +
           "AND (:propertyType IS NULL OR l.propertyType = :propertyType) " +
           "AND (:transactionType IS NULL OR l.transactionType = :transactionType) " +
           "AND (:minPrice IS NULL OR l.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR l.price <= :maxPrice) " +
           "AND (:minSurface IS NULL OR l.surfaceArea >= :minSurface) " +
           "AND (:maxSurface IS NULL OR l.surfaceArea <= :maxSurface) " +
           "AND (:minRooms IS NULL OR l.rooms >= :minRooms) " +
           "AND (:maxRooms IS NULL OR l.rooms <= :maxRooms)")
    Page<Listing> findWithFilters(
            @Param("status") Listing.Status status,
            @Param("cityName") String cityName,
            @Param("propertyType") Listing.PropertyType propertyType,
            @Param("transactionType") Listing.TransactionType transactionType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minSurface") Integer minSurface,
            @Param("maxSurface") Integer maxSurface,
            @Param("minRooms") Integer minRooms,
            @Param("maxRooms") Integer maxRooms,
            Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.status = 'ACTIVE' " +
           "AND LOWER(l.city.name) = LOWER(:cityName) " +
           "AND l.price <= :maxPrice " +
           "AND (:propertyType IS NULL OR l.propertyType = :propertyType) " +
           "AND (:minRooms IS NULL OR l.rooms >= :minRooms)")
    List<Listing> findByAIQuery(
            @Param("cityName") String cityName,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("propertyType") Listing.PropertyType propertyType,
            @Param("minRooms") Integer minRooms);
}