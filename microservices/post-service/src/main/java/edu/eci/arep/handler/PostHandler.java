package edu.eci.arep.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arep.dto.PostRequest;
import edu.eci.arep.dto.PostResponse;
import edu.eci.arep.model.Post;
import edu.eci.arep.service.PostService;
import edu.eci.arep.service.PostServiceImpl;
import edu.eci.arep.util.Auth0TokenValidator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.Map;

/**
 * AWS Lambda handler for POST /api/posts and GET /api/posts.
 * GET is public; POST requires a valid Auth0 JWT.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Map<String, String> CORS_HEADERS = Map.of(
            "Access-Control-Allow-Origin", "*",
            "Content-Type", "application/json");

    private final PostService postService;
    private final Auth0TokenValidator tokenValidator;
    private final ObjectMapper objectMapper;

    /**
     * Default constructor used by the Lambda runtime.
     */
    public PostHandler() {
        this.postService = new PostServiceImpl(DynamoDbClient.create());
        this.tokenValidator = new Auth0TokenValidator(
                System.getenv("AUTH0_ISSUER_URI"),
                System.getenv("AUTH0_AUDIENCE"));
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor for unit testing with injected dependencies.
     *
     * @param postService    the post service to use
     * @param tokenValidator the JWT validator to use
     * @param objectMapper   the Jackson mapper to use
     */
    public PostHandler(PostService postService, Auth0TokenValidator tokenValidator, ObjectMapper objectMapper) {
        this.postService = postService;
        this.tokenValidator = tokenValidator;
        this.objectMapper = objectMapper;
    }

    /**
     * Routes GET requests to the public listing and POST requests to the protected creation endpoint.
     *
     * @param event   the API Gateway request event
     * @param context the Lambda execution context
     * @return the appropriate APIGatewayProxyResponseEvent
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String method = event.getHttpMethod();
            if ("GET".equalsIgnoreCase(method)) {
                return handleGetAll();
            }
            if ("POST".equalsIgnoreCase(method)) {
                return handleCreate(event);
            }
            return response(405, "{\"error\":\"Method not allowed\"}");
        } catch (Exception e) {
            context.getLogger().log("ERROR in PostHandler: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return response(500, "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent handleGetAll() throws Exception {
        List<PostResponse> posts = postService.getAllPosts().stream()
                .map(PostResponse::from)
                .toList();
        return response(200, objectMapper.writeValueAsString(posts));
    }

    private APIGatewayProxyResponseEvent handleCreate(APIGatewayProxyRequestEvent event) throws Exception {
        String authHeader = event.getHeaders() != null ? event.getHeaders().get("Authorization") : null;
        if (authHeader == null || authHeader.isBlank()) {
            return response(401, "{\"error\":\"Missing Authorization header\"}");
        }
        DecodedJWT jwt;
        try {
            jwt = tokenValidator.validate(authHeader);
        } catch (RuntimeException e) {
            return response(401, "{\"error\":\"" + e.getMessage() + "\"}");
        }

        PostRequest req = objectMapper.readValue(event.getBody(), PostRequest.class);
        if (req.getContent() == null || req.getContent().isBlank()) {
            return response(400, "{\"error\":\"Content must not be blank\"}");
        }
        if (req.getContent().length() > 140) {
            return response(400, "{\"error\":\"Content must not exceed 140 characters\"}");
        }

        String authorId = jwt.getSubject();
        String authorNickname = jwt.getClaim("nickname").asString();
        if (authorNickname == null || authorNickname.isBlank()) {
            authorNickname = jwt.getClaim("name").asString();
        }
        if (authorNickname == null || authorNickname.isBlank()) {
            authorNickname = authorId != null && authorId.contains("|") ? authorId.split("\\|")[0] + "-user" : "anon";
        }
        Post post = postService.createPost(req.getContent(), authorId, authorNickname);
        return response(201, objectMapper.writeValueAsString(PostResponse.from(post)));
    }

    private APIGatewayProxyResponseEvent response(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(CORS_HEADERS)
                .withBody(body);
    }
}
