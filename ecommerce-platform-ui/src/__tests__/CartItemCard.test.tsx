import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import CartItemCard from "../app/components/pages/cart/CartItemCard";
import * as CartItemsRequest from "../app/api/CartItemsRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";

describe("Cart Item Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, roles: { roles: { roles: [{ id: 1, name: "USER" }] } }, lang: { lang: "ENG" } };
    const mockStore = configureStore();
    let store: Store;
    const mockOnUpdate = jest.fn();

    it("Test Cart Items displayed", () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartItemCard id={1} productId={1} price="2.5" quantity={1} title="Apple" key={1} onUpdate={mockOnUpdate} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const productTitle = screen.getByText("Apple");
            expect(productTitle).toBeInTheDocument();

            const productPrice = screen.getByText("Price: 2.5");
            expect(productPrice).toBeInTheDocument();

            const quantityInput = screen.getByTestId("quantity-input-1");
            expect(quantityInput).toBeInTheDocument();

            const deleteIcon = screen.getByTestId("delete-icon");
            expect(deleteIcon).toBeInTheDocument();
        })
    });

    it("Test delete cart item", () => {
        store = mockStore(initialState)

        const deleteCartItem = jest.spyOn(CartItemsRequest, 'deleteCartItem');
        deleteCartItem.mockReturnValue(Promise.resolve({ "status": 200, "message": "Cart item successfully deleted!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartItemCard id={1} productId={1} price="2.5" quantity={1} title="Apple" key={1} onUpdate={mockOnUpdate} />
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

        const changeQuantity = jest.spyOn(CartItemsRequest, 'changeQuantity');
        changeQuantity.mockReturnValue(Promise.resolve({ "status": 200, "message": "Cart item successfully updated!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <CartItemCard id={1} productId={1} price="2.5" quantity={1} title="Apple" key={1} onUpdate={mockOnUpdate} />
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