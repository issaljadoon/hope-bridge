
package com.hopeBridgeAi.homeBridgeAi_backend.config;

import com.hopeBridgeAi.homeBridgeAi_backend.entity.User;
import com.hopeBridgeAi.homeBridgeAi_backend.repository.UserRepository;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final UserRepository userRepository;

    public CurrentUserJwtValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {

        OAuth2Error invalidToken = new OAuth2Error(
                "invalid_token",
                "Token is no longer valid for this account.",
                null
        );

        String email = jwt.getSubject();
        Object userIdClaim = jwt.getClaim("userId");
        Object tokenVersionClaim = jwt.getClaim("tokenVersion");
        String tokenRole = jwt.getClaimAsString("role");

        if (email == null || email.isBlank()
                || !(userIdClaim instanceof Number)
                || !(tokenVersionClaim instanceof Number)
                || tokenRole == null) {
            return OAuth2TokenValidatorResult.failure(invalidToken);
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return OAuth2TokenValidatorResult.failure(invalidToken);
        }

        User user = optionalUser.get();

        if (!user.isEnabled()
                || user.getRole() == null
                || !user.getId().equals(
                ((Number) userIdClaim).longValue()
        )
                || !user.getRole().name().equals(tokenRole)
                || user.getTokenVersion() !=
                ((Number) tokenVersionClaim).longValue()) {

            return OAuth2TokenValidatorResult.failure(invalidToken);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
