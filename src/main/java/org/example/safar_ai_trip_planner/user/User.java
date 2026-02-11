package org.example.safar_ai_trip_planner.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.safar_ai_trip_planner.common.BaseEntity;
import org.example.safar_ai_trip_planner.trip.Trip;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable=false)
    @NotBlank
    private String name;

    @Column(nullable=false, unique=true)
    @NotBlank
    @Email
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable=false)
    @NotBlank
    @Size(min=6)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade= CascadeType.ALL)
    private List<Trip> trips;

}
