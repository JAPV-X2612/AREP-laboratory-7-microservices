package edu.eci.arep.service;

import edu.eci.arep.entity.Post;

import java.util.List;

/**
 * Contract for reading the global public post stream.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface StreamService {

    /**
     * Returns the complete public stream of all posts, newest first.
     *
     * @return ordered list of all posts
     */
    List<Post> getStream();
}
