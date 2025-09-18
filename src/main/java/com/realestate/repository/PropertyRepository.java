package com.realestate.repository;

import com.realestate.entity.Property;
import com.realestate.entity.PropertyStatus;
import com.realestate.entity.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    
    // Méthodes de recherche de base
    Page<Property> findByCityIgnoreCase(String city, Pageable pageable);
    Page<Property> findByType(PropertyType type, Pageable pageable);
    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);
    Page<Property> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Property> findByRoomsGreaterThanEqual(Integer rooms, Pageable pageable);
    Page<Property> findByBedroomsGreaterThanEqual(Integer bedrooms, Pageable pageable);
    Page<Property> findBySurfaceBetween(Double minSurface, Double maxSurface, Pageable pageable);
    
    // Recherche avancée avec plusieurs critères
    @Query("SELECT p FROM Property p WHERE " +
           "(:query IS NULL OR LOWER(p.title) LIKE %:query% OR LOWER(p.description) LIKE %:query%) AND " +
           "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minSurface IS NULL OR p.surface >= :minSurface) AND " +
           "(:maxSurface IS NULL OR p.surface <= :maxSurface) AND " +
           "(:rooms IS NULL OR p.rooms >= :rooms) AND " +
           "(:bedrooms IS NULL OR p.bedrooms >= :bedrooms) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:ownerId IS NULL OR p.owner.id = :ownerId)")
    Page<Property> searchProperties(
            @Param("query") String query,
            @Param("city") String city,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minSurface") Double minSurface,
            @Param("maxSurface") Double maxSurface,
            @Param("rooms") Integer rooms,
            @Param("bedrooms") Integer bedrooms,
            @Param("type") PropertyType type,
            @Param("status") PropertyStatus status,
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    // Méthodes pour les statistiques
    @Query("SELECT COUNT(p) FROM Property p WHERE p.status = :status")
    long countByStatus(@Param("status") PropertyStatus status);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.createdAt >= :date")
    long countAddedSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT p.city, COUNT(p) as count FROM Property p GROUP BY p.city ORDER BY count DESC")
    List<Object[]> countPropertiesByCity();
    
    @Query("SELECT p.type, COUNT(p) as count FROM Property p GROUP BY p.type ORDER BY count DESC")
    List<Object[]> countPropertiesByType();
    
    // Méthodes pour la gestion administrative
    @Modifying
    @Query("UPDATE Property p SET p.status = :status WHERE p.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") PropertyStatus status);
    
    @Modifying
    @Query("UPDATE Property p SET p.featured = :featured WHERE p.id = :id")
    int updateFeaturedStatus(@Param("id") Long id, @Param("featured") boolean featured);
    
    // Trouver les propriétés sans images
    @Query("SELECT p FROM Property p LEFT JOIN p.images i WHERE i.id IS NULL")
    List<Property> findPropertiesWithoutImages();
    
    // Trouver les propriétés expirées (non mises à jour depuis une certaine date)
    @Query("SELECT p FROM Property p WHERE p.updatedAt < :date")
    List<Property> findOutdatedProperties(@Param("date") LocalDateTime date);
    
    // Compter les propriétés par statut
    @Query("SELECT p.status, COUNT(p) FROM Property p GROUP BY p.status")
    List<Object[]> countByStatusGroup();
    
    // Trouver les propriétés par statut et propriétaire
    Page<Property> findByStatusAndOwnerId(PropertyStatus status, Long ownerId, Pageable pageable);
    
    // Méthodes existantes
    List<Property> findByOwnerId(Long ownerId);
    
    long countByOwnerId(Long ownerId);
}
