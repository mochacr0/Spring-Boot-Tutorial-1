package com.example.tutorial.common.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
public class PageParameter {
    private int page;
    private int pageSize;
    private String sortProperty;
    private String sortDirection;
    private String textSearch;

    private static final String DEFAULT_SORT_PROPERTY = "createdAt";
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, DEFAULT_SORT_PROPERTY);

    public PageParameter(int page, int pageSize, String sortDirection, String sortProperty, String textSearch) {
        this.page = page;
        this.pageSize = pageSize;
        this.sortDirection = sortDirection;
        this.sortProperty = sortProperty;
        this.textSearch = textSearch;
    }
    public Sort toSort() {
        return Sort.by(Sort.Direction.fromString(sortDirection), sortProperty);
    }
}
