package edu.eci.arep.service;

import edu.eci.arep.entity.Post;

import java.util.List;

/**
 * Contract for post creation and retrieval operations.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface PostService {

    /**
     * Creates and persists a new post authored by the given user.
     *
     * @param content  post body, max 140 characters
     * @param auth0Id  Auth0 subject of the authenticated author
     * @return the newly persisted Post
     */
    Post createPost(String content, String auth0Id);

    /**
     * Retrieves all posts ordered from newest to oldest.
     *
     * @return list of all posts
     */
    List<Post> getAllPosts();
}
