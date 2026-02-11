package com.sundaysoul.config;

import com.sundaysoul.model.Trip;
import com.sundaysoul.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private TripRepository tripRepository;

    @Override
    public void run(String... args) throws Exception {
        if (tripRepository.count() == 0) {
            System.out.println("Seeding initial trips...");

            Trip trip1 = Trip.builder()
                    .name("Mist of Munnar")
                    .destination("Munnar, Kerala")
                    .description(
                            "Experience the breathtaking tea gardens and misty hills of Munnar. A perfect getaway for nature lovers.")
                    .price(12999.0)
                    .duration(3)
                    .startDate(LocalDateTime.now().plusDays(10))
                    .endDate(LocalDateTime.now().plusDays(13))
                    .groupSize(15)
                    .difficulty("Easy")
                    .season("Winter")
                    .bestSeason("September to March")
                    .altitude("1600m")
                    .availableSeats(15)
                    .image("https://images.unsplash.com/photo-1596328892468-12b7a9de281e?q=80&w=2070&auto=format&fit=crop")
                    .highlights("Tea Museum visit, Trekking to Top Station, Campfire night")
                    .itinerary("Day 1: Arrival and Tea Museum. Day 2: Trekking. Day 3: Departure.")
                    .inclusions("Accommodation, Meals, Guide")
                    .exclusions("Travel to Munnar, Personal expenses")
                    .active(true)
                    .build();

            Trip trip2 = Trip.builder()
                    .name("Goa Beach Vibes")
                    .destination("North Goa")
                    .description(
                            "Sun, sand, and sea! Join us for an unforgettable beach party and water sports adventure in Goa.")
                    .price(15999.0)
                    .duration(4)
                    .startDate(LocalDateTime.now().plusDays(20))
                    .endDate(LocalDateTime.now().plusDays(24))
                    .groupSize(20)
                    .difficulty("Easy")
                    .season("Summer")
                    .bestSeason("November to February")
                    .altitude("Sea Level")
                    .availableSeats(20)
                    .image("https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?q=80&w=1974&auto=format&fit=crop")
                    .highlights("Beach hopping, Water sports, Night market")
                    .itinerary("Day 1: Calangute Beach. Day 2: Water sports. Day 3: Party. Day 4: Departure.")
                    .inclusions("Stay, Breakfast, Scooter rental")
                    .exclusions("Flights, Lunch/Dinner")
                    .active(true)
                    .build();

            Trip trip3 = Trip.builder()
                    .name("Himalayan Trek to Kheerganga")
                    .destination("Kasol, Himachal Pradesh")
                    .description("A thrilling trek through the Parvati Valley to the hot springs of Kheerganga.")
                    .price(8999.0)
                    .duration(5)
                    .startDate(LocalDateTime.now().plusDays(30))
                    .endDate(LocalDateTime.now().plusDays(35))
                    .groupSize(12)
                    .difficulty("Moderate")
                    .season("Spring")
                    .bestSeason("April to June")
                    .altitude("2960m")
                    .availableSeats(12)
                    .image("https://images.unsplash.com/photo-1526716173434-a1b560f2065d?q=80&w=2070&auto=format&fit=crop")
                    .highlights("Hot springs, Camping under stars, Cafe hopping in Kasol")
                    .itinerary(
                            "Day 1: Reach Kasol. Day 2: Trek to Kheerganga. Day 3: Descent. Day 4: Manikaran. Day 5: Departure.")
                    .inclusions("Camping gear, Meals on trek, Guide")
                    .exclusions("Travel to Kasol, Personal porter")
                    .active(true)
                    .build();

            tripRepository.saveAll(Arrays.asList(trip1, trip2, trip3));
            System.out.println("Seeding completed!");
        }
    }
}
