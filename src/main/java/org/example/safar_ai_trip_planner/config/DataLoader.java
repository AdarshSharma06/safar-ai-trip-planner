package org.example.safar_ai_trip_planner.config;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.common.enums.PlaceType;
import org.example.safar_ai_trip_planner.destination.Destination;
import org.example.safar_ai_trip_planner.destination.DestinationRepository;
import org.example.safar_ai_trip_planner.place.Place;
import org.example.safar_ai_trip_planner.place.PlaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

//@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DestinationRepository destinationRepository;
    private final PlaceRepository placeRepository;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Destination count: " + destinationRepository.count());
        System.out.println("Place count: " + placeRepository.count());

        if (destinationRepository.count() == 0) {
            loadDestinations();
        }

        if (placeRepository.count() == 0) {
            loadPlaces();
        }

        System.out.println("FINAL Destination count: " + destinationRepository.count());
        System.out.println("FINAL Place count: " + placeRepository.count());
    }

    private void loadDestinations() throws Exception {

        CSVReader reader = new CSVReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("/destinations.csv")
                )
        );

        reader.readNext();

        String[] data;

        while ((data = reader.readNext()) != null) {

            Destination destination = new Destination();
            destination.setName(data[0].trim());
            destination.setState(data[1].trim());
            destination.setCountry(data[2].trim());
            destination.setDescription(data[3]);
            destination.setImageUrl(data[4]);

            double lat = 0.0;
            if (data[5] != null && !data[5].isBlank()) {
                lat = Double.parseDouble(data[5]);
            }
            destination.setLatitude(lat);

            double lon = 0.0;
            if (data[6] != null && !data[6].isBlank()) {
                lon = Double.parseDouble(data[6]);
            }
            destination.setLongitude(lon);

            destinationRepository.save(destination);
        }

        reader.close();
    }

    private void loadPlaces() throws Exception {

        Map<String, Destination> destinationMap = new HashMap<>();

        destinationRepository.findAll()
                .forEach(dest ->
                        destinationMap.put(
                                dest.getName().trim().toLowerCase(),
                                dest
                        )
                );

        CSVReader reader = new CSVReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("/places.csv")
                )
        );

        reader.readNext();

        String[] data;

        while ((data = reader.readNext()) != null) {

            if (data.length < 8) continue;

            String destinationName = data[7].trim();

            Destination destination =
                    destinationMap.get(destinationName.toLowerCase());


            if (destination == null) {

                destination = new Destination();
                destination.setName(destinationName);
                destination.setState("Unknown");
                destination.setCountry("India");
                destination.setDescription("Auto-created destination");
                destination.setImageUrl("");
                destination.setLatitude(0.0);
                destination.setLongitude(0.0);

                destination = destinationRepository.save(destination);

                destinationMap.put(destinationName.toLowerCase(), destination);

                System.out.println("Created missing destination: " + destinationName);
            }

            Place place = new Place();
            place.setName(data[0].trim());
            place.setDescription(data[1]);

            try {
                place.setType(PlaceType.valueOf(data[2].trim()));
            } catch (Exception e) {
                continue;
            }

            double rating = 0.0;
            if (data[3] != null && !data[3].isBlank()) {
                try {
                    rating = Double.parseDouble(data[3]);
                } catch (Exception ignored) {}
            }
            place.setRating(rating);

            place.setImageUrl(data[4]);

            double lat = 0.0;
            if (data[5] != null && !data[5].isBlank()) {
                try {
                    lat = Double.parseDouble(data[5]);
                } catch (Exception ignored) {}
            }
            place.setLatitude(lat);

            double lon = 0.0;
            if (data[6] != null && !data[6].isBlank()) {
                try {
                    lon = Double.parseDouble(data[6]);
                } catch (Exception ignored) {}
            }
            place.setLongitude(lon);

            place.setDestination(destination);

            placeRepository.save(place);
        }

        reader.close();
    }

}
