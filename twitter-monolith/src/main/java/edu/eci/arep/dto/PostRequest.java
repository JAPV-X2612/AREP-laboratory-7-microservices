package edu.eci.arep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Inbound DTO carrying the content of a new post.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class PostRequest {

    @NotBlank(message = "Content must not be blank")
    @Size(max = 140, message = "Content must not exceed 140 characters")
    private String content;

    public PostRequest() {}

    public PostRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
