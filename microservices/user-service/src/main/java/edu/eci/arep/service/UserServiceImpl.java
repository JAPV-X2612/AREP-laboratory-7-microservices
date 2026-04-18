package edu.eci.arep.service;

import edu.eci.arep.model.AppUser;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.Map;

/**
 * DynamoDB-backed implementation of UserService for the user-service Lambda.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class UserServiceImpl implements UserService {

    private static final String TABLE_NAME = System.getenv("USERS_TABLE");

    private final DynamoDbClient dynamoDb;

    /**
     * @param dynamoDb the AWS SDK DynamoDB client
     */
    public UserServiceImpl(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppUser resolveUser(String auth0Id, String email, String nickname) {
        GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("auth0Id", AttributeValue.fromS(auth0Id)))
                .build());

        if (response.hasItem()) {
            Map<String, AttributeValue> item = response.item();
            return new AppUser(
                    auth0Id,
                    item.get("email").s(),
                    item.get("nickname").s(),
                    item.get("createdAt").s());
        }

        String createdAt = Instant.now().toString();
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(Map.of(
                        "auth0Id", AttributeValue.fromS(auth0Id),
                        "email", AttributeValue.fromS(email),
                        "nickname", AttributeValue.fromS(nickname),
                        "createdAt", AttributeValue.fromS(createdAt)))
                .build());

        return new AppUser(auth0Id, email, nickname, createdAt);
    }
}
