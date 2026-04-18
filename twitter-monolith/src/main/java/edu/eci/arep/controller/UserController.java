package edu.eci.arep.controller;

import edu.eci.arep.dto.UserResponse;
import edu.eci.arep.entity.AppUser;
import edu.eci.arep.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the authenticated user's profile at GET /api/me.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "Authenticated user profile operations")
public class UserController {

    private final UserService userService;

    /**
     * @param userService service for resolving and persisting user profiles
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the profile of the currently authenticated user, auto-provisioning
     * a local record on first access.
     *
     * @param jwt the validated JWT injected by Spring Security
     * @return 200 with the user's profile as UserResponse
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the authenticated user's profile. Requires a valid Auth0 JWT.")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String nickname = jwt.getClaimAsString("nickname");
        AppUser user = userService.resolveUser(auth0Id, email, nickname);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
