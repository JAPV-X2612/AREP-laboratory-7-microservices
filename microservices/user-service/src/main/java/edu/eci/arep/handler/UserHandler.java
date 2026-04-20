package edu.eci.arep.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.dto.UserResponse;
import edu.eci.arep.model.AppUser;
import edu.eci.arep.service.UserService;
import edu.eci.arep.service.UserServiceImpl;
import edu.eci.arep.util.Auth0TokenValidator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

/**
 * AWS Lambda handler for GET /api/me. Validates the Auth0 JWT from the Authorization header,
 * resolves or auto-provisions the user in DynamoDB, and returns the user profile.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Map<String, String> CORS_HEADERS = Map.of(
            "Access-Control-Allow-Origin", "*",
            "Content-Type", "application/json");

    private final UserService userService;
    private final Auth0TokenValidator tokenValidator;
    private final ObjectMapper objectMapper;

    /**
     * Default constructor used by the Lambda runtime — reads config from environment variables.
     */
    public UserHandler() {
        this.userService = new UserServiceImpl(DynamoDbClient.create());
        this.tokenValidator = new Auth0TokenValidator(
                System.getenv("AUTH0_ISSUER_URI"),
                System.getenv("AUTH0_AUDIENCE"));
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor for unit testing with injected dependencies.
     *
     * @param userService    the user service to use
     * @param tokenValidator the JWT validator to use
     * @param objectMapper   the Jackson mapper to use
     */
    public UserHandler(UserService userService, Auth0TokenValidator tokenValidator, ObjectMapper objectMapper) {
        this.userService = userService;
        this.tokenValidator = tokenValidator;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles the API Gateway proxy event for GET /api/me.
     *
     * @param event   the API Gateway request event
     * @param context the Lambda execution context
     * @return 200 with UserResponse, 401 if JWT is missing or invalid, 500 on unexpected errors
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String authHeader = event.getHeaders() != null ? event.getHeaders().get("Authorization") : null;
            if (authHeader == null || authHeader.isBlank()) {
                return response(401, "{\"error\":\"Missing Authorization header\"}");
            }

            DecodedJWT jwt = tokenValidator.validate(authHeader);
            String auth0Id = jwt.getSubject();
            String email = jwt.getClaim("email").asString();
            String nickname = jwt.getClaim("nickname").asString();
            if (nickname == null || nickname.isBlank()) {
                nickname = jwt.getClaim("name").asString();
            }
            if (nickname == null || nickname.isBlank()) {
                nickname = auth0Id != null && auth0Id.contains("|") ? auth0Id.split("\\|")[0] + "-user" : "anon";
            }
            if (email == null || email.isBlank()) {
                email = "unknown@unknown";
            }

            AppUser user = userService.resolveUser(auth0Id, email, nickname);
            return response(200, objectMapper.writeValueAsString(UserResponse.from(user)));
        } catch (RuntimeException e) {
            return response(401, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return response(500, "{\"error\":\"Internal server error\"}");
        }
    }

    private APIGatewayProxyResponseEvent response(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(CORS_HEADERS)
                .withBody(body);
    }
}
