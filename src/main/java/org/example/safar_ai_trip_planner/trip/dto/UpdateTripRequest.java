package org.example.safar_ai_trip_planner.trip.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTripRequest {

    private LocalDate startDate;

    private Integer duration;

    private Double budget;

    private String tripStyle;

    private String transportPreference;

    private String foodPreference;

    private String interests;
}
