package org.example.safar_ai_trip_planner.itinerary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    Optional<Itinerary> findByTripId(Long tripId);
}
