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

import com.yashmerino.ecommerce.exceptions.CouldntUploadPhotoException;
import com.yashmerino.ecommerce.model.Role;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.model.dto.auth.UserDTO;
import com.yashmerino.ecommerce.model.dto.auth.UserInfoDTO;
import com.yashmerino.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testUser.setRoles(roles);
    }

    @Test
    void testGetByIdUserExistsReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getById(1L);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdUserDoesNotExistThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testGetByUsernameUserExistsReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.getByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetByUsernameUserDoesNotExistThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> userService.getByUsername("nonexistent"));
        assertEquals("username_not_found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testGetUserInfoUserExistsReturnsUserInfoDTO() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserInfoDTO result = userService.getUserInfo("testuser");
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().stream().anyMatch(r -> "USER".equals(r.getName())));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserInfoUserDoesNotExistThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> userService.getUserInfo("nonexistent"));
        assertEquals("username_not_found", exception.getMessage());
    }

    @Test
    void testUpdatePhotoValidUserSuccess() throws IOException {
        byte[] photoBytes = "test photo".getBytes();
        MultipartFile photo = mock(MultipartFile.class);
        when(photo.getBytes()).thenReturn(photoBytes);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userService.updatePhoto("testuser", photo);

        assertArrayEquals(photoBytes, testUser.getPhoto());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdatePhotoUnauthorizedUserThrowsAccessDeniedException() throws IOException {
        MultipartFile photo = mock(MultipartFile.class);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(authentication.getName()).thenReturn("differentuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, () -> userService.updatePhoto("testuser", photo));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdatePhotoIOExceptionThrownThrowsCouldntUploadPhotoException() throws IOException {
        MultipartFile photo = mock(MultipartFile.class);
        when(photo.getBytes()).thenThrow(new IOException("File error"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        CouldntUploadPhotoException exception = assertThrows(CouldntUploadPhotoException.class, 
            () -> userService.updatePhoto("testuser", photo));
        assertEquals("user_photo_not_uploaded", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserValidUserSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newemail@example.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userService.updateUser("testuser", userDTO);

        assertEquals("newemail@example.com", testUser.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUserUnauthorizedUserThrowsAccessDeniedException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newemail@example.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(authentication.getName()).thenReturn("differentuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, () -> userService.updateUser("testuser", userDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserUserNotFoundThrowsEntityNotFoundException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newemail@example.com");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser("nonexistent", userDTO));
        verify(userRepository, never()).save(any());
    }
}
