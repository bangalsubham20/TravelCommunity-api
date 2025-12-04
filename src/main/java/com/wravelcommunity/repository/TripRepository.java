package com.wravelcommunity.repository;

import com.wravelcommunity.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByActive(Boolean active);
    
    List<Trip> findByDestinationContainingIgnoreCase(String destination);
    
    List<Trip> findByDifficultyAndActive(String difficulty, Boolean active);
    
    @Query("SELECT t FROM Trip t WHERE t.active = true AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(t.destination) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Trip> searchTrips(String searchTerm);
    
    List<Trip> findByPriceBetweenAndActive(Double minPrice, Double maxPrice, Boolean active);
    
    List<Trip> findBySeasonAndActive(String season, Boolean active);
}
