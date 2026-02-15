package org.example.safar_ai_trip_planner.trip;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.safar_ai_trip_planner.common.BaseEntity;
import org.example.safar_ai_trip_planner.destination.Destination;
import org.example.safar_ai_trip_planner.user.User;

import java.time.LocalDate;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable=false)
    private Destination destination;

    private LocalDate startDate;
    private Integer duration;

    private Double budget;
    private String tripStyle;

    private String transportPreference;

    private String foodPreference;

    @Column(length=1000)
    private String interests;

    @Column(nullable = false)
    private String sourceCity;

    private Double sourceLatitude;

    private Double sourceLongitude;

}
