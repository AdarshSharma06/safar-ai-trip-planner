package org.example.safar_ai_trip_planner.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class TripResponse {

    private Long id ;
    private Long destinationId;
    private String destinationName;
    private LocalDate startDate;
    private Integer duration;
    private Double budget;
    private String tripStyle;
    private String transportPreference;
    private String foodPreference;
    private String interests;
}
