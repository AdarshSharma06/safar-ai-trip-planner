package org.example.safar_ai_trip_planner.trip;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.trip.dto.CreateTripRequest;
import org.example.safar_ai_trip_planner.trip.dto.TripResponse;
import org.example.safar_ai_trip_planner.trip.dto.UpdateTripRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public TripResponse createTrip(@Valid @RequestBody CreateTripRequest request){

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return tripService.createTrip(request, email);
    }

    @GetMapping
    public List<TripResponse> getMyTrips(){
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return tripService.getTripsForUser(email);
    }

    @DeleteMapping("/{id}")
    public void deleteTrip(@PathVariable Long id){

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        tripService.deleteTrip(id, email);
    }

    @PutMapping("/{id}")
    public TripResponse updateTrip(
            @PathVariable Long id,
            @RequestBody UpdateTripRequest request){

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return tripService.updateTrip(id, email, request);
    }
}























