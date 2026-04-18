package edu.eci.arep.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.model.AppUser;
import edu.eci.arep.service.UserService;
import edu.eci.arep.util.Auth0TokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserHandler covering JWT validation flow and DynamoDB delegation.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private Auth0TokenValidator tokenValidator;

    @Mock
    private DecodedJWT decodedJWT;

    private UserHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UserHandler(userService, tokenValidator, new ObjectMapper());
    }

    @Test
    void handleRequest_withValidToken_returns200() {
        when(tokenValidator.validate(any())).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("auth0|1");
        when(decodedJWT.getClaim("email")).thenReturn(mockClaim("user@test.com"));
        when(decodedJWT.getClaim("nickname")).thenReturn(mockClaim("testuser"));
        when(userService.resolveUser("auth0|1", "user@test.com", "testuser"))
                .thenReturn(new AppUser("auth0|1", "user@test.com", "testuser", "2026-04-17T00:00:00Z"));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHeaders(Map.of("Authorization", "Bearer valid.token.here"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).contains("testuser");
    }

    @Test
    void handleRequest_withMissingAuthHeader_returns401() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent().withHeaders(Map.of());

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(401);
        verify(tokenValidator, never()).validate(any());
    }

    @Test
    void handleRequest_withInvalidToken_returns401() {
        when(tokenValidator.validate(any())).thenThrow(new RuntimeException("Invalid token"));

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent()
                .withHeaders(Map.of("Authorization", "Bearer bad.token"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, null);

        assertThat(response.getStatusCode()).isEqualTo(401);
    }

    private com.auth0.jwt.interfaces.Claim mockClaim(String value) {
        com.auth0.jwt.interfaces.Claim claim = mock(com.auth0.jwt.interfaces.Claim.class);
        when(claim.asString()).thenReturn(value);
        return claim;
    }
}
