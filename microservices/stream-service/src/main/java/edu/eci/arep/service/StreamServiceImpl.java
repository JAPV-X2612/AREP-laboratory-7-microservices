package edu.eci.arep.service;

import edu.eci.arep.dto.StreamResponse;
import edu.eci.arep.dto.StreamResponse.PostItem;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DynamoDB-backed implementation of StreamService for the stream-service Lambda.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class StreamServiceImpl implements StreamService {

    private static final String TABLE_NAME = System.getenv("POSTS_TABLE");

    private final DynamoDbClient dynamoDb;

    /**
     * @param dynamoDb the AWS SDK DynamoDB client
     */
    public StreamServiceImpl(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamResponse getStream() {
        ScanResponse scan = dynamoDb.scan(ScanRequest.builder().tableName(TABLE_NAME).build());
        List<PostItem> items = scan.items().stream()
                .map(item -> new PostItem(
                        item.get("id").s(),
                        item.get("content").s(),
                        item.get("authorNickname").s(),
                        item.get("createdAt").s()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
        return new StreamResponse(items);
    }
}
