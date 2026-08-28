package com.sundaysoul.repository;

import com.sundaysoul.model.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findByEmail(String email);

    Optional<PendingUser> findByEmailAndOtp(String email, String otp);

    void deleteByEmail(String email);
}
