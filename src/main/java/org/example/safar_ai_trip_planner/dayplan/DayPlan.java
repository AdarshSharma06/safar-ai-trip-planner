package org.example.safar_ai_trip_planner.dayplan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.safar_ai_trip_planner.common.BaseEntity;
import org.example.safar_ai_trip_planner.itinerary.Itinerary;
import org.example.safar_ai_trip_planner.step.Step;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="day_plans")
@Getter
@Setter
public class DayPlan extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="itinerary_id", nullable=false)
    private Itinerary itinerary;

    @Column(nullable=false)
    private Integer dayNumber;

    private LocalDate date;

    private String notes;

    @OneToMany(mappedBy="dayPlan", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Step> steps;
}
