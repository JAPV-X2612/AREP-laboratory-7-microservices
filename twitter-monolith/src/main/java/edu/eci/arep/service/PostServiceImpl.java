package edu.eci.arep.service;

import edu.eci.arep.entity.AppUser;
import edu.eci.arep.entity.Post;
import edu.eci.arep.exception.UserNotFoundException;
import edu.eci.arep.repository.AppUserRepository;
import edu.eci.arep.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of PostService using JPA persistence.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AppUserRepository userRepository;

    /**
     * @param postRepository JPA repository for Post entities
     * @param userRepository JPA repository for AppUser entities
     */
    public PostServiceImpl(PostRepository postRepository, AppUserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     *
     * @throws UserNotFoundException if no user exists for the given auth0Id
     */
    @Override
    @Transactional
    public Post createPost(String content, String auth0Id) {
        AppUser author = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new UserNotFoundException(auth0Id));
        return postRepository.save(new Post(content, author));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
