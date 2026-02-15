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
import org.example.safar_ai_trip_planner.trip.TripService;
import org.example.safar_ai_trip_planner.common.util.DistanceUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItineraryService {
    private final TripRepository tripRepository;
    private final TripService tripService;
    private final ItineraryRepository itineraryRepository;
    private final DayPlanRepository dayPlanRepository;
    private final StepRepository stepRepository;
    private final PlaceRepository placeRepository;

    @CacheEvict(value = "itineraries", key = "#tripId + '_' + #userEmail")
    public Itinerary generateItinerary(Long tripId, String userEmail){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        tripService.validateTripOwnership(trip, userEmail);
        itineraryRepository.findByTripId(tripId).ifPresent(existing->{
            throw new RuntimeException("Itinerary already generated for this trip");
        });

        Long destinationId = trip.getDestination().getId();
        List<Place> attractions = placeRepository
                .findByDestinationIdAndType(destinationId, PlaceType.ATTRACTION);
        List<Place> hotels = placeRepository
                .findByDestinationIdAndType(destinationId, PlaceType.HOTEL);
        List<Place> restaurants = placeRepository
                .findByDestinationIdAndType(destinationId, PlaceType.RESTAURANT);
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

            int currentStepOrder = 1;
            if(day == 1){
                List<Step> transportSteps = createTransportAndStops(savedDayPlan, trip);

                for(Step s : transportSteps){
                    s.setStepOrder(currentStepOrder++);
                    stepRepository.save(s);

                }
            }
            if(day == 1){
                if(!hotels.isEmpty()){
                    Place hotel = hotels.get(0);

                    Step hotelStep = new Step();
                    hotelStep.setDayPlan(savedDayPlan);
                    hotelStep.setPlace(hotel);
                    hotelStep.setStepOrder(currentStepOrder++);
                    hotelStep.setDescription("Check-in at " + hotel.getName());
                    hotelStep.setStatus(StepStatus.PENDING);

                    stepRepository.save(hotelStep);

                }else{
                    Step fallbackHotel = new Step();
                    fallbackHotel.setDayPlan(savedDayPlan);
                    fallbackHotel.setStepOrder(currentStepOrder++);
                    fallbackHotel.setDescription("Find and check-in to a hotel near city center");
                    fallbackHotel.setStatus(StepStatus.PENDING);

                    stepRepository.save(fallbackHotel);
                }
            }
            for(int i = 0; i<placesPerDay; i++){
                if(attractionIndex >= attractions.size()) break ;

                Place place = attractions.get(attractionIndex++);
                Step step = new Step();
                step.setDayPlan(savedDayPlan);
                step.setPlace(place);
                step.setStepOrder(currentStepOrder++);
                step.setDescription("Visit "+place.getName());
                step.setStatus(StepStatus.PENDING);

                stepRepository.save(step);
            }

            if(!restaurants.isEmpty()){
                Place restaurant = restaurants.get(day % restaurants.size());

                Step foodStep = new Step();
                foodStep.setDayPlan(savedDayPlan);
                foodStep.setPlace(restaurant);
                foodStep.setStepOrder(currentStepOrder);
                foodStep.setDescription("Dinner at " + restaurant.getName());
                foodStep.setStatus(StepStatus.PENDING);

                stepRepository.save(foodStep);

            }else{
                Step fallbackFood = new Step();
                fallbackFood.setDayPlan(savedDayPlan);
                fallbackFood.setStepOrder(currentStepOrder);
                fallbackFood.setDescription("Explore local restaurants nearby");
                fallbackFood.setStatus(StepStatus.PENDING);

                stepRepository.save(fallbackFood);
            }
        }
        return savedItinerary;
    }

    private Step createTransportStep(DayPlan dayPlan, Trip trip){

        String sourceCity = trip.getSourceCity();
        String destinationCity = trip.getDestination().getName();
        String userPreference = trip.getTransportPreference();

        Double sourceLat = trip.getSourceLatitude();
        Double sourceLon = trip.getSourceLongitude();
        Double destLat = trip.getDestination().getLatitude();
        Double destLon = trip.getDestination().getLongitude();

        String description;

        double distanceKm = 0;
        boolean hasCoordinates = sourceLat != null && sourceLon != null
                && destLat != null && destLon != null;

        if(hasCoordinates){
            distanceKm = DistanceUtil.calculateDistance(
                    sourceLat, sourceLon,
                    destLat, destLon
            );
        }

        if(userPreference != null){
            switch (userPreference.toUpperCase()){
                case "FLIGHT" -> description =
                        "Recommended flight from " + sourceCity + " to " + destinationCity +
                                " (Distance: ~" + (int)distanceKm + " km). Reach airport 2 hours early.";

                case "TRAIN" -> description =
                        "Recommended train journey from " + sourceCity + " to " + destinationCity +
                                " (~" + (int)distanceKm + " km). Overnight travel suggested for comfort.";

                case "SELF" -> {
                    if(distanceKm > 500){
                        description =
                                "Long road trip (~" + (int)distanceKm + " km) from " + sourceCity +
                                        " to " + destinationCity +
                                        ". Start early morning and take rest stops every 3-4 hours.";
                    } else {
                        description =
                                "Comfortable road trip (~" + (int)distanceKm + " km) from " +
                                        sourceCity + " to " + destinationCity +
                                        ". Best to start early to avoid traffic.";
                    }
                }

                default -> description =
                        "Travel from " + sourceCity + " to " + destinationCity +
                                " (~" + (int)distanceKm + " km).";
            }
        }
        else {

            if(distanceKm > 800){
                description =
                        "Long distance journey (~" + (int)distanceKm + " km). Flight is the fastest option from "
                                + sourceCity + " to " + destinationCity + ".";
            }
            else if(distanceKm > 300){
                description =
                        "Medium distance (~" + (int)distanceKm + " km). Train is a convenient option from "
                                + sourceCity + " to " + destinationCity + ".";
            }
            else{
                description =
                        "Short distance (~" + (int)distanceKm + " km). Self-drive or cab recommended from "
                                + sourceCity + " to " + destinationCity + ".";
            }
        }

        Step step = new Step();
        step.setDayPlan(dayPlan);
        step.setStepOrder(0);
        step.setDescription(description);
        step.setStatus(StepStatus.PENDING);
        step.setPlace(null);

        return step;
    }
    private List<Step> createTransportAndStops(DayPlan dayPlan, Trip trip){
        List<Step> steps = new java.util.ArrayList<>();
        Step mainTransportStep = createTransportStep(dayPlan, trip);
        mainTransportStep.setStepOrder(0);
        steps.add(mainTransportStep);


        String userPreference = trip.getTransportPreference();

        Double sourceLat = trip.getSourceLatitude();
        Double sourceLon = trip.getSourceLongitude();
        Double destLat = trip.getDestination().getLatitude();
        Double destLon = trip.getDestination().getLongitude();

        double distanceKm = 0;
        boolean hasCoordinates = sourceLat != null && sourceLon != null
                && destLat != null && destLon != null;

        if(hasCoordinates){
            distanceKm = DistanceUtil.calculateDistance(
                    sourceLat, sourceLon,
                    destLat, destLon
            );
        }

        if(userPreference != null && userPreference.equalsIgnoreCase("SELF")){

            int stepOrderCounter = 1;

            if(distanceKm > 400){

                Step restStop = new Step();
                restStop.setDayPlan(dayPlan);
                restStop.setStepOrder(stepOrderCounter++);
                restStop.setDescription(
                        "Recommended rest stop during the journey (~" +
                                (int)(distanceKm / 2) +
                                " km mark) to relax and refuel."
                );
                restStop.setStatus(StepStatus.PENDING);
                restStop.setPlace(null);
                steps.add(restStop);

                Step foodStop = new Step();
                foodStop.setDayPlan(dayPlan);
                foodStop.setStepOrder(stepOrderCounter++);
                foodStop.setDescription(
                        "Suggested meal break on the way to avoid fatigue during the road trip."
                );
                foodStop.setStatus(StepStatus.PENDING);
                foodStop.setPlace(null);
                steps.add(foodStop);
            }

            if(distanceKm > 1000){
                Step overnightStop = new Step();
                overnightStop.setDayPlan(dayPlan);
                overnightStop.setStepOrder(stepOrderCounter++);
                overnightStop.setDescription(
                        "Very long distance journey (~" + (int)distanceKm +
                                " km). Consider an overnight hotel stop for safety and comfort."
                );
                overnightStop.setStatus(StepStatus.PENDING);
                overnightStop.setPlace(null);
                steps.add(overnightStop);
            }
        }
        return steps;
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
    @Cacheable(value = "itineraries", key = "#tripId + '_' + #userEmail")
    public ItineraryResponse getItineraryByTripId(Long tripId, String userEmail){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        tripService.validateTripOwnership(trip,userEmail);
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
    @CacheEvict(value = "itineraries", allEntries = true)
    public void updateStepStatus(Long stepId, StepStatus newStatus, String userEmail){
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Step not found"));

        Trip trip = step.getDayPlan()
                .getItinerary()
                .getTrip();
        tripService.validateTripOwnership(trip, userEmail);
        step.setStatus(newStatus);
        stepRepository.save(step);
    }
}
