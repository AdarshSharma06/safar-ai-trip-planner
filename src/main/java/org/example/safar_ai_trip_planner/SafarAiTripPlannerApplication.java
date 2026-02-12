package org.example.safar_ai_trip_planner;

import org.example.safar_ai_trip_planner.destination.Destination;
import org.example.safar_ai_trip_planner.destination.DestinationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SafarAiTripPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafarAiTripPlannerApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(DestinationRepository destinationRepository){
        return args -> {
            if(destinationRepository.count()==0){
                destinationRepository.save(
                        Destination.builder()
                                .name("Goa")
                                .state("Goa")
                                .country("India")
                                .description("Famous beach destination")
                                .imageUrl("goa.jpg")
                                .latitude(15.2993)
                                .longitude(74.1240)
                                .build()
                );
            }
        };
    }
}
