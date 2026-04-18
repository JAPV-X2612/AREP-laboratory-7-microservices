package edu.eci.arep.service;

import edu.eci.arep.entity.AppUser;
import edu.eci.arep.entity.Post;
import edu.eci.arep.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for StreamServiceImpl verifying stream delegation to PostRepository.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@ExtendWith(MockitoExtension.class)
class StreamServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private StreamServiceImpl streamService;

    @Test
    void getStream_returnsAllPostsFromRepository() {
        AppUser user = new AppUser("auth0|1", "a@b.com", "alice");
        List<Post> expected = List.of(new Post("Hi", user), new Post("Hello", user));
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expected);

        List<Post> result = streamService.getStream();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getStream_whenEmpty_returnsEmptyList() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        assertThat(streamService.getStream()).isEmpty();
    }
}
