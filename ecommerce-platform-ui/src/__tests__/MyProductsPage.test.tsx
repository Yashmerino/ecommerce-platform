import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as ProductRequest from "../app/api/ProductRequest";
import MyProductsPage from "../app/components/pages/products/MyProductsPage";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";

describe("Products Container Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "seller" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null }}, lang: { lang: "ENG" }, theme: { theme: "false" } };
    const mockStore = configureStore()
    let store: Store;

    it("Test Seller Products displayed", () => {
        store = mockStore(initialState)

        const getSellerProducts = jest.spyOn(ProductRequest, 'getSellerProducts');
        const result = [{ "name": "Apple", "price": 2.5, "categories": [], "userId": 2 }];
        getSellerProducts.mockReturnValue(Promise.resolve(JSON.stringify(result)));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProductsPage />
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

            const productPrice = screen.getByText("price 2.5");
            expect(productPrice).toBeInTheDocument();
        })
    });

    it("Test delete product", () => {
        store = mockStore(initialState)

        const getSellerProducts = jest.spyOn(ProductRequest, 'getSellerProducts');
        const result = [{ "name": "Apple", "price": 2.5, "categories": [], "userId": 2 }];
        getSellerProducts.mockReturnValue(Promise.resolve(JSON.stringify(result)));

        const deleteProduct = jest.spyOn(ProductRequest, 'deleteProduct');
        deleteProduct.mockReturnValue(Promise.resolve({ "status": 200, "message": "Product successfully deleted!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProductsPage />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const deleteButton = screen.getByTestId("delete-button-1");
            deleteButton.click();

            const successAlert = screen.getByAltText("Product successfully deleted!");
            expect(successAlert).toBeInTheDocument();
        })
    });
});