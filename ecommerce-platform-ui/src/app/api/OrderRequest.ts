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
import { authenticatedPost, authenticatedGet } from "../utils/AuthInterceptor";

export interface CreateOrderRequest {
  totalAmount: number;
  status: string;
}

export interface OrderResponse {
  id: number;
  status: string;
}

export interface OrderWithPayment {
    orderId: number;
    totalAmount: number;
    orderStatus: string;
    createdAt: string;
    paymentId: number;
    paymentAmount: number;
    paymentStatus: string;
    paymentCreatedAt: string;
}

export interface PaginatedOrderResponse {
    data: OrderWithPayment[];
    currentPage: number;
    totalPages: number;
    totalItems: number;
    pageSize: number;
    hasNext: boolean;
    hasPrevious: boolean;
}

export const createOrder = async (orderData: CreateOrderRequest): Promise<OrderResponse | any> => {
    const response = await authenticatedPost(`${API_BASE_URL}/api/order`, orderData);

    if (!response.ok) {
        return response;
    }

    return response.json();
};

export const getUserOrders = async (page: number = 0, size: number = 10): Promise<PaginatedOrderResponse | Response> => {
    const response = await authenticatedGet(`${API_BASE_URL}/api/order/my-orders?page=${page}&size=${size}`);

    if (!response.ok) {
        return response;
    }

    return await response.json();
}
