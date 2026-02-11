package org.example.safar_ai_trip_planner.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TripResponse {

    private Long id ;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
