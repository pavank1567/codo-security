package com.codomania.security.service;

import com.codomania.security.exception.NotAvailableForRentException;
import com.codomania.security.exception.RentalLimitExceededException;
import com.codomania.security.model.Rental;
import com.codomania.security.model.User;
import com.codomania.security.model.Video;
import com.codomania.security.repo.RentalRepository;
import com.codomania.security.repo.UserRepository;
import com.codomania.security.repo.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public VideoService(VideoRepository videoRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Video createVideo(Video video) {
        if(video.getAvailabilityStatus() == null){
            video.setAvailabilityStatus(true);
        }
        return videoRepository.save(video);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Video not found with id: " + id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Video updateVideo(Long id, Video updatedVideo) {
        Video existingVideo = getVideoById(id);

        existingVideo.setTitle(updatedVideo.getTitle());
        existingVideo.setDirector(updatedVideo.getDirector());
        existingVideo.setGenre(updatedVideo.getGenre());
        if(updatedVideo.getAvailabilityStatus() == null)
            existingVideo.setAvailabilityStatus(true);
        else
            existingVideo.setAvailabilityStatus(updatedVideo.getAvailabilityStatus());

        return videoRepository.save(existingVideo);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }


    @Transactional
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public void rentVideo(Long userId, Long videoId) {

        if (rentalRepository.countByUserIdAndReturnedAtIsNull(userId) >= 2) {
            throw new RentalLimitExceededException(
                    "User cannot have more than two active rentals");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Video not found"));

        if (!video.getAvailabilityStatus()) {
            throw new NotAvailableForRentException("Video is already rented");
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
