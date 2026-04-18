package edu.eci.arep.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Persistent entity representing a public post of up to 140 characters.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 140)
    @Column(nullable = false, length = 140)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
    }

    /**
     * Default no-arg constructor required by JPA.
     */
    protected Post() {}

    /**
     * Creates a new Post with its content and author.
     *
     * @param content post body, max 140 characters
     * @param author  authenticated user who authored the post
     */
    public Post(String content, AppUser author) {
        this.content = content;
        this.author = author;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public AppUser getAuthor() { return author; }
    public Instant getCreatedAt() { return createdAt; }
}
