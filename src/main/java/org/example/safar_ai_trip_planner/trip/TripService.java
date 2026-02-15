package org.example.safar_ai_trip_planner.trip;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.exception.AccessDeniedException;
import org.example.safar_ai_trip_planner.destination.Destination;
import org.example.safar_ai_trip_planner.destination.DestinationRepository;
import org.example.safar_ai_trip_planner.trip.dto.CreateTripRequest;
import org.example.safar_ai_trip_planner.trip.dto.TripResponse;
import org.example.safar_ai_trip_planner.trip.dto.UpdateTripRequest;
import org.example.safar_ai_trip_planner.user.User;
import org.example.safar_ai_trip_planner.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private TripResponse mapToResponse(Trip trip){
        return TripResponse.builder()
                .id(trip.getId())
                .destinationId(trip.getDestination().getId())
                .destinationName(trip.getDestination().getName())
                .startDate(trip.getStartDate())
                .duration(trip.getDuration())
                .budget(trip.getBudget())
                .tripStyle(trip.getTripStyle())
                .transportPreference(trip.getTransportPreference())
                .foodPreference(trip.getFoodPreference())
                .interests(trip.getInterests())
                .sourceCity(trip.getSourceCity())
                .sourceLatitude(trip.getSourceLatitude())
                .sourceLongitude(trip.getSourceLongitude())
                .build();
    }

    public TripResponse createTrip(CreateTripRequest request, String userEmail){

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setDestination(destination);
        trip.setStartDate(request.getStartDate());
        trip.setDuration(request.getDuration());
        trip.setBudget(request.getBudget());
        trip.setTripStyle(request.getTripStyle());
        trip.setTransportPreference(request.getTransportPreference());
        trip.setFoodPreference(request.getFoodPreference());
        trip.setInterests(request.getInterests());
        trip.setSourceCity(request.getSourceCity());
        trip.setSourceLatitude(request.getSourceLatitude());
        trip.setSourceLongitude(request.getSourceLongitude());

        Trip saved =  tripRepository.save(trip);

        return mapToResponse(saved );
    }

    public List<TripResponse> getTripsForUser(String userEmail){
        return tripRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }
    public TripResponse getTripById(Long tripId, String userEmail){

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        validateTripOwnership(trip,userEmail);

        return mapToResponse(trip);
    }
    public void deleteTrip(Long tripId, String userEmail){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        validateTripOwnership(trip,userEmail);
        tripRepository.delete(trip);
    }

    public TripResponse updateTrip(Long id, String userEmail, UpdateTripRequest request){

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

       validateTripOwnership(trip,userEmail);

        if (request.getStartDate() != null)
            trip.setStartDate(request.getStartDate());

        if (request.getDuration() != null)
            trip.setDuration(request.getDuration());

        if (request.getBudget() != null)
            trip.setBudget(request.getBudget());

        if (request.getTripStyle() != null)
            trip.setTripStyle(request.getTripStyle());

        if (request.getTransportPreference() != null)
            trip.setTransportPreference(request.getTransportPreference());

        if (request.getFoodPreference() != null)
            trip.setFoodPreference(request.getFoodPreference());

        if (request.getInterests() != null)
            trip.setInterests(request.getInterests());

        Trip updated = tripRepository.save(trip);

        return mapToResponse(updated);
    }

    public void validateTripOwnership(Trip trip , String userEmail){
        if(!trip.getUser().getEmail().equals(userEmail)){
            throw new AccessDeniedException("You are not allowed to access this trip");
        }
    }
}
















