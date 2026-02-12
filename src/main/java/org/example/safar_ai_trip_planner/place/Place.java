package org.example.safar_ai_trip_planner.place;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.example.safar_ai_trip_planner.common.enums.PlaceType;
import org.example.safar_ai_trip_planner.destination.Destination;

@Entity
@Table(name="places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(length=2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private PlaceType type;

    private Double rating;

    private String imageUrl;

    private Double latitude;
    private Double longitude;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="destination_id", nullable=false)
    @JsonIgnoreProperties({"places"})
    private Destination destination;
}
