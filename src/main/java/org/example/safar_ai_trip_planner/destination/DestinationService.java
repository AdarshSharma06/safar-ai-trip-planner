package org.example.safar_ai_trip_planner.destination;


import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.destination.dto.DestinationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;

    private DestinationResponse mapToResponse(Destination destination){
        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .state(destination.getState())
                .country(destination.getCountry())
                .description(destination.getDescription())
                .imageUrl(destination.getImageUrl())
                .latitude(destination.getLatitude())
                .longitude(destination.getLongitude())
                .build();
    }

    public DestinationResponse createDestination(Destination destination){
        Destination saved =  destinationRepository.save(destination);
                return mapToResponse(saved);
    }

    public List<DestinationResponse> getAllDestinations(){
        return destinationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }
    public DestinationResponse getDestinationById(Long id){
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destination not found"));
        return mapToResponse(destination);
    }
}
