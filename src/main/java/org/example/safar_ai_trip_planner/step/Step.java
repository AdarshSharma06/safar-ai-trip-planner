package org.example.safar_ai_trip_planner.step;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.safar_ai_trip_planner.common.BaseEntity;
import org.example.safar_ai_trip_planner.dayplan.DayPlan;
import org.example.safar_ai_trip_planner.place.Place;


@Entity
@Table(name="steps")
@Getter
@Setter
public class Step extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="day_plan_id", nullable=false)
    private DayPlan dayPlan;

    @ManyToOne
    @JoinColumn(name="place_id")
    private Place place;

    @Column(nullable=false)
    private Integer stepOrder;

    @Column(nullable=false, length=1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private StepStatus status;
}
