
package com.hopeBridgeAi.homeBridgeAi_backend.service;

import com.hopeBridgeAi.homeBridgeAi_backend.entity.Role;
import com.hopeBridgeAi.homeBridgeAi_backend.entity.User;
import com.hopeBridgeAi.homeBridgeAi_backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Service
public class UserAccountService {

    private final UserRepository userRepository;

    public UserAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void changeRole(Long userId, Role newRole) {

        if (newRole == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role is required"
            );
        }

        preventSelfModification(userId);

        User user = findUser(userId);

        if (user.getRole() == newRole) {
            return;
        }

        user.setRole(newRole);
        incrementTokenVersion(user);
    }

    @Transactional
    public void changeEnabledStatus(Long userId, boolean enabled) {
        preventSelfModification(userId);
        User user = findUser(userId);

        if (user.isEnabled() == enabled) {
            return;
        }

        user.setEnabled(enabled);
        incrementTokenVersion(user);
    }

    private User findUser(Long userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with ID: " + userId
                ));
    }

    private void incrementTokenVersion(User user) {

        if (user.getTokenVersion() == Long.MAX_VALUE) {
            throw new IllegalStateException(
                    "Token version limit reached"
            );
        }

        user.setTokenVersion(user.getTokenVersion() + 1);
    }
    private void preventSelfModification(Long targetUserId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {

            Object userIdClaim = jwt.getClaim("userId");

            if (userIdClaim instanceof Number &&
                    ((Number) userIdClaim).longValue() == targetUserId) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot modify your own role or account status"
                );
            }
        }
    }
}
