package edu.eci.arep.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

/**
 * Persistent entity representing an authenticated application user.
 * Created on first login from Auth0 JWT claims.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @Column(name = "auth0_id", nullable = false, unique = true)
    private String auth0Id;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @Column
    private String nickname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
    }

    /**
     * Default no-arg constructor required by JPA.
     */
    protected AppUser() {}

    /**
     * Creates a new AppUser with mandatory fields.
     *
     * @param auth0Id  unique subject identifier from Auth0 JWT
     * @param email    user's email address
     * @param nickname display name derived from Auth0 profile
     */
    public AppUser(String auth0Id, String email, String nickname) {
        this.auth0Id = auth0Id;
        this.email = email;
        this.nickname = nickname;
    }

    public String getAuth0Id() { return auth0Id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public Instant getCreatedAt() { return createdAt; }

    public void setNickname(String nickname) { this.nickname = nickname; }
}
