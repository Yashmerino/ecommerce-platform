package com.yashmerino.ecommerce.controllers;
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

import com.yashmerino.ecommerce.model.Product;
import com.yashmerino.ecommerce.model.dto.PaginatedDTO;
import com.yashmerino.ecommerce.model.dto.ProductDTO;
import com.yashmerino.ecommerce.model.dto.SuccessDTO;
import com.yashmerino.ecommerce.model.dto.SuccessWithIdDTO;
import com.yashmerino.ecommerce.model.dto.*;
import com.yashmerino.ecommerce.services.interfaces.ProductService;
import com.yashmerino.ecommerce.swagger.SwaggerConfig;
import com.yashmerino.ecommerce.swagger.SwaggerHttpStatus;
import com.yashmerino.ecommerce.swagger.SwaggerMessages;
import com.yashmerino.ecommerce.utils.RequestBodyToEntityConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.yashmerino.ecommerce.utils.RequestBodyToEntityConverter.convertToProductDTO;

/**
 * Product's controller.
 */
@Tag(name = "2. Products Controller", description = "These endpoints are used to perform actions on products.")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/product")
@Validated
public class ProductController {

    /**
     * Products' service.
     */
    private final ProductService productService;

    /**
     * Constructor.
     *
     * @param productService        is the products' service.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Adds a product.
     *
     * @param productDTO is the product DTO.
     * @return <code>ResponseEntity</code>
     * @throws EntityNotFoundException if user couldn't be found.
     */
    @Operation(summary = "Adds a new product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.PRODUCT_SUCCESSFULLY_ADDED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @PostMapping
    public ResponseEntity<SuccessWithIdDTO> addProduct(@Validated @RequestBody ProductDTO productDTO) {
        Long productId = productService.addProduct(productDTO);

        SuccessWithIdDTO successDTO = new SuccessWithIdDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("product_added_successfully");
        successDTO.setId(productId);

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }

    /**
     * Adds a product to the cart.
     *
     * @param id       is the product's id.
     * @param quantity is the quantity of item.
     * @return <code>ResponseEntity</code>
     * @throws EntityNotFoundException if product or cart couldn't be found.
     */
    @Operation(summary = "Adds a product to the cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.ITEM_SUCCESSFULLY_ADDED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping("/{id}/add")
    public ResponseEntity<SuccessDTO> addProductToCart(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.addProductToCart(id, quantity);

        SuccessDTO successDTO = new SuccessDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("product_added_to_cart_successfully");

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }

    /**
     * Returns a product.
     *
     * @param id is the product's id.
     * @return <code>ResponseEntity</code>
     * @throws EntityNotFoundException if cart item couldn't be found.
     */
    @Operation(summary = "Returns a product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.RETURN_PRODUCT,
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        ProductDTO productDTO = convertToProductDTO(product);

        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    /**
     * Returns all the products.
     *
     * @param pageable is the page data.
     *
     * @return <code>Page of ProductDTOs</code>.
     */
    @Operation(summary = "Returns all the products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.RETURN_PRODUCTS,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping
    public PaginatedDTO<ProductDTO> getProducts(Pageable pageable) {
        Page<Product> page = productService.getAllProducts(pageable);

        List<ProductDTO> products = page.getContent().stream()
                .map(RequestBodyToEntityConverter::convertToProductDTO)
                .toList();

        PaginatedDTO<ProductDTO> paginated = new PaginatedDTO<>();
        paginated.setData(products);
        paginated.setCurrentPage(page.getNumber());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setTotalItems(page.getTotalElements());
        paginated.setPageSize(page.getSize());
        paginated.setHasNext(page.hasNext());
        paginated.setHasPrevious(page.hasPrevious());

        return paginated;
    }

    /**
     * Searches for products by query.
     *
     * @param pageable is the page data.
     *
     * @return <code>Page of ProductDTOs</code>.
     */
    @Operation(summary = "Searches for products by query.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.RETURN_PRODUCTS,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping("/search")
    public PaginatedDTO<ProductDTO> search(@RequestParam(name = "query") String query, Pageable pageable) {
        Page<Product> page = productService.search(query, pageable);

        List<ProductDTO> products = page.getContent().stream()
                .map(RequestBodyToEntityConverter::convertToProductDTO)
                .toList();

        PaginatedDTO<ProductDTO> paginated = new PaginatedDTO<>();
        paginated.setData(products);
        paginated.setCurrentPage(page.getNumber());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setTotalItems(page.getTotalElements());
        paginated.setPageSize(page.getSize());
        paginated.setHasNext(page.hasNext());
        paginated.setHasPrevious(page.hasPrevious());

        return paginated;
    }

    /**
     * Returns all the seller's products.
     *
     * @param username is the seller's username.
     * @param pageable is the page details.
     *
     * @return <code>Page of ProductDTOs</code>.
     */
    @Operation(summary = "Returns all the seller's products by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.RETURN_SELLER_PRODUCTS,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping("/seller/{username}")
    public PaginatedDTO<ProductDTO> getSellerProducts(@PathVariable String username, Pageable pageable) {
        Page<Product> page = productService.getSellerProducts(username, pageable);

        List<ProductDTO> products = page.getContent().stream()
                .map(RequestBodyToEntityConverter::convertToProductDTO)
                .toList();

        PaginatedDTO<ProductDTO> paginated = new PaginatedDTO<>();
        paginated.setData(products);
        paginated.setCurrentPage(page.getNumber());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setTotalItems(page.getTotalElements());
        paginated.setPageSize(page.getSize());
        paginated.setHasNext(page.hasNext());
        paginated.setHasPrevious(page.hasPrevious());

        return paginated;
    }

    /**
     * Deletes a product.
     *
     * @param id is the product's id.
     * @return <code>ResponseEntity</code>
     * @throws EntityNotFoundException if product couldn't be found.
     */
    @Operation(summary = "Deletes a product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.PRODUCT_SUCCESSFULLY_DELETED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessDTO> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        SuccessDTO successDTO = new SuccessDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("product_deleted_successfully");

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }

    /**
     * Sets product's photo.
     *
     * @param id    is the product's id.
     * @param photo is the product's photo.
     * @return <code>ResponseEntity</code>
     */
    @Operation(summary = "Updates product photo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.PRODUCT_PHOTO_IS_UPDATED,
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessDTO.class))}),
            @ApiResponse(responseCode = SwaggerHttpStatus.NOT_FOUND, description = SwaggerMessages.PRODUCT_DOES_NOT_EXIST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @PostMapping(path = "/{id}/photo", consumes = "multipart/form-data")
    public ResponseEntity<SuccessDTO> setProductPhoto(@PathVariable Long id, @Parameter(description = "Product's photo.", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestPart("photo") MultipartFile photo) {
        productService.updatePhoto(id, photo);

        SuccessDTO successDTO = new SuccessDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("product_photo_updated_successfully");

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }

    /**
     * Returns product's photo as array of bytes.
     *
     * @param id is the product's id.
     * @return <code>ResponseEntity</code>
     */
    @Operation(summary = "Returns product photo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.PRODUCT_PHOTO_RETURNED,
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = byte[].class))}),
            @ApiResponse(responseCode = SwaggerHttpStatus.NOT_FOUND, description = SwaggerMessages.PRODUCT_DOES_NOT_EXIST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping(path = "/{id}/photo")
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable Long id) {
        byte[] photo = productService.getProduct(id).getPhoto();

        return new ResponseEntity<>(photo, HttpStatus.OK);
    }

    /**
     * Updates a product.
     *
     * @param id         is the product's id.
     * @param productDTO is the product DTO.
     * @return <code>ResponseEntity</code>
     * @throws EntityNotFoundException if product couldn't be found.
     */
    @Operation(summary = "Updates a product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.PRODUCT_SUCCESSFULLY_UPDATED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<SuccessDTO> updateProduct(@PathVariable Long id, @Validated @RequestBody ProductDTO productDTO) {
        productService.updateProduct(id, productDTO);

        SuccessDTO successDTO = new SuccessDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("product_updated_successfully");

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }
}
