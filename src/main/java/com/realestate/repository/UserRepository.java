package com.realestate.repository;

import com.realestate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    long countAdmins();
    
    // Récupérer les utilisateurs avec pagination et filtrage optionnel
    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.firstName) LIKE %:search% OR LOWER(u.lastName) LIKE %:search% OR LOWER(u.email) LIKE %:search%) " +
           "AND (:role IS NULL OR u.role = :role)")
    Page<User> findAllWithFilters(
            @Param("search") String search,
            @Param("role") User.Role role,
            Pageable pageable
    );
    
    // Compter les nouveaux utilisateurs depuis une date donnée
    long countByCreatedAtAfter(LocalDateTime date);
    
    // Trouver les utilisateurs inactifs depuis une certaine date
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date AND u.enabled = true")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);
    
    // Trouver les utilisateurs inactifs avec des rendez-vous
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.appointments a WHERE u.lastLogin < :date AND a IS NOT NULL")
    List<User> findInactiveUsersWithAppointments(@Param("date") LocalDateTime date);
    
    // Compter les utilisateurs actifs (ayant une activité) dans une période donnée
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.appointments a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    long countActiveUsersInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Trouver les utilisateurs par rôle avec pagination
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    // Vérifier si un utilisateur a des propriétés
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Property p WHERE p.owner.id = :userId")
    boolean hasProperties(@Param("userId") Long userId);
}