package org.example.safar_ai_trip_planner.itinerary.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.safar_ai_trip_planner.step.StepStatus;

@Getter
@Builder
public class StepResponse {
    private Long id;
    private String placeName;
    private String description;
    private StepStatus status;
    private Integer stepOrder;
    private Double placeLatitude;
    private Double placeLongitude;
}
