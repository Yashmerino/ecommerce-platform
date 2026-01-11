/*
 * MIT License
 *
 * Copyright (c) 2023 Artiom Bozieac
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import { API_BASE_URL } from "../../env-config";

/**
 * API Request to get user's information.
 * @param username The user's username.
 * @returns Response.
 */
export const getUserInfo = async (username: string) => {
    const response = await fetch(`${API_BASE_URL}/api/user/${username}`, {
        headers: { 'Content-Type': 'application/json' }
    })

    return response.json();
}

/**
 * API Request to get user's photo.
 * @param username User's username.
 * @returns Response Blob.
 */
export const getUserPhoto = async (username: string) => {
    const response = await fetch(`${API_BASE_URL}/api/user/${username}/photo`, {})

    if (response.status == 401) {
        return response;
    }

    return response.blob();
}

/**
 * API Request to set user's photo.
 * @param token The JWT Token.
 * @param username The user's username.
 * @param photo The user's new photo.
 * @returns Response.
 */
export const setUserPhoto = async (token: string, username: string, photo: File | null) => {
    const formData = new FormData();
    formData.append("photo", photo ?? "");

    const response = await fetch(`${API_BASE_URL}/api/user/${username}/photo`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData
    })

    if (response.status == 401) {
        return response;
    }

    return response.json();
}

/**
 * API Request to update user.
 * @param token The JWT Token.
 * @param username The user's username.
 * @param email The user's email.
 * @returns Response.
 */
export const updateUser = async (token: string, username: string, email: string) => {
    const userDTO = {
        email
    };

    const response = await fetch(`${API_BASE_URL}/api/user/${username}`, {
        method: "PUT",
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify(userDTO),
    });

    if (response.status == 401) {
        return response;
    }

    return response.json();
}