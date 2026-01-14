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
import { Category } from "../components/pages/products/AddProductPage";
import { authenticatedGet, authenticatedPost, authenticatedPut, authenticatedDelete } from "./AuthInterceptor";

export const getProducts = async (page = 0, size = 10) => {
    const response = await authenticatedGet(`${API_BASE_URL}/api/product?page=${page}&size=${size}`);

    if (!response.ok) {
        return response;
    }

    return response.json();
};

export const getProduct = async (id: number) => {
    const response = await authenticatedGet(`${API_BASE_URL}/api/product/${id}`);

    return response.json();
}

export const addProductToCart = async (id: number, quantity: number) => {
    const response = await authenticatedGet(`${API_BASE_URL}/api/product/${id}/add?quantity=${quantity}`);

    return response.json();
}

export const addProduct = async (name: string, categories: Category[], price: number, description: string) => {
    const productDTO = {
        name,
        price,
        categories,
        description
    }

    const response = await authenticatedPost(`${API_BASE_URL}/api/product`, productDTO);

    return response.json();
}

export const getSellerProducts = async (username: string, page = 0, size = 5) => {
    const response = await authenticatedGet(`${API_BASE_URL}/api/product/seller/${username}?page=${page}&size=${size}`);

    if (!response.ok) {
        return response;
    }

    return response.json();
};


export const deleteProduct = async (id: number) => {
    const response = await authenticatedDelete(`${API_BASE_URL}/api/product/${id}`);

    return response.json();
}

/**
 * API Request to get product's photo.
 * @param id Product's id.
 * @returns Response Blob.
 */
export const getProductPhoto = async (id: number) => {
    const response = await fetch(`${API_BASE_URL}/api/product/${id}/photo`, {})

    return response.blob();
}

export const setProductPhoto = async (id: number, photo: File | null) => {
    const formData = new FormData();
    formData.append("photo", photo ?? "");

    const response = await authenticatedPost(`${API_BASE_URL}/api/product/${id}/photo`, formData);

    return response.json();
}

export const updateProduct = async (id: number, name: string, categories: Category[], price: number, description: string) => {
    const productDTO = {
        name,
        price,
        categories,
        description
    }

    const response = await authenticatedPut(`${API_BASE_URL}/api/product/${id}`, productDTO);

    return response.json();
}

export const searchProducts = async (query: string, page = 0, size = 10) => {
    const response = await authenticatedGet(`${API_BASE_URL}/api/product/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);

    if (!response.ok) {
        return response;
    }

    return response.json();
};