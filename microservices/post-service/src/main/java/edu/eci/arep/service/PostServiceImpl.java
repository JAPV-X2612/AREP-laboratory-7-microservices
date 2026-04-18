package edu.eci.arep.service;

import edu.eci.arep.model.Post;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DynamoDB-backed implementation of PostService for the post-service Lambda.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class PostServiceImpl implements PostService {

    private static final String TABLE_NAME = System.getenv("POSTS_TABLE");

    private final DynamoDbClient dynamoDb;

    /**
     * @param dynamoDb the AWS SDK DynamoDB client
     */
    public PostServiceImpl(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post createPost(String content, String authorId, String authorNickname) {
        String id = UUID.randomUUID().toString();
        String createdAt = Instant.now().toString();
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(Map.of(
                        "id", AttributeValue.fromS(id),
                        "content", AttributeValue.fromS(content),
                        "authorId", AttributeValue.fromS(authorId),
                        "authorNickname", AttributeValue.fromS(authorNickname),
                        "createdAt", AttributeValue.fromS(createdAt)))
                .build());
        return new Post(id, content, authorId, authorNickname, createdAt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> getAllPosts() {
        ScanResponse scan = dynamoDb.scan(ScanRequest.builder().tableName(TABLE_NAME).build());
        return scan.items().stream()
                .map(item -> new Post(
                        item.get("id").s(),
                        item.get("content").s(),
                        item.get("authorId").s(),
                        item.get("authorNickname").s(),
                        item.get("createdAt").s()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
