package edu.eci.arep.repository;

import edu.eci.arep.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for AppUser entities.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public interface AppUserRepository extends JpaRepository<AppUser, String> {

    /**
     * Finds a user by their Auth0 subject identifier.
     *
     * @param auth0Id the Auth0 subject claim value
     * @return an Optional containing the user if found
     */
    Optional<AppUser> findByAuth0Id(String auth0Id);
}
