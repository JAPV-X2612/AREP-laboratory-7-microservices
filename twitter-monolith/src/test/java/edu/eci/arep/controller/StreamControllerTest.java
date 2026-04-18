package edu.eci.arep.controller;

import edu.eci.arep.entity.AppUser;
import edu.eci.arep.entity.Post;
import edu.eci.arep.service.StreamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice tests for StreamController verifying the public stream endpoint.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@WebMvcTest(StreamController.class)
class StreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StreamService streamService;

    @Test
    void getStream_isPublicAndReturnsAllPosts() throws Exception {
        AppUser user = new AppUser("auth0|1", "a@b.com", "alice");
        when(streamService.getStream()).thenReturn(List.of(
                new Post("First post", user),
                new Post("Second post", user)));

        mockMvc.perform(get("/api/stream"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("First post"));
    }

    @Test
    void getStream_whenEmpty_returnsEmptyArray() throws Exception {
        when(streamService.getStream()).thenReturn(List.of());

        mockMvc.perform(get("/api/stream"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
