package edu.eci.arep.dto;

import edu.eci.arep.model.Post;

/**
 * Outbound DTO representing a post returned by the post-service.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class PostResponse {

    private String id;
    private String content;
    private String authorNickname;
    private String createdAt;

    /**
     * Factory method that maps a Post model to a PostResponse DTO.
     *
     * @param post the domain model to map
     * @return a populated PostResponse
     */
    public static PostResponse from(Post post) {
        PostResponse dto = new PostResponse();
        dto.id = post.getId();
        dto.content = post.getContent();
        dto.authorNickname = post.getAuthorNickname();
        dto.createdAt = post.getCreatedAt();
        return dto;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorNickname() { return authorNickname; }
    public String getCreatedAt() { return createdAt; }
}
