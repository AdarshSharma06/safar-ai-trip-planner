package org.example.safar_ai_trip_planner.trip;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.safar_ai_trip_planner.common.BaseEntity;
import org.example.safar_ai_trip_planner.user.User;

import java.time.LocalDate;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip extends BaseEntity {

    @Column(nullable=false)
    private String title;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User user;
}
