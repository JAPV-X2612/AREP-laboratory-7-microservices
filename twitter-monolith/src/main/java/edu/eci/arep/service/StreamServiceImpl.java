package edu.eci.arep.service;

import edu.eci.arep.entity.Post;
import edu.eci.arep.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of StreamService that delegates to PostRepository.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@Service
public class StreamServiceImpl implements StreamService {

    private final PostRepository postRepository;

    /**
     * @param postRepository JPA repository for Post entities
     */
    public StreamServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Post> getStream() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
