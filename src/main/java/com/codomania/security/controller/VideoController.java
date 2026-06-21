package com.codomania.security.controller;

import com.codomania.security.model.Video;
import com.codomania.security.service.RentalService;
import com.codomania.security.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @PostMapping
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        Video savedVideo = videoService.createVideo(video);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVideo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(
            @PathVariable Long id,
            @RequestBody Video video) {
        return ResponseEntity.ok(videoService.updateVideo(id, video));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rent")
    public ResponseEntity<Void> rentVideo(
            @RequestParam Long userId,
            @RequestParam Long videoId) {

        System.out.println("IN CONTROLLER");
        videoService.rentVideo(userId, videoId);
        System.out.println();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnVideo(
            @RequestParam Long userId,
            @RequestParam Long videoId) {

        videoService.returnVideo(userId, videoId);

        return ResponseEntity.ok().build();
    }
}
