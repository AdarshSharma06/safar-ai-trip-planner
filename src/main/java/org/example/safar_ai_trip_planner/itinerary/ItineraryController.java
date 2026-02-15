package org.example.safar_ai_trip_planner.itinerary;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.ApiResponse;
import org.example.safar_ai_trip_planner.itinerary.dto.ItineraryResponse;
import org.example.safar_ai_trip_planner.step.dto.UpdateStepStatusRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
public class ItineraryController {
    private final ItineraryService itineraryService;

    @PostMapping("/{tripId}/generate")
    public ApiResponse<ItineraryResponse> generateItinerary(@PathVariable Long tripId){
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        itineraryService.generateItinerary(tripId, email);
        ItineraryResponse response = itineraryService.getItineraryByTripId(tripId, email);
        return ApiResponse.success("Itinerary generated successfully", response);
    }
    @GetMapping("/{tripId}")
    public ApiResponse<ItineraryResponse> getItinerary(@PathVariable Long tripId){
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        ItineraryResponse response =  itineraryService.getItineraryByTripId(tripId, email);
        return ApiResponse.success("Itinerary fetched successfully", response);
    }

    @PatchMapping("/steps/{stepId}/status")
    public ApiResponse<Void> updateStepStatus(@PathVariable Long stepId,@RequestBody UpdateStepStatusRequest request,
                                   Authentication authentication){
        String userEmail = authentication.getName();
        itineraryService.updateStepStatus(stepId, request.getStatus(), userEmail);
        return ApiResponse.success("Step status updated successfully", null);
    }
}
