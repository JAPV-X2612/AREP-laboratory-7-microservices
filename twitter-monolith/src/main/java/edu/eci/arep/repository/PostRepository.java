package edu.eci.arep.repository;

import edu.eci.arep.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for Post entities.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Retrieves all posts ordered by creation time descending (newest first).
     *
     * @return list of posts sorted from newest to oldest
     */
    List<Post> findAllByOrderByCreatedAtDesc();
}
