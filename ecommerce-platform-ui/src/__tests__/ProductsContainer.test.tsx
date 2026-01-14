import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import ProductsContainer from "../app/components/pages/products/ProductsContainer";
import * as ProductRequest from "../app/api/ProductRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";

describe("Products Container Tests", () => {
    const initialState = { jwt: { token: "jwtkey", refreshToken: "refresh123" }, username: { sub: "user" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null }}, lang: { lang: "ENG" }, theme: { theme: "false" } };
    const mockStore = configureStore()
    let store: Store;

    it("Test Products displayed", () => {
        store = mockStore(initialState)

        const getAllProductsMock = jest.spyOn(ProductRequest, 'getProducts');
        const result = [{ id: 1, "name": "Apple", "price": 2.5, "categories": [], "userId": 2 }];
        getAllProductsMock.mockReturnValue(Promise.resolve(JSON.stringify(result)));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <ProductsContainer />
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

            const addToCartButton = screen.getByText("Add To Cart");
            expect(addToCartButton).toBeInTheDocument();

            const quantityInput = screen.getByTestId("quantity-input-1");
            expect(quantityInput).toBeInTheDocument();
        })
    });

    it("Test add product to cart", () => {
        store = mockStore(initialState)

        const getAllProductsMock = jest.spyOn(ProductRequest, 'getProducts');
        const result = [{ "name": "Apple", "price": 2.5, "categories": [], "userId": 2 }];
        getAllProductsMock.mockReturnValue(Promise.resolve(JSON.stringify(result)));

        const addProductToCart = jest.spyOn(ProductRequest, "addProductToCart");
        const successResponse = { "status": 200, "message": "Product successfully added to the cart!" };
        addProductToCart.mockReturnValue(Promise.resolve(JSON.stringify(successResponse)));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <ProductsContainer />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const addToCartButton = screen.getByText("add_to_cart");
            expect(addToCartButton).toBeInTheDocument();
            addToCartButton.click();

            const successAlert = screen.getByText("Product successfully added to the cart!");
            expect(successAlert).toBeInTheDocument();
        })
    });

    it("Test no add product to the cart button as Seller", () => {
        const newState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null }}, lang: { lang: "ENG" }, theme: { theme: "false" } };
        store = mockStore(newState)

        const getAllProductsMock = jest.spyOn(ProductRequest, 'getProducts');
        const result = [{ "name": "Apple", "price": 2.5, "categories": [], "userId": 2 }];
        getAllProductsMock.mockReturnValue(Promise.resolve(JSON.stringify(result)));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <ProductsContainer />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const addToCartButton = screen.getByText("add_to_cart");
            expect(addToCartButton).toBeNull();
        })
    });
});