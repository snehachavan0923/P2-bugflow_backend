package com.bugflow.dto.admin;

import java.util.List;

public class PageResponseDTO<T> {

    public List<T> content;

    public int page;

    public int size;

    public int totalPages;

    public long totalElements;

    public PageResponseDTO(
            List<T> content,
            int page,
            int size,
            long totalElements) {

        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;

        this.totalPages =
                size == 0
                        ? 0
                        : (int) Math.ceil(
                                (double) totalElements / size);
    }
}