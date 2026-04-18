package edu.eci.arep.model;

/**
 * Domain model representing an application user in the user-service.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class AppUser {

    private String auth0Id;
    private String email;
    private String nickname;
    private String createdAt;

    public AppUser() {}

    /**
     * @param auth0Id   Auth0 subject identifier
     * @param email     user email address
     * @param nickname  display name
     * @param createdAt ISO-8601 creation timestamp
     */
    public AppUser(String auth0Id, String email, String nickname, String createdAt) {
        this.auth0Id = auth0Id;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public String getAuth0Id() { return auth0Id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getCreatedAt() { return createdAt; }

    public void setAuth0Id(String auth0Id) { this.auth0Id = auth0Id; }
    public void setEmail(String email) { this.email = email; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
