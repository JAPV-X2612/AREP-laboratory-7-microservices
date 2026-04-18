package edu.eci.arep.dto;

import edu.eci.arep.model.AppUser;

/**
 * Outbound DTO representing the authenticated user's profile in the user-service.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class UserResponse {

    private String auth0Id;
    private String email;
    private String nickname;
    private String createdAt;

    /**
     * Factory method that maps an AppUser model to a UserResponse DTO.
     *
     * @param user the domain model to map
     * @return a populated UserResponse
     */
    public static UserResponse from(AppUser user) {
        UserResponse dto = new UserResponse();
        dto.auth0Id = user.getAuth0Id();
        dto.email = user.getEmail();
        dto.nickname = user.getNickname();
        dto.createdAt = user.getCreatedAt();
        return dto;
    }

    public String getAuth0Id() { return auth0Id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getCreatedAt() { return createdAt; }
}
