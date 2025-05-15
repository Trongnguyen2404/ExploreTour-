package com.vivu.api.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content; // Nội dung trang hiện tại
    private int pageNo;      // Số trang hiện tại (bắt đầu từ 0)
    private int pageSize;    // Kích thước trang
    private long totalElements; // Tổng số phần tử
    private int totalPages;   // Tổng số trang
    private boolean last;     // Có phải trang cuối cùng không
}