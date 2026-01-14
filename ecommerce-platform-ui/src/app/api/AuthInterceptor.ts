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

import { store } from "../../store/store";
import { updateJwt, updateRefreshToken, clearTokens } from "../slices/jwtSlice";
import { refreshAccessToken } from "./AuthRequest";

let isRefreshing = false;
let failedQueue: Array<{ resolve: (value?: unknown) => void; reject: (reason?: any) => void }> = [];

const processQueue = (error: any = null) => {
    failedQueue.forEach((prom) => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve();
        }
    });

    failedQueue = [];
};

/**
 * Enhanced fetch wrapper that automatically refreshes tokens on 401 responses.
 * @param url The URL to fetch.
 * @param options The fetch options.
 * @returns Promise with the response.
 */
export const authenticatedFetch = async (url: string, options: RequestInit = {}): Promise<Response> => {
    const state = store.getState();
    const token = state.jwt.token;
    const refreshToken = state.jwt.refreshToken;

    // Add authorization header if token exists
    if (token && options.headers) {
        options.headers = {
            ...options.headers,
            Authorization: `Bearer ${token}`,
        };
    }

    // Always include credentials for cookies
    options.credentials = 'include';

    // Make the initial request
    let response = await fetch(url, options);

    // If 401 (Unauthorized), try to refresh the token
    if (response.status === 401 && refreshToken) {
        if (isRefreshing) {
            // Wait for the current refresh to complete
            return new Promise((resolve, reject) => {
                failedQueue.push({ resolve, reject });
            })
                .then(() => {
                    // Retry the original request with new token
                    const newState = store.getState();
                    const newToken = newState.jwt.token;
                    
                    if (options.headers) {
                        options.headers = {
                            ...options.headers,
                            Authorization: `Bearer ${newToken}`,
                        };
                    }
                    
                    return fetch(url, options);
                })
                .catch((err) => {
                    return Promise.reject(err);
                });
        }

        isRefreshing = true;

        try {
            // Attempt to refresh the token
            const refreshResponse = await refreshAccessToken(refreshToken);

            if (refreshResponse.accessToken) {
                // Update tokens in store
                store.dispatch(updateJwt(refreshResponse.accessToken));
                store.dispatch(updateRefreshToken(refreshResponse.refreshToken));

                // Process queued requests
                processQueue();

                // Retry the original request with new token
                if (options.headers) {
                    options.headers = {
                        ...options.headers,
                        Authorization: `Bearer ${refreshResponse.accessToken}`,
                    };
                }
                
                response = await fetch(url, options);
            } else {
                // Refresh failed, clear tokens and redirect to login
                processQueue(new Error("Token refresh failed"));
                store.dispatch(clearTokens());
                window.location.href = "/login";
            }
        } catch (error) {
            // Refresh failed, clear tokens and redirect to login
            processQueue(error);
            store.dispatch(clearTokens());
            window.location.href = "/login";
            throw error;
        } finally {
            isRefreshing = false;
        }
    }

    return response;
};

/**
 * Helper function to make authenticated GET requests.
 * @param url The URL to fetch.
 * @param options Additional fetch options.
 * @returns Promise with the response.
 */
export const authenticatedGet = async (url: string, options: RequestInit = {}): Promise<Response> => {
    return authenticatedFetch(url, {
        ...options,
        method: "GET",
    });
};

/**
 * Helper function to make authenticated POST requests.
 * @param url The URL to fetch.
 * @param body The request body.
 * @param options Additional fetch options.
 * @returns Promise with the response.
 */
export const authenticatedPost = async (url: string, body?: any, options: RequestInit = {}): Promise<Response> => {
    const isFormData = body instanceof FormData;
    
    return authenticatedFetch(url, {
        ...options,
        method: "POST",
        body: isFormData ? body : (body ? JSON.stringify(body) : undefined),
        headers: isFormData ? (options.headers || {}) : {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
    });
};

/**
 * Helper function to make authenticated DELETE requests.
 * @param url The URL to fetch.
 * @param options Additional fetch options.
 * @returns Promise with the response.
 */
export const authenticatedDelete = async (url: string, options: RequestInit = {}): Promise<Response> => {
    return authenticatedFetch(url, {
        ...options,
        method: "DELETE",
    });
};

/**
 * Helper function to make authenticated PUT requests.
 * @param url The URL to fetch.
 * @param body The request body.
 * @param options Additional fetch options.
 * @returns Promise with the response.
 */
export const authenticatedPut = async (url: string, body?: any, options: RequestInit = {}): Promise<Response> => {
    return authenticatedFetch(url, {
        ...options,
        method: "PUT",
        body: body ? JSON.stringify(body) : undefined,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
    });
};
