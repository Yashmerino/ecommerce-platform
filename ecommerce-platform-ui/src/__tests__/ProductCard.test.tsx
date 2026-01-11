import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as ProductRequest from "../app/api/ProductRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";
import ProductCard from "../app/components/pages/products/ProductCard";

describe("Product Card Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null } }, lang: { lang: "ENG" } };
    const mockStore = configureStore()
    let store: Store;

    it("Product Card displayed", () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <ProductCard id={1} title="Apple" price="2.5" categories={[]} description={""} key={1} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const productPrice = screen.getByText("Price: 2.5");
            expect(productPrice).toBeInTheDocument();

            const addToCartButton = screen.getByText("add_to_cart");
            expect(addToCartButton).toBeInTheDocument();

            const deleteButton = screen.getByTestId("delete-button-1");
            expect(deleteButton).toBeInTheDocument();

            const quantityInput = screen.getByTestId("quantity-input-1");
            expect(quantityInput).toBeInTheDocument();
        })
    });

    it("Test add product to cart", () => {
        store = mockStore(initialState)

        const addProductToCart = jest.spyOn(ProductRequest, "addProductToCart");
        const successResponse = { "status": 200, "message": "Product successfully added to the cart!" };
        addProductToCart.mockReturnValue(Promise.resolve(JSON.stringify(successResponse)));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <ProductCard id={1} title="Apple" price="2.5" categories={[]} description={""} key={1} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const addToCartButton = screen.getByText("add_to_cart");
            expect(addToCartButton).toBeInTheDocument();
            addToCartButton.click();

            const successAlert = screen.getByText("The product has been successfully added to the cart!");
            expect(successAlert).toBeInTheDocument();
        })
    });
});