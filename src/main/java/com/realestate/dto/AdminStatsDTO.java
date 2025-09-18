package com.realestate.dto;

import lombok.Data;

import java.util.Map;

/**
 * DTO pour les statistiques de la plateforme destiné aux administrateurs
 */
@Data
public class AdminStatsDTO {
    // Utilisateurs
    private long totalUsers;
    private long newUsersThisMonth;
    private long activeUsersLast30Days;
    
    // Propriétés
    private long totalProperties;
    private long availableProperties;
    private long propertiesAddedThisMonth;
    
    // Rendez-vous
    private long totalAppointments;
    private long upcomingAppointments;
    private long completedAppointments;
    
    // Revenus (si applicable)
    private double totalRevenue;
    private double revenueThisMonth;
    
    // Répartition par type de propriété
    private Map<String, Long> propertiesByType;
    
    // Répartition des rendez-vous par statut
    private Map<String, Long> appointmentsByStatus;
    
    // Méthodes utilitaires
    public double getOccupancyRate() {
        return totalProperties > 0 ? 
               100.0 * (totalProperties - availableProperties) / totalProperties : 0.0;
    }
    
    public double getConversionRate() {
        return totalAppointments > 0 ? 
               100.0 * completedAppointments / totalAppointments : 0.0;
    }
}
