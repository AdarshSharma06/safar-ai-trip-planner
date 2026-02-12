package org.example.safar_ai_trip_planner.destination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.example.safar_ai_trip_planner.place.Place;

import java.util.List;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String state;

    private String country;

    @Column(length=2000)
    private String description;

    private String imageUrl;

    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy="destination", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonIgnore
    private List<Place> places;
}
