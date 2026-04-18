package edu.eci.arep.dto;

import edu.eci.arep.entity.Post;

import java.time.Instant;

/**
 * Outbound DTO representing a post returned to the client.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class PostResponse {

    private Long id;
    private String content;
    private String authorNickname;
    private Instant createdAt;

    /**
     * Factory method that maps a Post entity to a PostResponse DTO.
     *
     * @param post the entity to map
     * @return a populated PostResponse
     */
    public static PostResponse from(Post post) {
        PostResponse dto = new PostResponse();
        dto.id = post.getId();
        dto.content = post.getContent();
        dto.authorNickname = post.getAuthor().getNickname();
        dto.createdAt = post.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorNickname() { return authorNickname; }
    public Instant getCreatedAt() { return createdAt; }
}
