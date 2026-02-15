package org.example.safar_ai_trip_planner.trip.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTripRequest {
    @NotNull(message="Destination is required")
    private Long destinationId;
    @NotNull(message="Start date is required")
    private LocalDate startDate ;
    @Min(value=1,message="Duration must be at least 1 day")
    private Integer duration;
    private Double budget;
    private String tripStyle;
    private String transportPreference;
    private String foodPreference;
    private String interests;
    private String sourceCity;
    private Double sourceLatitude;
    private Double sourceLongitude;
}
