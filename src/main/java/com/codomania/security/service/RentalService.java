package com.codomania.security.service;

import com.codomania.security.model.Rental;
import com.codomania.security.model.User;
import com.codomania.security.model.Video;
import com.codomania.security.repo.RentalRepository;
import com.codomania.security.repo.UserRepository;
import com.codomania.security.repo.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

public class RentalService {

    private final RentalRepository rentalRepository;

    private final UserRepository userRepository;

    private final VideoRepository videoRepository;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }


    @Transactional
    public void rentVideo(Long userId, Long videoId) {

        if (rentalRepository.countByUserIdAndReturnedAtIsNull(userId) >= 2) {
            throw new IllegalStateException(
                    "User cannot have more than two active rentals");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Video not found"));

        if (!video.getAvailabilityStatus()) {
            throw new IllegalStateException("Video is already rented");
        }

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVideo(video);
        rental.setRentedAt(LocalDateTime.now());

        video.setAvailabilityStatus(false);

        rentalRepository.save(rental);
        videoRepository.save(video);
    }

    @Transactional
    public void returnVideo(Long userId, Long videoId) {

        Rental rental = rentalRepository
                .findByUserIdAndVideoIdAndReturnedAtIsNull(userId, videoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("No active rental found"));

        rental.setReturnedAt(LocalDateTime.now());

        Video video = rental.getVideo();
        video.setAvailabilityStatus(true);

        rentalRepository.save(rental);
        videoRepository.save(video);
    }
}
