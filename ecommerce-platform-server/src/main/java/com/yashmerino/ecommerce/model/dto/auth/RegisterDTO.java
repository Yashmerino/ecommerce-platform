package com.yashmerino.ecommerce.model.dto.auth;

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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Register DTO.
 */
@Data
public class RegisterDTO {

    /**
     * User's role.
     */
    @NotNull(message = "Roles are required.")
    private Role role;

    /**
     * User's email.
     */
    @Email(message = "email_is_invalid")
    @NotNull(message = "email_is_required")
    @NotBlank(message = "email_is_required")
    // NOSONAR: The wrapper is required. Different error messages.
    @Size(min = 4, max = 255, message = "email_invalid_length")
    private String email;

    /**
     * User's username.
     */
    @NotNull(message = "username_is_required")
    @NotBlank(message = "username_is_required")
    // NOSONAR: The wrapper is required. Different error messages.
    @Size(min = 4, max = 255, message = "username_invalid_length")
    private String username;

    /**
     * User's password.
     */
    @NotNull(message = "password_is_required")
    @NotBlank(message = "password_is_required")
    // NOSONAR: The wrapper is required. Different error messages.
    @Size(min = 4, max = 255, message = "password_invalid_length")
    private String password;
}
