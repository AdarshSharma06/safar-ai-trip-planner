package org.example.safar_ai_trip_planner.place;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.enums.PlaceType;
import org.example.safar_ai_trip_planner.destination.Destination;
import org.example.safar_ai_trip_planner.destination.DestinationRepository;
import org.example.safar_ai_trip_planner.place.dto.PlaceResponse;
import org.springframework.stereotype.Service;
import java.util.Optional ;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final DestinationRepository destinationRepository;

    private PlaceResponse mapToResponse(Place place){
        return PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .description(place.getDescription())
                .type(place.getType().name())
                .rating(place.getRating())
                .imageUrl(place.getImageUrl())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .destinationId(place.getDestination().getId())
                .destinationName(place.getDestination().getName())
                .build();
    }
    public PlaceResponse createPlace(Long destinationId, Place place){

        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        place.setDestination(destination);

        Place saved = placeRepository.save(place);

        return mapToResponse(saved);
    }

    public List<PlaceResponse> getPlacesByDestination(Long destinationId){
        return placeRepository.findByDestinationId(destinationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<PlaceResponse> getPlacesByDestinationAndType(Long destinationId, PlaceType type){
        return placeRepository.findByDestinationIdAndType(destinationId, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
