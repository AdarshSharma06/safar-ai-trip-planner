package org.example.safar_ai_trip_planner.itinerary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DayPlanResponse {
    private Integer dayNumber;
    private LocalDate date;
    private List<StepResponse> steps;
}
