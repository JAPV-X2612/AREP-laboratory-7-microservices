package edu.eci.arep.service;

import edu.eci.arep.model.AppUser;

/**
 * Contract for user profile resolution in the user-service Lambda.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface UserService {

    /**
     * Returns the stored user matching the given Auth0 subject, creating a new
     * DynamoDB record on first login if none exists.
     *
     * @param auth0Id  Auth0 subject claim
     * @param email    email from the JWT
     * @param nickname nickname from the JWT
     * @return the resolved or newly created AppUser
     */
    AppUser resolveUser(String auth0Id, String email, String nickname);
}
