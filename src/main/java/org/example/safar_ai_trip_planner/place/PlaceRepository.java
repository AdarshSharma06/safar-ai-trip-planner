package org.example.safar_ai_trip_planner.place;

import org.example.safar_ai_trip_planner.common.enums.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByDestinationId(Long destinationId);

    List<Place> findByDestinationIdAndType(Long destinationId, PlaceType type);
}
