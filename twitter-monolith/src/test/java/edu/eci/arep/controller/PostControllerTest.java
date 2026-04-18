package edu.eci.arep.controller;

import edu.eci.arep.entity.AppUser;
import edu.eci.arep.entity.Post;
import edu.eci.arep.service.PostService;
import edu.eci.arep.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice tests for PostController verifying security rules and response mappings.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @Test
    void getAllPosts_isPublicAndReturns200() throws Exception {
        AppUser user = new AppUser("auth0|1", "a@b.com", "alice");
        when(postService.getAllPosts()).thenReturn(List.of(new Post("Hello", user)));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"));
    }

    @Test
    void createPost_withoutJwt_returns401() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Hello\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_withValidJwt_returns201() throws Exception {
        AppUser user = new AppUser("auth0|1", "a@b.com", "alice");
        Post saved = new Post("Hello world", user);
        when(userService.resolveUser(any(), any(), any())).thenReturn(user);
        when(postService.createPost(eq("Hello world"), eq("auth0|1"))).thenReturn(saved);

        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.subject("auth0|1")
                                .claim("email", "a@b.com")
                                .claim("nickname", "alice")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Hello world\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello world"));
    }

    @Test
    void createPost_withBlankContent_returns400() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.subject("auth0|1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
