package org.example.safar_ai_trip_planner.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationResponse {

    private Long id;
    private String name;
    private String state;
    private String country;
    private String description;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
}
