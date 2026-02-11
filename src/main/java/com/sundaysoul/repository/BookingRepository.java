package com.sundaysoul.repository;

import com.sundaysoul.model.Booking;
import com.sundaysoul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByUserOrderByBookingDateDesc(User user);

    List<Booking> findByStatusOrderByBookingDateDesc(String status);
}
