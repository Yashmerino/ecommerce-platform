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

import com.yashmerino.ecommerce.exceptions.RefreshTokenExpiredException;
import com.yashmerino.ecommerce.exceptions.RefreshTokenRevokedException;
import com.yashmerino.ecommerce.exceptions.UserDoesntExistException;
import com.yashmerino.ecommerce.model.RefreshToken;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.repositories.RefreshTokenRepository;
import com.yashmerino.ecommerce.repositories.UserRepository;
import com.yashmerino.ecommerce.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User testUser;
    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testToken = new RefreshToken();
        testToken.setId(1L);
        testToken.setToken("valid-refresh-token");
        testToken.setUser(testUser);
        testToken.setExpiryDate(Instant.now().plusMillis(86400000));
        testToken.setCreatedAt(Instant.now());
        testToken.setRevoked(false);
    }

    @Test
    void testCreateRefreshTokenCreatesNewToken() {
        String generatedToken = "new-refresh-token";
        when(jwtProvider.generateRefreshToken()).thenReturn(generatedToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(2L);
            return token;
        });

        RefreshToken result = refreshTokenService.createRefreshToken(testUser);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(generatedToken, result.getToken());
        assertFalse(result.isRevoked());
        assertNotNull(result.getExpiryDate());
        assertNotNull(result.getCreatedAt());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        
        RefreshToken savedToken = tokenCaptor.getValue();
        assertEquals(testUser, savedToken.getUser());
        assertEquals(generatedToken, savedToken.getToken());
        assertFalse(savedToken.isRevoked());
    }

    @Test
    void testVerifyRefreshTokenValidTokenReturnsToken() {
        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(testToken));

        RefreshToken result = refreshTokenService.verifyRefreshToken("valid-refresh-token");
        assertNotNull(result);
        assertEquals("valid-refresh-token", result.getToken());
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository, times(1)).findByToken("valid-refresh-token");
    }

    @Test
    void testVerifyRefreshTokenTokenNotFoundThrowsException() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> refreshTokenService.verifyRefreshToken("invalid-token"));
        
        assertEquals("refresh_token_not_found", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findByToken("invalid-token");
    }

    @Test
    void testVerifyRefreshTokenRevokedTokenThrowsRefreshTokenRevokedException() {
        testToken.setRevoked(true);
        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(testToken));

        RefreshTokenRevokedException exception = assertThrows(RefreshTokenRevokedException.class,
            () -> refreshTokenService.verifyRefreshToken("revoked-token"));
        
        assertEquals("refresh_token_revoked", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findByToken("revoked-token");
    }

    @Test
    void testVerifyRefreshTokenExpiredTokenThrowsRefreshTokenExpiredException() {
        testToken.setExpiryDate(Instant.now().minusMillis(1000));
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(testToken));

        RefreshTokenExpiredException exception = assertThrows(RefreshTokenExpiredException.class,
            () -> refreshTokenService.verifyRefreshToken("expired-token"));
        
        assertEquals("refresh_token_expired", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findByToken("expired-token");
        verify(refreshTokenRepository, times(1)).delete(testToken);
    }

    @Test
    void testRevokeUserTokensUserExistsRevokesTokens() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        doNothing().when(refreshTokenRepository).revokeAllUserTokens(1L);

        refreshTokenService.revokeUserTokens("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(1L);
    }

    @Test
    void testRevokeUserTokensUserDoesNotExistThrowsUserDoesntExistException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UserDoesntExistException exception = assertThrows(UserDoesntExistException.class,
            () -> refreshTokenService.revokeUserTokens("nonexistent"));
        
        assertEquals("username_not_found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(refreshTokenRepository, never()).revokeAllUserTokens(any());
    }

    @Test
    void testDeleteRefreshTokenTokenExistsDeletesToken() {
        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(testToken));
        doNothing().when(refreshTokenRepository).delete(testToken);

        refreshTokenService.deleteRefreshToken("valid-refresh-token");
        verify(refreshTokenRepository, times(1)).findByToken("valid-refresh-token");
        verify(refreshTokenRepository, times(1)).delete(testToken);
    }

    @Test
    void testDeleteRefreshTokenTokenDoesNotExistDoesNothing() {
        when(refreshTokenRepository.findByToken("nonexistent-token")).thenReturn(Optional.empty());

        refreshTokenService.deleteRefreshToken("nonexistent-token");
        verify(refreshTokenRepository, times(1)).findByToken("nonexistent-token");
        verify(refreshTokenRepository, never()).delete(any());
    }
}
