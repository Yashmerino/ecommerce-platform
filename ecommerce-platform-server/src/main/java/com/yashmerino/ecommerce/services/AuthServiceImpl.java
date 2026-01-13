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
import com.yashmerino.ecommerce.exceptions.UsernameAlreadyTakenException;
import com.yashmerino.ecommerce.kafka.NotificationEventProducer;
import com.yashmerino.ecommerce.model.Cart;
import com.yashmerino.ecommerce.model.Role;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.model.dto.auth.LoginDTO;
import com.yashmerino.ecommerce.model.dto.auth.RegisterDTO;
import com.yashmerino.ecommerce.repositories.RoleRepository;
import com.yashmerino.ecommerce.repositories.UserRepository;
import com.yashmerino.ecommerce.security.JwtProvider;
import com.yashmerino.ecommerce.services.interfaces.AuthService;
import com.yashmerino.ecommerce.services.interfaces.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Implementation for {@link AuthService}
 */
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * Authentication manager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Users' repository.
     */
    private final UserRepository userRepository;

    /**
     * Roles' repository.
     */
    private final RoleRepository roleRepository;

    /**
     * Password encoder.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * JWT Token generator.
     */
    private final JwtProvider jwtProvider;

    /**
     * Cart service.
     */
    private final CartService cartService;

    /**
     * Kafka Notification producer.
     */
    private final NotificationEventProducer notificationEventProducer;

    /**
     * Registers the user.
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) { // NOSONAR - The user repository cannot be null.
            throw new UsernameAlreadyTakenException("username_taken");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        Optional<Role> roleOptional = roleRepository.findByName(registerDTO.getRole().name());

        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            user.setRoles(new HashSet<>(List.of(role)));
        } else {
            throw new EntityNotFoundException("role_not_found");
        }

        Cart cart = new Cart();
        cartService.save(cart);
        userRepository.save(user);

        user.setCart(cart);
        userRepository.save(user);

        cart.setUser(user);
        cartService.save(cart);

        notificationEventProducer.sendWelcomeNotificationRequested(user.getEmail());
    }

    /**
     * Logins the user.
     *
     * @param loginDTO is the login DTO.
     * @return JWT Token.
     */
    @Override
    public String login(LoginDTO loginDTO) {
        if (!userRepository.existsByUsername(loginDTO.getUsername())) {
            throw new UserDoesntExistException("username_not_found");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }
}
