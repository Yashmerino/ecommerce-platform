package com.yashmerino.ecommerce.security;

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

import com.yashmerino.ecommerce.model.Role;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;
    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");
        testUser.setEmail("test@example.com");

        roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");
        roles.add(userRole);

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");
        roles.add(adminRole);

        testUser.setRoles(roles);
    }

    @Test
    void testLoadUserByUsernameUserExistsReturnsUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword123", result.getPassword());
        assertEquals(2, result.getAuthorities().size());
        
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameUserDoesNotExistThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername("nonexistent"));
        
        assertEquals("Username not found.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsernameUserWithSingleRoleReturnsCorrectAuthorities() {
        Role singleRole = new Role();
        singleRole.setId(1L);
        singleRole.setName("USER");
        Set<Role> singleRoleSet = new HashSet<>();
        singleRoleSet.add(singleRole);
        testUser.setRoles(singleRoleSet);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");
        assertNotNull(result);
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameUserWithNoRolesReturnsEmptyAuthorities() {
        testUser.setRoles(new HashSet<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");
        assertNotNull(result);
        assertEquals(0, result.getAuthorities().size());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameUserWithCustomRoleMapsCorrectly() {
        Role sellerRole = new Role();
        sellerRole.setId(3L);
        sellerRole.setName("SELLER");
        Set<Role> sellerRoleSet = new HashSet<>();
        sellerRoleSet.add(sellerRole);
        testUser.setRoles(sellerRoleSet);

        when(userRepository.findByUsername("seller")).thenReturn(Optional.of(testUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("seller");
        assertNotNull(result);
        assertEquals(1, result.getAuthorities().size());
        
        boolean hasSellerAuthority = result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("SELLER"));
        assertTrue(hasSellerAuthority);
        
        verify(userRepository, times(1)).findByUsername("seller");
    }
}
