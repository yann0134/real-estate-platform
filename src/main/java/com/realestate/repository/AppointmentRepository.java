package com.realestate.repository;

import com.realestate.entity.Appointment;
import com.realestate.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Méthodes de base
    List<Appointment> findByVisitorIdOrderByStartTimeDesc(Long visitorId);
    List<Appointment> findByPropertyIdOrderByStartTimeAsc(Long propertyId);
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    
    // Récupérer les rendez-vous d'un visiteur avec pagination
    Page<Appointment> findByVisitorId(Long visitorId, Pageable pageable);
    
    // Récupérer les rendez-vous d'une propriété avec pagination et filtrage par statut
    Page<Appointment> findByPropertyIdAndStatus(Long propertyId, AppointmentStatus status, Pageable pageable);
    
    // Trouver les rendez-vous dans une période donnée
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.startTime >= :startDate AND a.endTime <= :endDate " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Détection des conflits de rendez-vous
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.property.id = :propertyId " +
           "AND a.status = 'CONFIRMED' " +
           "AND ((a.startTime < :endTime) AND (a.endTime > :startTime))") 
    List<Appointment> findConflictingAppointments(
            @Param("propertyId") Long propertyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.property.id = :propertyId " +
           "AND a.status = 'CONFIRMED' " +
           "AND ((a.startTime < :endTime) AND (a.endTime > :startTime))") 
    boolean existsConflictingAppointment(
            @Param("propertyId") Long propertyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Méthodes pour les statistiques
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(@Param("status") AppointmentStatus status);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.startTime >= :date")
    long countUpcomingAppointments(@Param("date") LocalDate date);
    
    // Statistiques par période
    @Query("SELECT FUNCTION('date', a.startTime) as date, COUNT(a) as count " +
           "FROM Appointment a " +
           "WHERE a.startTime BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('date', a.startTime) " +
           "ORDER BY date")
    List<Object[]> countAppointmentsByDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Taux de conversion des rendez-vous
    @Query("SELECT " +
           "COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) * 100.0 / NULLIF(COUNT(a), 0) " +
           "FROM Appointment a " +
           "WHERE a.startTime BETWEEN :startDate AND :endDate")
    Double getConversionRate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Durée moyenne des rendez-vous
    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', MINUTE, a.startTime, a.endTime)) " +
           "FROM Appointment a " +
           "WHERE a.status = 'COMPLETED' AND a.startTime >= :startDate")
    Double getAverageAppointmentDuration(@Param("startDate") LocalDate startDate);
    
    // Méthodes pour la gestion administrative
    @Modifying
    @Query("UPDATE Appointment a SET a.status = 'CANCELLED' " +
           "WHERE a.property.id = :propertyId AND a.startTime > CURRENT_TIMESTAMP")
    void cancelFutureAppointmentsByProperty(@Param("propertyId") Long propertyId);
    
    @Modifying
    @Query("UPDATE Appointment a SET a.status = 'CANCELLED' " +
           "WHERE a.visitor.id = :userId AND a.startTime > :date")
    void cancelFutureAppointmentsByUser(
            @Param("userId") Long userId,
            @Param("date") LocalDate date);
    
    // Rendez-vous à venir par propriétaire
    @Query("SELECT a FROM Appointment a " +
           "JOIN a.property p " +
           "WHERE p.owner.id = :ownerId " +
           "AND a.startTime >= CURRENT_TIMESTAMP " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointmentsByOwner(@Param("ownerId") Long ownerId);

    // Créneaux disponibles pour une propriété
    @Query(value = "WITH RECURSIVE time_slots AS (" +
            "SELECT :startDate + INTERVAL '1 HOUR' * n AS slot_start, " +
            "       :startDate + INTERVAL '1 HOUR' * (n + 1) AS slot_end " +
            "FROM generate_series(0, 23) AS n " +
            "WHERE :startDate + INTERVAL '1 HOUR' * n < :endDate" +
            ") " +
            "SELECT ts.slot_start, ts.slot_end " +
            "FROM time_slots ts " +
            "WHERE NOT EXISTS (" +
            "    SELECT 1 FROM appointments a " +
            "    WHERE a.property_id = :propertyId " +
            "    AND a.status IN ('PENDING', 'CONFIRMED') " +
            "    AND ts.slot_start < a.end_time " +
            "    AND ts.slot_end > a.start_time" +
            ") " +
            "AND ts.slot_start >= CURRENT_TIMESTAMP " +
            "ORDER BY ts.slot_start", nativeQuery = true)
    List<Object[]> findAvailableTimeSlots(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Rendez-vous passés avec évaluation
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = 'COMPLETED' " +
            "AND a.rating IS NOT NULL " +
            "ORDER BY a.endTime DESC")
    Page<Appointment> findRatedAppointments(Pageable pageable);

    // Taux d'occupation des créneaux
    @Query("SELECT " +
            "FUNCTION('HOUR', a.startTime) as hour, " +
            "COUNT(a) as count, " +
            "COUNT(a) * 100.0 / (SELECT COUNT(a2) FROM Appointment a2 " +
            "                     WHERE FUNCTION('HOUR', a2.startTime) = FUNCTION('HOUR', a.startTime) " +
            "                     AND a2.startTime >= :startDate) as percentage " +
            "FROM Appointment a " +
            "WHERE a.startTime >= :startDate " +
            "GROUP BY FUNCTION('HOUR', a.startTime) " +
            "ORDER BY count DESC")
    List<Object[]> getTimeSlotOccupancy(@Param("startDate") LocalDate startDate);

    // Nombre de rendez-vous par statut
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatusGroup();
            
