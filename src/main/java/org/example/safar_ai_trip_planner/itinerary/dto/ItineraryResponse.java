package org.example.safar_ai_trip_planner.itinerary.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.safar_ai_trip_planner.itinerary.ItineraryStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ItineraryResponse {
    private Long itineraryId;
    private ItineraryStatus status;
    private LocalDate generatedAt;
    private List<DayPlanResponse> days;
}
