package com.example.tutorial.controller;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.examples.Example;

public class ControllerConstants {
    /**
     * Generics constants.
     */
    public static final String ID_PARAM = "id";
    public static final String PAGE_NUMBER_PARAM = "page";
    public static final String PAGE_SIZE_PARAM = "pageSize";
    public static final String SORT_ORDER_PARAM = "sortOrder";
    public static final String SORT_PROPERTY_PARAM = "sortProperty";
    public static final String TEXT_SEARCH_PARAM = "textSearch";

    public static final int PAGE_NUMBER_DEFAULT_VALUE = 0;
    public static final String PAGE_NUMBER_DEFAULT_STRING_VALUE = "0";
    public static final int PAGE_SIZE_DEFAULT_VALUE = 10;
    public static final String PAGE_SIZE_DEFAULT_STRING_VALUE = "10";
    public static final String SORT_DIRECTION_DEFAULT_VALUE = "desc";
    public static final String SORT_PROPERTY_DEFAULT_VALUE = "createdAt";
    public static final String USER_ID_PARAM = "userId";
    public static final String PAGE_NUMBER_DESCRIPTION = "Sequence number of pages starting from 0";
    public static final String PAGE_SIZE_DESCRIPTION = "Maximum amount of entities in one page";
    public static final String SORT_ORDER_DESCRIPTION = "Sorting direction";
    public static final String SORT_ORDER_EXAMPLE = "asc (ascending) or desc (descending)";
    public static final String SORT_PROPERTY_DESCRIPTION = "Entity property to sort by";

}
