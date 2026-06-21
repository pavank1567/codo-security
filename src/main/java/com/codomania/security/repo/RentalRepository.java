package com.codomania.security.repo;

import com.codomania.security.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByUserIdAndReturnedAtIsNull(Long userId);

    Optional<Rental> findByUserIdAndVideoIdAndReturnedAtIsNull(
            Long userId,
            Long videoId);
}
