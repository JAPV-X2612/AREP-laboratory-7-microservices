package edu.eci.arep.exception;

/**
 * Thrown when no AppUser record exists for the given Auth0 subject identifier.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * @param auth0Id the Auth0 subject that could not be found
     */
    public UserNotFoundException(String auth0Id) {
        super("User not found for Auth0 ID: " + auth0Id);
    }
}
