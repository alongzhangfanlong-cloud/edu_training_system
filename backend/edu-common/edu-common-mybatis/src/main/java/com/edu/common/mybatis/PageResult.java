package com.edu.common.mybatis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private Long total;
    private Integer page;
    private Integer pageSize;

    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .list(Collections.emptyList())
                .total(0L)
                .page(1)
                .pageSize(20)
                .build();
    }

    public static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer pageSize) {
        return PageResult.<T>builder()
                .list(list)
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .build();
    }
}
