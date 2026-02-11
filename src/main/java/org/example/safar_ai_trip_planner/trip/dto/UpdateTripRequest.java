package org.example.safar_ai_trip_planner.trip.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTripRequest {

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
