package edu.eci.arep.service;

import edu.eci.arep.entity.AppUser;
import edu.eci.arep.entity.Post;
import edu.eci.arep.exception.UserNotFoundException;
import edu.eci.arep.repository.AppUserRepository;
import edu.eci.arep.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PostServiceImpl covering post creation and retrieval.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new AppUser("auth0|123", "user@test.com", "testuser");
    }

    @Test
    void createPost_withExistingUser_returnsPersistedPost() {
        when(userRepository.findByAuth0Id("auth0|123")).thenReturn(Optional.of(testUser));
        Post saved = new Post("Hello world", testUser);
        when(postRepository.save(any(Post.class))).thenReturn(saved);

        Post result = postService.createPost("Hello world", "auth0|123");

        assertThat(result.getContent()).isEqualTo("Hello world");
        assertThat(result.getAuthor()).isEqualTo(testUser);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_withUnknownUser_throwsUserNotFoundException() {
        when(userRepository.findByAuth0Id("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost("content", "unknown"))
                .isInstanceOf(UserNotFoundException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void getAllPosts_returnsListFromRepository() {
        Post p1 = new Post("First", testUser);
        Post p2 = new Post("Second", testUser);
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(p1, p2));

        List<Post> result = postService.getAllPosts();

        assertThat(result).hasSize(2).containsExactly(p1, p2);
    }
}
