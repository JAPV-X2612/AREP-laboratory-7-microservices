package edu.eci.arep.dto;

/**
 * Inbound DTO carrying the content for a new post in the post-service.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class PostRequest {

    private String content;

    public PostRequest() {}

    public PostRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
