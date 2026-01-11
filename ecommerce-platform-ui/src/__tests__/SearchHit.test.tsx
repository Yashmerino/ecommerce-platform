import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as ProductRequest from "../app/api/ProductRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";
import SearchHit from "../app/components/pages/search/SearchHit";

describe("Search Hit Card Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, roles: { roles: { roles: [{ id: 1, name: "USER" }] } }, lang: { lang: "ENG" } };
    const mockStore = configureStore();
    let store: Store;

    it("Test Search Hit Card displayed", () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <SearchHit objectID={1} price={2.5} name="Apple" key={1} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const productTitle = screen.getByText("Apple");
            expect(productTitle).toBeInTheDocument();

            const productPrice = screen.getByText("Price: 2.5");
            expect(productPrice).toBeInTheDocument();

            const deleteIcon = screen.getByTestId("add-icon");
            expect(deleteIcon).toBeInTheDocument();
        })
    });

    it("Test add Search Hit", () => {
        store = mockStore(initialState)

        const addProductToCart = jest.spyOn(ProductRequest, 'addProductToCart');
        addProductToCart.mockReturnValue(Promise.resolve({ "status": 200, "message": "The product has been added successfully!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <SearchHit objectID={1} price={2.5} name="Apple" key={1} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const deleteButton = screen.getByTestId("add-icon");
            deleteButton.click();

            const successAlert = screen.getByAltText("The product has been added successfully!");
            expect(successAlert).toBeInTheDocument();
        })
    });
});