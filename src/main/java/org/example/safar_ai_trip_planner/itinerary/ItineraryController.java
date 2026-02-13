package org.example.safar_ai_trip_planner.itinerary;

import lombok.RequiredArgsConstructor;
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
    public String generateItinerary(@PathVariable Long tripId){
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        itineraryService.generateItinerary(tripId, email);

        return "Itinerary generated successfully";
    }
    @GetMapping("/{tripId}")
    public ItineraryResponse getItinerary(@PathVariable Long tripId){
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return itineraryService.getItineraryByTripId(tripId, email);
    }

    @PatchMapping("/steps/{stepId}/status")
    public String updateStepStatus(@PathVariable Long stepId,@RequestBody UpdateStepStatusRequest request,
                                   Authentication authentication){
        String userEmail = authentication.getName();
        itineraryService.updateStepStatus(stepId, request.getStatus(), userEmail);
        return "Step status updated successfully";
    }
}
