package org.example.safar_ai_trip_planner.itinerary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.safar_ai_trip_planner.common.BaseEntity;
import org.example.safar_ai_trip_planner.dayplan.DayPlan;
import org.example.safar_ai_trip_planner.trip.Trip;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="itineraries")
@Getter
@Setter
public class Itinerary extends BaseEntity {
    @OneToOne
    @JoinColumn(name="trip_id", nullable=false, unique=true)
    private Trip trip;

    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    private ItineraryStatus status;

    @OneToMany(mappedBy="itinerary", cascade= CascadeType.ALL, orphanRemoval= true)
    private List<DayPlan> dayPlans;
}
