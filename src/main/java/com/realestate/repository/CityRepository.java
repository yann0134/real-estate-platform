package com.realestate.repository;

import com.realestate.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    
    Optional<City> findByNameIgnoreCase(String name);
    
    List<City> findByCountryIgnoreCase(String country);
    
    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<City> findByNameContainingIgnoreCase(String name);
}