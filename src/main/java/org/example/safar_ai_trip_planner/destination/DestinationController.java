package org.example.safar_ai_trip_planner.destination;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.destination.dto.DestinationResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @PostMapping
    public DestinationResponse createDestination(@RequestBody Destination destination){
        return destinationService.createDestination(destination);
    }

    @GetMapping
    public List<DestinationResponse> getAllDestinations(){
        return destinationService.getAllDestinations();
    }

    @GetMapping("/{id}")
    public DestinationResponse getDestinationById(@PathVariable Long id){
        return destinationService.getDestinationById(id);
    }

    @PutMapping("/{id}")
    public DestinationResponse updateDestination(
            @PathVariable Long id,
            @RequestBody Destination destination){
        return destinationService.updateDestination(id, destination);
    }

    @DeleteMapping("/{id}")
    public String deleteDestination(@PathVariable Long id){
        destinationService.deleteDestination(id);
        return "Destination deleted successfully";
    }

}
