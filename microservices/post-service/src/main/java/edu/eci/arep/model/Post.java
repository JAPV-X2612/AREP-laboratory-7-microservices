package edu.eci.arep.model;

/**
 * Domain model representing a post in the post-service.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class Post {

    private String id;
    private String content;
    private String authorId;
    private String authorNickname;
    private String createdAt;

    public Post() {}

    /**
     * @param id              unique post identifier (UUID)
     * @param content         post body, max 140 characters
     * @param authorId        Auth0 subject of the author
     * @param authorNickname  display name of the author
     * @param createdAt       ISO-8601 creation timestamp
     */
    public Post(String id, String content, String authorId, String authorNickname, String createdAt) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorId() { return authorId; }
    public String getAuthorNickname() { return authorNickname; }
    public String getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
