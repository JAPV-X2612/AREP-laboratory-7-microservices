package edu.eci.arep.controller;

import edu.eci.arep.dto.PostResponse;
import edu.eci.arep.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes the single global public post stream at GET /api/stream.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@RestController
@RequestMapping("/api/stream")
@Tag(name = "Stream", description = "Global public post stream")
public class StreamController {

    private final StreamService streamService;

    /**
     * @param streamService service for reading the global stream
     */
    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    /**
     * Returns the complete public stream of all posts, newest first.
     * No authentication is required.
     *
     * @return 200 with list of PostResponse DTOs
     */
    @GetMapping
    @Operation(summary = "Get public stream", description = "Returns the global public post stream ordered newest first. No authentication required.")
    public ResponseEntity<List<PostResponse>> getStream() {
        List<PostResponse> stream = streamService.getStream().stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(stream);
    }
}
