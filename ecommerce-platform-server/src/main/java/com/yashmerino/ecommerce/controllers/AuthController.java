package com.yashmerino.ecommerce.controllers;

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

import com.yashmerino.ecommerce.model.dto.SuccessDTO;
import com.yashmerino.ecommerce.model.dto.auth.AuthResponseDTO;
import com.yashmerino.ecommerce.model.dto.auth.LoginDTO;
import com.yashmerino.ecommerce.model.dto.auth.RegisterDTO;
import com.yashmerino.ecommerce.services.interfaces.AuthService;
import com.yashmerino.ecommerce.swagger.SwaggerConfig;
import com.yashmerino.ecommerce.swagger.SwaggerHttpStatus;
import com.yashmerino.ecommerce.swagger.SwaggerMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication.
 */
@Tag(name = "1. Authentication/Authorization", description = "These endpoints are used to register/login.")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    /**
     * Authentication/authorization service.
     */
    private AuthService authService;

    /**
     * Constructor.
     *
     * @param authService is the auth service.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user.
     *
     * @param registerDTO is the user's data.
     * @return <code>ResponseEntity</code>
     * @throws EntityNotFoundException if role couldn't be found.
     */
    @Operation(summary = "Registers a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.USER_SUCCESSFULLY_REGISTERED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.USERNAME_IS_TAKEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @PostMapping("/register")
    public ResponseEntity<SuccessDTO> register(@Parameter(description = "JSON Object for user's credentials.") @Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);

        SuccessDTO successDTO = new SuccessDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("user_registered_successfully");

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }

    /**
     * Login for user.
     *
     * @param loginDTO is the user's data.
     * @return <code>ResponseEntity</code>
     */
    @Operation(summary = "Allows user to login and get his JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.USER_SIGNED_IN,
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseDTO.class))}),
            @ApiResponse(responseCode = SwaggerHttpStatus.NOT_FOUND, description = SwaggerMessages.USER_DOES_NOT_EXIST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Parameter(description = "JSON Object for user's credentials.") @Valid @RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);

        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    }
}
