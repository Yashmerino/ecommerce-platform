package com.yashmerino.ecommerce.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO that contains data alongside page details.
 *
 * @param <T> is the type of the data stored in the page.
 */
@Getter
@Setter
public class PaginatedDTO<T> {
    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;
    private double totalPrice;
    private boolean hasNext;
    private boolean hasPrevious;

    /**
     * Builds a paginated DTO from page.
     *
     * @param page is the page object.
     *
     * @return Paginated DTO.
     *
     * @param <T> is the type of the data stored in the page.
     */
    public static <T> PaginatedDTO<T> buildPaginatedResponse(Page<T> page) {
        PaginatedDTO<T> response = new PaginatedDTO<>();
        response.setData(page.getContent());
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalItems(page.getTotalElements());
        response.setPageSize(page.getSize());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}