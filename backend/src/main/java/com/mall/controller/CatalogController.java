package com.mall.controller;

import com.mall.common.ApiResponse;
import com.mall.dto.Dtos;
import com.mall.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping("/categories")
    public ApiResponse<List<Dtos.CategoryView>> categories() {
        return ApiResponse.success(catalogService.categories());
    }

    @GetMapping("/products")
    public ApiResponse<Dtos.PageResult<Dtos.ProductView>> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.success(catalogService.products(keyword, categoryId, page, size));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<Dtos.ProductView> product(@PathVariable Long id) {
        return ApiResponse.success(catalogService.product(id));
    }
}
