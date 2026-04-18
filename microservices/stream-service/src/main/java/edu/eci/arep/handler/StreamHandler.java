package edu.eci.arep.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.dto.StreamResponse;
import edu.eci.arep.service.StreamService;
import edu.eci.arep.service.StreamServiceImpl;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

/**
 * AWS Lambda handler for GET /api/stream (public endpoint).
 * Returns the global post stream ordered newest first with no authentication required.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class StreamHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Map<String, String> CORS_HEADERS = Map.of(
            "Access-Control-Allow-Origin", "*",
            "Content-Type", "application/json");

    private final StreamService streamService;
    private final ObjectMapper objectMapper;

    /**
     * Default constructor used by the Lambda runtime.
     */
    public StreamHandler() {
        this.streamService = new StreamServiceImpl(DynamoDbClient.create());
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor for unit testing with injected dependencies.
     *
     * @param streamService the stream service to use
     * @param objectMapper  the Jackson mapper to use
     */
    public StreamHandler(StreamService streamService, ObjectMapper objectMapper) {
        this.streamService = streamService;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles the API Gateway proxy event for GET /api/stream.
     *
     * @param event   the API Gateway request event
     * @param context the Lambda execution context
     * @return 200 with StreamResponse JSON body, or 500 on unexpected errors
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            StreamResponse stream = streamService.getStream();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(CORS_HEADERS)
                    .withBody(objectMapper.writeValueAsString(stream));
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(CORS_HEADERS)
                    .withBody("{\"error\":\"Internal server error\"}");
        }
    }
}
