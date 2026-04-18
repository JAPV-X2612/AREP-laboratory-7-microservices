package edu.eci.arep.service;

import edu.eci.arep.entity.AppUser;
import edu.eci.arep.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of UserService using JPA persistence.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;

    /**
     * @param userRepository JPA repository for AppUser entities
     */
    public UserServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AppUser resolveUser(String auth0Id, String email, String nickname) {
        return userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> userRepository.save(new AppUser(auth0Id, email, nickname)));
    }
}
