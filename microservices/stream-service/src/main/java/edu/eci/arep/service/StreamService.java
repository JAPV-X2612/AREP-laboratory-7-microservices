package edu.eci.arep.service;

import edu.eci.arep.dto.StreamResponse;

/**
 * Contract for reading the global public post stream in the stream-service Lambda.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface StreamService {

    /**
     * Retrieves the complete public stream of all posts, newest first.
     *
     * @return a StreamResponse containing all posts ordered by creation time descending
     */
    StreamResponse getStream();
}
