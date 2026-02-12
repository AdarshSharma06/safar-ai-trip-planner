package org.example.safar_ai_trip_planner.place;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.enums.PlaceType;
import org.example.safar_ai_trip_planner.place.dto.PlaceResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    public PlaceResponse createPlace(@RequestParam Long destinationId, @RequestBody Place place){
        return placeService.createPlace(destinationId, place);
    }
    @GetMapping
    public List<PlaceResponse> getPlace(@RequestParam Long destinationId, @RequestParam(required=false) PlaceType type){
        if(type != null){
            return placeService.getPlacesByDestinationAndType(destinationId, type);
        }
        return placeService.getPlacesByDestination(destinationId);
    }
}
