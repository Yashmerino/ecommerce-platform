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

import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.model.dto.auth.UserDTO;
import com.yashmerino.ecommerce.model.dto.auth.UserInfoDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for user service.
 */
public interface UserService {

    /**
     * Returns user by id.
     *
     * @param id is the user's id.
     * @return <code>User</code>
     */
    User getById(final Long id);

    /**
     * Returns user by username.
     *
     * @param username is the user's username.
     * @return <code>User</code>
     */
    User getByUsername(final String username);

    /**
     * Get information about a user by username.
     *
     * @param username is the user's username.
     * @return <code>UserInfoDTO</code>
     */
    UserInfoDTO getUserInfo(String username);

    /**
     * Update photo for user.
     *
     * @param photo    is the user's photo.
     * @param username is the user's username.
     */
    void updatePhoto(String username, MultipartFile photo);

    /**
     * Updates user information.
     *
     * @param username is the user's username.
     * @param userDTO  is the user's updated information.
     */
    void updateUser(String username, UserDTO userDTO);
}
