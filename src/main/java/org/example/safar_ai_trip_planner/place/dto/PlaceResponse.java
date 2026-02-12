package org.example.safar_ai_trip_planner.place.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {

    private Long id;
    private String name;
    private String description;
    private String type;
    private Double rating ;
    private String imageUrl;
    private Double latitude;
    private Double longitude;

    private Long destinationId;
    private String destinationName;
}
