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

import com.yashmerino.ecommerce.utils.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Endpoints for Swagger UI.
     */
    private static final String[] SWAGGER_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    /**
     * Endpoints for Actuator.
     */
    private static final String[] ACTUATOR_WHITELIST = {
        "/actuator/**",
        "/actuator/health",
        "/actuator/info"
    };

    /**
     * Regex for all the endpoints related to authentication/authorization.
     */
    private static final String AUTH_ALL_ENDPOINTS = "/api/auth/**";

    /**
     * Regex for all the endpoints related to products.
     */
    private static final String PRODUCTS_ALL_ENDPOINTS = "/api/product/**";

    /**
     * Regex for all the endpoints related to cart items.
     */
    private static final String CART_ITEMS_ALL_ENDPOINTS = "/api/cartItem/**";

    /**
     * Regex for all the endpoints related to categories.
     */
    private static final String CATEGORIES_ALL_ENDPOINTS = "/api/categories/**";

    /**
     * Regex for all the endpoints related to orders.
     */
    private static final String ORDERS_ALL_ENDPOINTS = "/api/order/**";

    /**
     * Regex for all the endpoints related to payments.
     */
    private static final String PAYMENTS_ALL_ENDPOINTS = "/api/payment/**";

    /**
     * Regex for all the endpoints related to users.
     */
    private static final String USERS_ALL_ENDPOINTS = "/api/user/**";

    /**
     * Regex for all the endpoints related to carts.
     */
    private static final String CARTS_ALL_ENDPOINTS = "/api/cart/**";

    /**
     * Jwt Auth Entry Point to handle exceptions.
     */
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    /**
     * JWT Token generator.
     */
    private final JwtProvider tokenGenerator;

    /**
     * Custom user details service.
     */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Constructor.
     *
     * @param jwtAuthEntryPoint        is the auth entry point.
     * @param tokenGenerator           is the token generator.
     * @param customUserDetailsService is the service that deals with user's details.
     */
    public SecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint, JwtProvider tokenGenerator, CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.tokenGenerator = tokenGenerator;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Security filter.
     *
     * @param http is the Http Security config object.
     * @return Security Filter Chain.
     * @throws Exception if certain settings couldn't be changed.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                .disable()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, AUTH_ALL_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, AUTH_ALL_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, USERS_ALL_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PRODUCTS_ALL_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, CATEGORIES_ALL_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, USERS_ALL_ENDPOINTS).hasAnyAuthority(Role.USER.name(), Role.SELLER.name())
                        .requestMatchers(HttpMethod.PUT, USERS_ALL_ENDPOINTS).hasAnyAuthority(Role.USER.name(), Role.SELLER.name())
                        .requestMatchers(CART_ITEMS_ALL_ENDPOINTS).hasAnyAuthority(Role.SELLER.name(), Role.USER.name())
                        .requestMatchers(ORDERS_ALL_ENDPOINTS).hasAnyAuthority(Role.USER.name())
                        .requestMatchers(PAYMENTS_ALL_ENDPOINTS).hasAnyAuthority(Role.USER.name())
                        .requestMatchers(CARTS_ALL_ENDPOINTS).hasAnyAuthority(Role.USER.name())
                        .requestMatchers(HttpMethod.POST, PRODUCTS_ALL_ENDPOINTS).hasAuthority(Role.SELLER.name())
                        .requestMatchers(HttpMethod.PUT, PRODUCTS_ALL_ENDPOINTS).hasAuthority(Role.SELLER.name())
                        .requestMatchers(HttpMethod.DELETE, PRODUCTS_ALL_ENDPOINTS).hasAuthority(Role.SELLER.name())
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(ACTUATOR_WHITELIST).permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Gives the authentication manager.
     *
     * @param authenticationConfiguration is the auth configuration.
     * @return authentication manager.
     * @throws Exception if manager couldn't be obtained.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Generates an BCrypt password encoder.
     *
     * @return an BCrypt password encoder.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthenticationFilter() {
        return new JwtAuthFilter(tokenGenerator, customUserDetailsService);
    }
}
