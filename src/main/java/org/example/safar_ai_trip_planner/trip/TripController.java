package org.example.safar_ai_trip_planner.trip;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.ApiResponse;
import org.example.safar_ai_trip_planner.trip.dto.CreateTripRequest;
import org.example.safar_ai_trip_planner.trip.dto.TripResponse;
import org.example.safar_ai_trip_planner.trip.dto.UpdateTripRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ApiResponse<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request, Authentication authentication){

        String email = authentication.getName();

        TripResponse response = tripService.createTrip(request, email);
        return ApiResponse.success("Trip created successfully", response);
    }

    @GetMapping
    public ApiResponse<List<TripResponse>> getMyTrips(Authentication authentication){
        String email = authentication.getName();
        List<TripResponse> trips =  tripService.getTripsForUser(email);
        return ApiResponse.success("Trips fetched successfully", trips);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTrip(@PathVariable Long id, Authentication authentication){

        String email = authentication.getName();
        tripService.deleteTrip(id, email);
        return ApiResponse.success("Trip deleted successfully", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<TripResponse> updateTrip(
            @PathVariable Long id,
            @RequestBody UpdateTripRequest request,
            Authentication authentication){

        String email = authentication.getName();
        TripResponse updated =  tripService.updateTrip(id, email, request);
        return ApiResponse.success("Trip updated successfully", updated);
    }
}























