package com.codomania.security.service;

import com.codomania.security.model.Video;
import com.codomania.security.repo.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
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
}
