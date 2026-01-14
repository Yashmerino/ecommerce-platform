package com.yashmerino.ecommerce.swagger;

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + MIT License
 +
 + Copyright (c) 2023 Artiom Bozieac
 +
 + Permission is hereby granted, free of charge, to any person obtaining a copy
 + of this software and associated documentation files (the "Software"), to deal
 + in the Software without restriction, including without limitation the rights
 + to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 + copies of the Software, and to permit persons to whom the Software is
 + furnished to do so, subject to the following conditions:
 +
 + The above copyright notice and this permission notice shall be included in all
 + copies or substantial portions of the Software.
 +
 + THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 + IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 + FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 + AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 + LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 + OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 + SOFTWARE.
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

/**
 * Messages displayed in the swagger UI.
 */
public class SwaggerMessages {

    /**
     * Private constructor to now allow instantiation.
     */
    private SwaggerMessages() {

    }

    /**
     * Message when user successfully registered.
     */
    public static final String USER_SUCCESSFULLY_REGISTERED = "User successfully registered.";

    /**
     * Message when username is taken.
     */
    public static final String USERNAME_IS_TAKEN = "Username is taken.";

    /**
     * Message when a user is successfully signed in.
     */
    public static final String USER_SIGNED_IN = "User successfully signed in.";

    /**
     * Message when a user does not exist.
     */
    public static final String USER_DOES_NOT_EXIST = "User doesn't exist.";

    /**
     * Message when a product does not exist.
     */
    public static final String PRODUCT_DOES_NOT_EXIST = "Product doesn't exist.";

    /**
     * Message when a problem occurred on the server.
     */
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error.";

    /**
     * Message when the request is bad.
     */
    public static final String BAD_REQUEST = "Bad Request.";

    /**
     * Message when the cart item is successfully deleted.
     */
    public static final String ITEM_SUCCESSFULLY_DELETED = "Item successfully deleted!";

    /**
     * Message when the cart item's quantity is successfully changed.
     */
    public static final String QUANTITY_SUCCESSFULLY_CHANGED = "Quantity of the item successfully changed!";

    /**
     * Message when the cart item is returned.
     */
    public static final String RETURNED_CART_ITEM = "Returned item.";

    /**
     * Message when the endpoint is forbidden.
     */
    public static final String FORBIDDEN = "You have no access to this endpoint!";

    /**
     * Message when the user isn't authorized.
     */
    public static final String UNAUTHORIZED = "You are not authorized!";

    /**
     * Message when a product was successfully added.
     */
    public static final String PRODUCT_SUCCESSFULLY_ADDED = "Product was successfully added!";

    /**
     * Message when a product was successfully deleted.
     */
    public static final String PRODUCT_SUCCESSFULLY_DELETED = "Product was successfully deleted!";

    /**
     * Message when a product was successfully added as a cart item.
     */
    public static final String ITEM_SUCCESSFULLY_ADDED = "Item was successfully added!";

    /**
     * Message when a product was successfully updated.
     */
    public static final String PRODUCT_SUCCESSFULLY_UPDATED = "Product was successfully updated!";

    /**
     * Message when a product was returned.
     */
    public static final String RETURN_PRODUCT = "Returns the product.";

    /**
     * Message when all the products were returned.
     */
    public static final String RETURN_PRODUCTS = "Returns all the products.";

    /**
     * Message when sellers' products were returned.
     */
    public static final String RETURN_SELLER_PRODUCTS = "Returns all the seller's products";

    /**
     * Message when user info is returned.
     */
    public static final String USER_INFO_IS_RETURNED = "User information successfully returned.";

    /**
     * Message when categories are successfully retrieved.
     */
    public static final String CATEGORIES_SUCCESSFULLY_RETRIEVED = "Categories successfully retrieved.";

    /**
     * Message when user photo is successfully updated.
     */
    public static final String USER_PHOTO_IS_UPDATED = "User photo was successfully updated.";

    /**
     * Message when product photo is successfully updated.
     */
    public static final String PRODUCT_PHOTO_IS_UPDATED = "Product photo was successfully updated.";

    /**
     * Message when user photo is successfully returned.
     */
    public static final String USER_PHOTO_RETURNED = "User photo was successfully returned.";

    /**
     * Message when product photo is successfully returned.
     */
    public static final String PRODUCT_PHOTO_RETURNED = "Product photo was successfully returned.";

    /**
     * Message when user information is successfully updated.
     */
    public static final String USER_INFO_UPDATED = "User information was successfully updated.";

    /**
     * Message when user orders are successfully returned.
     */
    public static final String ORDERS_RETURNED = "Order information was successfully retrieved.";
}
