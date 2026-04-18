package edu.eci.arep.service;

import edu.eci.arep.model.Post;

import java.util.List;

/**
 * Contract for post persistence and retrieval in the post-service Lambda.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface PostService {

    /**
     * Creates and persists a new post in DynamoDB.
     *
     * @param content        post body, max 140 characters
     * @param authorId       Auth0 subject of the authenticated author
     * @param authorNickname display name of the author
     * @return the newly created Post
     */
    Post createPost(String content, String authorId, String authorNickname);

    /**
     * Retrieves all posts ordered from newest to oldest.
     *
     * @return list of all posts
     */
    List<Post> getAllPosts();
}
