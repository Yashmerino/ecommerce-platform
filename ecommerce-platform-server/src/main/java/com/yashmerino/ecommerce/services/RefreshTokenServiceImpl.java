package com.yashmerino.ecommerce.services;

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + MIT License
 +
 + Copyright (c) 2023 Artiom Bozieac
 +
 + Permission is hereby granted, free of charge, to any person obtaining a copy
 + of this software and associated documentation files (the "Software"), to deal
 + in the Software without restriction, including without limitation the rights
 + to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 + copies of the Software, and to permit persons to whom the Software is
 + furnished to do so, subject to the following conditions:
 +
 + The above copyright notice and this permission notice shall be included in all
 + copies or substantial portions of the Software.
 +
 + THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 + IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 + FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 + AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 + LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 + OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 + SOFTWARE.
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

import com.yashmerino.ecommerce.exceptions.UserDoesntExistException;
import com.yashmerino.ecommerce.model.RefreshToken;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.repositories.RefreshTokenRepository;
import com.yashmerino.ecommerce.repositories.UserRepository;
import com.yashmerino.ecommerce.security.JwtProvider;
import com.yashmerino.ecommerce.services.interfaces.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.yashmerino.ecommerce.security.SecurityConstants.REFRESH_TOKEN_EXPIRATION;

/**
 * Implementation for {@link RefreshTokenService}
 */
@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    /**
     * Refresh token repository.
     */
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * User repository.
     */
    private final UserRepository userRepository;

    /**
     * JWT Provider.
     */
    private final JwtProvider jwtProvider;

    /**
     * Creates a new refresh token for the user.
     *
     * @param user the user.
     * @return RefreshToken entity.
     */
    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(jwtProvider.generateRefreshToken());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validates a refresh token.
     *
     * @param token the refresh token string.
     * @return RefreshToken if valid.
     * @throws RuntimeException if token is invalid, expired or revoked.
     */
    @Override
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("refresh_token_not_found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("refresh_token_revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("refresh_token_expired");
        }

        return refreshToken;
    }

    /**
     * Revokes all refresh tokens for a user.
     *
     * @param username the username.
     */
    @Override
    @Transactional
    public void revokeUserTokens(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserDoesntExistException("username_not_found"));

        refreshTokenRepository.revokeAllUserTokens(user.getId());
    }

    /**
     * Deletes a specific refresh token.
     *
     * @param token the refresh token string.
     */
    @Override
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}
