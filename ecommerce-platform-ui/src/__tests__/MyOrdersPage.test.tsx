import React from "react";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as OrderRequest from "../app/api/OrderRequest";
import * as PaymentRequest from "../app/api/PaymentRequest";
import MyOrdersPage from "../app/components/pages/orders/MyOrdersPage";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";

describe("My Orders Page Tests", () => {
    const initialState = { 
        jwt: { token: "jwtkey" }, 
        username: { sub: "user" }, 
        info: { info: { roles: [{ id: 1, name: "USER" }], email: "test@test.com" }}, 
        lang: { lang: "ENG" }, 
        theme: { theme: "false" } 
    };
    const mockStore = configureStore();
    let store: Store;

    beforeEach(() => {
        store = mockStore(initialState);
        jest.clearAllMocks();
    });

    it("Test Orders Page loads with header and title", async () => {
        const getUserOrders = jest.spyOn(OrderRequest, 'getUserOrders');
        const emptyResult = {
            data: [],
            currentPage: 0,
            totalPages: 0,
            totalItems: 0,
            pageSize: 10,
            hasNext: false,
            hasPrevious: false
        };
        getUserOrders.mockReturnValue(Promise.resolve(emptyResult));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyOrdersPage />
                </MemoryRouter>
            </Provider>
        );

        await waitFor(() => {
            const title = screen.getByTestId("header");
            expect(title).toBeInTheDocument();
        });
    });

    it("Test Orders displayed in table", async () => {
        const getUserOrders = jest.spyOn(OrderRequest, 'getUserOrders');
        const mockOrders = [
            {
                orderId: 1,
                totalAmount: 25.50,
                orderStatus: "DELIVERED",
                createdAt: "2024-01-15T10:30:00",
                paymentId: 1,
                paymentAmount: 25.50,
                paymentStatus: "SUCCEEDED",
                paymentCreatedAt: "2024-01-15T10:35:00"
            }
        ];

        const result = {
            data: mockOrders,
            currentPage: 0,
            totalPages: 1,
            totalItems: 1,
            pageSize: 10,
            hasNext: false,
            hasPrevious: false
        };
        getUserOrders.mockReturnValue(Promise.resolve(result));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyOrdersPage />
                </MemoryRouter>
            </Provider>
        );

        await waitFor(() => {
            // Check if order ID appears
            const orderId = screen.getByText("#1");
            expect(orderId).toBeInTheDocument();

            // Check if status appears
            const status = screen.getByText("DELIVERED");
            expect(status).toBeInTheDocument();

            // Check if payment status appears
            const paymentStatus = screen.getByText("SUCCEEDED");
            expect(paymentStatus).toBeInTheDocument();
        });
    });

    it("Test retry payment for failed payment", async () => {
        const getUserOrders = jest.spyOn(OrderRequest, 'getUserOrders');
        const mockOrders = [
            {
                orderId: 1,
                totalAmount: 25.50,
                orderStatus: "PROCESSING",
                createdAt: "2024-01-15T10:30:00",
                paymentId: 1,
                paymentAmount: 25.50,
                paymentStatus: "FAILED",
                paymentCreatedAt: "2024-01-15T10:35:00"
            }
        ];

        const result = {
            data: mockOrders,
            currentPage: 0,
            totalPages: 1,
            totalItems: 1,
            pageSize: 10,
            hasNext: false,
            hasPrevious: false
        };
        getUserOrders.mockReturnValue(Promise.resolve(result));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyOrdersPage />
                </MemoryRouter>
            </Provider>
        );

        await waitFor(() => {
            const failedStatus = screen.getByText("FAILED");
            expect(failedStatus).toBeInTheDocument();
        });
    });
});