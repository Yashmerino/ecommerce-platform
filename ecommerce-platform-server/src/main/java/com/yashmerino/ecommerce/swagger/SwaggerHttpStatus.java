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
 * Status codes displayed in the swagger UI.
 */
public class SwaggerHttpStatus {

    /**
     * Private constructor to now allow instantiation.
     */
    private SwaggerHttpStatus() {

    }

    /**
     * Status code for OK.
     */
    public static final String OK = "200";

    /**
     * Status code for created.
     */
    public static final String CREATED = "201";

    /**
     * Status code for no content.
     */
    public static final String NO_CONTENT = "204";

    /**
     * Status code for bad request.
     */
    public static final String BAD_REQUEST = "400";

    /**
     * Status code for unauthorized.
     */
    public static final String UNAUTHORIZED = "401";

    /**
     * Status code for forbidden.
     */
    public static final String FORBIDDEN = "403";

    /**
     * Status code for not found.
     */
    public static final String NOT_FOUND = "404";

    /**
     * Status code for unprocessable entity.
     */
    public static final String UNPROCESSABLE_ENTITY = "422";

    /**
     * Status code for conflict.
     */
    public static final String CONFLICT = "409";

    /**
     * Status code for internal server error.
     */
    public static final String INTERNAL_SERVER_ERROR = "500";
}
