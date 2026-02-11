package org.example.safar_ai_trip_planner.trip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTripRequest {

    @NotBlank
    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}
