package com.yashmerino.ecommerce.services.interfaces;

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

import com.yashmerino.ecommerce.model.RefreshToken;
import com.yashmerino.ecommerce.model.User;

/**
 * Interface for refresh token service.
 */
public interface RefreshTokenService {

    /**
     * Creates a new refresh token for the user.
     *
     * @param user the user.
     * @return RefreshToken entity.
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Validates a refresh token.
     *
     * @param token the refresh token string.
     * @return RefreshToken if valid.
     * @throws RuntimeException if token is invalid, expired or revoked.
     */
    RefreshToken verifyRefreshToken(String token);

    /**
     * Revokes all refresh tokens for a user.
     *
     * @param username the username.
     */
    void revokeUserTokens(String username);

    /**
     * Deletes a specific refresh token.
     *
     * @param token the refresh token string.
     */
    void deleteRefreshToken(String token);
}
