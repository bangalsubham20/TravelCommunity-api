package com.travelcommunity.repository;

import com.travelcommunity.model.Review;
import com.travelcommunity.model.Trip;
import com.travelcommunity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTrip(Trip trip);

    List<Review> findByUser(User user);

    Optional<Review> findByTripAndUser(Trip trip, User user);
}
