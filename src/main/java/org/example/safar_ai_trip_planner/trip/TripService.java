package org.example.safar_ai_trip_planner.trip;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.exception.AccessDeniedException;
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


    public TripResponse createTrip(String email, String title, String description
    ,LocalDate startDate, LocalDate endDate){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = new Trip();
        trip.setTitle(title);
        trip.setDescription(description);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setUser(user);

        Trip savedTrip =  tripRepository.save(trip);

        return new TripResponse(
                savedTrip.getId(),
                savedTrip.getTitle(),
                savedTrip.getDescription(),
                savedTrip.getStartDate(),
                savedTrip.getEndDate()
        );
    }

    public List<TripResponse> getTripsForUser(String email){
        return tripRepository.findByUserEmail(email)
                .stream()
                .map(trip -> new TripResponse(
                        trip.getId(),
                        trip.getTitle(),
                        trip.getDescription(),
                        trip.getStartDate(),
                        trip.getEndDate()
                ))
                .toList();
    }

    public void deleteTrip(Long tripId, String email){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        if(!trip.getUser().getEmail().equals(email)){
            throw new AccessDeniedException("You are not allowed to delete this trip");
        }
        tripRepository.delete(trip);
    }

    public TripResponse updateTrip(Long id, String email, UpdateTripRequest request){

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if(!trip.getUser().getEmail().equals(email)){
            throw new AccessDeniedException("You are not allowed to update this trip");
        }

        if(request.getTitle() != null)
            trip.setTitle(request.getTitle());
        if(request.getDescription() != null)
            trip.setDescription(request.getDescription());
        if(request.getStartDate() != null)
            trip.setStartDate(request.getStartDate());
        if(request.getEndDate() != null)
            trip.setEndDate(request.getEndDate());

        Trip updated = tripRepository.save(trip);

        return new TripResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getStartDate(),
                updated.getEndDate()
        );
    }
}
















