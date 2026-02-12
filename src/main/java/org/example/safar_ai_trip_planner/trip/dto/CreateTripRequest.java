package org.example.safar_ai_trip_planner.trip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTripRequest {

    private Long destinationId;
    private LocalDate startDate ;
    private Integer duration;
    private Double budget;
    private String tripStyle;
    private String transportPreference;
    private String foodPreference;
    private String interests;
}
