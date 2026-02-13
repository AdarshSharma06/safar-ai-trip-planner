package org.example.safar_ai_trip_planner.itinerary;

import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.enums.PlaceType;
import org.example.safar_ai_trip_planner.common.exception.AccessDeniedException;
import org.example.safar_ai_trip_planner.dayplan.DayPlan;
import org.example.safar_ai_trip_planner.dayplan.DayPlanRepository;
import org.example.safar_ai_trip_planner.itinerary.dto.DayPlanResponse;
import org.example.safar_ai_trip_planner.itinerary.dto.ItineraryResponse;
import org.example.safar_ai_trip_planner.itinerary.dto.StepResponse;
import org.example.safar_ai_trip_planner.place.Place;
import org.example.safar_ai_trip_planner.place.PlaceRepository;
import org.example.safar_ai_trip_planner.step.Step;
import org.example.safar_ai_trip_planner.step.StepRepository;
import org.example.safar_ai_trip_planner.step.StepStatus;
import org.example.safar_ai_trip_planner.trip.Trip;
import org.example.safar_ai_trip_planner.trip.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItineraryService {
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final DayPlanRepository dayPlanRepository;
    private final StepRepository stepRepository;
    private final PlaceRepository placeRepository;

    public Itinerary generateItinerary(Long tripId, String userEmail){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if(!trip.getUser().getEmail().equals(userEmail)){
            throw new AccessDeniedException("You are not allowed to generate itinerary for this trip");
        }
        itineraryRepository.findByTripId(tripId).ifPresent(existing->{
            throw new RuntimeException("Itinerary already generated for this trip");
        });

        Long destinationId = trip.getDestination().getId();
        List<Place> attractions = placeRepository
                .findByDestinationIdAndType(destinationId, PlaceType.ATTRACTION);
        if(attractions.isEmpty()){
            throw new RuntimeException("No attraction found for this destination");
        }

        Collections.shuffle(attractions);

        int placesPerDay = getPlacesPerDay(trip.getTripStyle());

        Itinerary itinerary = new Itinerary();
        itinerary.setTrip(trip);
        itinerary.setGeneratedAt(java.time.LocalDateTime.now());
        itinerary.setStatus(ItineraryStatus.GENERATED);

        Itinerary savedItinerary = itineraryRepository.save(itinerary);
        int duration = trip.getDuration();
        LocalDate startDate = trip.getStartDate();
        int attractionIndex = 0;

        for(int day = 1; day <= duration; day++){
            DayPlan dayPlan = new DayPlan();
            dayPlan.setItinerary(savedItinerary);
            dayPlan.setDayNumber(day);
            dayPlan.setDate(startDate.plusDays(day-1));

            DayPlan savedDayPlan = dayPlanRepository.save(dayPlan);

            for(int stepOrder = 1; stepOrder <= placesPerDay; stepOrder++){
                if(attractionIndex >= attractions.size()) break;
                Place place = attractions.get(attractionIndex++);

                Step step = new Step();
                step.setDayPlan(savedDayPlan);
                step.setPlace(place);
                step.setStepOrder(stepOrder);
                step.setDescription("Visit "+place.getName());
                step.setStatus(StepStatus.PENDING);

                stepRepository.save(step);
            }
        }
        return savedItinerary;
    }

    private int getPlacesPerDay(String tripStyle){
        if(tripStyle == null) return 3;
        return switch (tripStyle.toUpperCase()){
            case "RELAXED" -> 2;
            case "ADVENTURE" -> 4;
            case "LUXURY" -> 2;
            default -> 3;
        };
    }

    public ItineraryResponse getItineraryByTripId(Long tripId, String userEmail){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        if(!trip.getUser().getEmail().equals(userEmail)){
            throw new AccessDeniedException("You are not allowed to access this itinerary");
        }
        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new RuntimeException("Itinerary not generated yet"));
        return mapToItineraryResponse(itinerary);
    }

    private ItineraryResponse mapToItineraryResponse(Itinerary itinerary) {
        List<DayPlanResponse> dayResponses = itinerary.getDayPlans()
                .stream()
                .sorted(Comparator.comparingInt(DayPlan::getDayNumber))
                .map(dayPlan -> {

                    List<StepResponse> stepResponses = dayPlan.getSteps()
                            .stream()
                            .sorted(Comparator.comparingInt(Step::getStepOrder))
                            .map(step -> StepResponse.builder()
                                    .id(step.getId())
                                    .description(step.getDescription())
                                    .status(step.getStatus())
                                    .stepOrder(step.getStepOrder())
                                    .placeName(step.getPlace() != null ? step.getPlace().getName() : null)
                                    .placeLatitude(step.getPlace() != null ? step.getPlace().getLatitude() : null)
                                    .placeLongitude(step.getPlace() != null ? step.getPlace().getLongitude() : null)
                                    .build())
                            .toList();

                    return DayPlanResponse.builder()
                            .dayNumber(dayPlan.getDayNumber())
                            .date(dayPlan.getDate())
                            .steps(stepResponses)
                            .build();


                })
                .toList();

        return ItineraryResponse.builder()
                .itineraryId(itinerary.getId())
                .status(itinerary.getStatus())
                .generatedAt(itinerary.getGeneratedAt().toLocalDate())
                .days(dayResponses)
                .build();
    }

    public void updateStepStatus(Long stepId, StepStatus newStatus, String userEmail){
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Step not found"));

        Trip trip = step.getDayPlan()
                .getItinerary()
                .getTrip();
        if(!trip.getUser().getEmail().equals(userEmail)){
            throw new AccessDeniedException("You are nto allowed to update this step");
        }
        step.setStatus(newStatus);
        stepRepository.save(step);
    }
}
