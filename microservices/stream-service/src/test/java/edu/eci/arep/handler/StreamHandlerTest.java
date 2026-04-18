package edu.eci.arep.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.dto.StreamResponse;
import edu.eci.arep.service.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for StreamHandler verifying public access and response serialization.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@ExtendWith(MockitoExtension.class)
class StreamHandlerTest {

    @Mock
    private StreamService streamService;

    private StreamHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StreamHandler(streamService, new ObjectMapper());
    }

    @Test
    void handleRequest_returnsPublicStream() {
        StreamResponse stream = new StreamResponse(List.of(
                new StreamResponse.PostItem("1", "Hello", "alice", "2026-04-17T00:00:00Z")));
        when(streamService.getStream()).thenReturn(stream);

        APIGatewayProxyResponseEvent response = handler.handleRequest(
                new APIGatewayProxyRequestEvent(), null);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).contains("Hello");
        assertThat(response.getBody()).contains("alice");
    }

    @Test
    void handleRequest_withEmptyStream_returns200WithEmptyList() {
        when(streamService.getStream()).thenReturn(new StreamResponse(List.of()));

        APIGatewayProxyResponseEvent response = handler.handleRequest(
                new APIGatewayProxyRequestEvent(), null);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"posts\":[]");
    }
}
