import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import CartContainer from "../app/components/pages/cart/CartContainer";
import * as CartItemsRequest from "../app/api/CartItemsRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";

describe("Cart Container Tests", () => {
    const initialState = { jwt: { token: "jwtkey", refreshToken: "refresh123" }, username: { sub: "user" }, info: { info: { roles: [{ id: 1, name: "USER" }], email: null }}, lang: { lang: "ENG" }, theme: { theme: "false" } };
    const mockStore = configureStore();
    let store: Store;

    it("Test Cart Items displayed", () => {
        store = mockStore(initialState)

        const getCartItems = jest.spyOn(CartItemsRequest, 'getCartItems');
        const result = [{ id: 1, "productId": 1, "name": "Apple", "price": 2.5, "cartId": 1, "quantity": 5 }];
        getCartItems.mockReturnValue(Promise.resolve(result));


        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartContainer />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const title = screen.getByText("ecommerce_platform");
            expect(title).toBeInTheDocument();

            const copyright = screen.getByText("Copyright");
            expect(copyright).toBeInTheDocument();

            const productTitle = screen.getByText("Apple");
            expect(productTitle).toBeInTheDocument();

            const productPrice = screen.getByText("Price: 2.5");
            expect(productPrice).toBeInTheDocument();

            const quantityInput = screen.getByTestId("quantity-input-1");
            expect(quantityInput).toBeInTheDocument();

            const saveCartItemButton = screen.getByText("save");
            expect(saveCartItemButton).toBeInTheDocument();
        })
    });

    it("Test no rights to access page as Seller", () => {
        const newState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null }}, lang: { lang: "ENG" }, theme: { theme: "false" } };
        store = mockStore(newState)

        const getCartItems = jest.spyOn(CartItemsRequest, 'getCartItems');
        const result = [{ "productId": 1, "name": "Apple", "price": 2.5, "cartId": 1, "quantity": 5 }];
        getCartItems.mockReturnValue(Promise.resolve(result));


        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartContainer />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const productPrice = screen.getByText("no_rights_to_access");
            expect(productPrice).toBeInTheDocument();
        })
    });

    it("Test delete cart item", () => {
        store = mockStore(initialState)

        const getCartItems = jest.spyOn(CartItemsRequest, 'getCartItems');
        const result = [{ "productId": 1, "name": "Apple", "price": 2.5, "cartId": 1, "quantity": 5 }];
        getCartItems.mockReturnValue(Promise.resolve(result));


        const deleteCartItem = jest.spyOn(CartItemsRequest, 'deleteCartItem');
        deleteCartItem.mockReturnValue(Promise.resolve({ "status": 200, "message": "Cart item successfully deleted!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartContainer />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const deleteButton = screen.getByTestId("delete-button-1");
            deleteButton.click();

            const successAlert = screen.getByAltText("Cart item successfully deleted!");
            expect(successAlert).toBeInTheDocument();
        })
    });

    it("Test save cart item", () => {
        store = mockStore(initialState)

        const getCartItems = jest.spyOn(CartItemsRequest, 'getCartItems');
        const result = [{ id: 1, "productId": 1, "name": "Apple", "price": 2.5, "cartId": 1, "quantity": 5 }];
        getCartItems.mockReturnValue(Promise.resolve(result));


        const changeQuantity = jest.spyOn(CartItemsRequest, 'changeQuantity');
        changeQuantity.mockReturnValue(Promise.resolve({ "status": 200, "message": "Cart item successfully updated!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartContainer />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const saveButton = screen.getByTestId("save-button-1");
            saveButton.click();

            const successAlert = screen.getByAltText("Cart item successfully updated!");
            expect(successAlert).toBeInTheDocument();
        })
    });
});