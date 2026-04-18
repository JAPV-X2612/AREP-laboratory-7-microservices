package edu.eci.arep.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.model.Post;
import edu.eci.arep.service.PostService;
import edu.eci.arep.util.Auth0TokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PostHandler covering GET (public) and POST (protected) flows.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@ExtendWith(MockitoExtension.class)
class PostHandlerTest {

    @Mock
    private PostService postService;

    @Mock
    private Auth0TokenValidator tokenValidator;

    @Mock
    private DecodedJWT decodedJWT;

    private PostHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PostHandler(postService, tokenValidator, new ObjectMapper());
    }

    @Test
    void handleGet_isPublicAndReturns200() {
        when(postService.getAllPosts()).thenReturn(List.of(
                new Post("1", "Hello", "auth0|1", "alice", "2026-04-17T00:00:00Z")));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent().withHttpMethod("GET");

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).contains("Hello");
        verify(tokenValidator, never()).validate(any());
    }

    @Test
    void handlePost_withoutToken_returns401() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withHeaders(Map.of())
                .withBody("{\"content\":\"Hello\"}");

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(401);
    }

    @Test
    void handlePost_withValidToken_returns201() {
        when(tokenValidator.validate(any())).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("auth0|1");
        when(decodedJWT.getClaim("nickname")).thenReturn(mockClaim("alice"));
        when(postService.createPost("Hello world", "auth0|1", "alice"))
                .thenReturn(new Post("uuid-1", "Hello world", "auth0|1", "alice", "2026-04-17T00:00:00Z"));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withHeaders(Map.of("Authorization", "Bearer valid.token"))
                .withBody("{\"content\":\"Hello world\"}");

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getBody()).contains("Hello world");
    }

    @Test
    void handlePost_withBlankContent_returns400() {
        when(tokenValidator.validate(any())).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("auth0|1");
        when(decodedJWT.getClaim("nickname")).thenReturn(mockClaim("alice"));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withHeaders(Map.of("Authorization", "Bearer valid.token"))
                .withBody("{\"content\":\"\"}");

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(400);
    }

    private com.auth0.jwt.interfaces.Claim mockClaim(String value) {
        com.auth0.jwt.interfaces.Claim claim = mock(com.auth0.jwt.interfaces.Claim.class);
        when(claim.asString()).thenReturn(value);
        return claim;
    }
}
