package com.mall.controller;

import com.mall.common.ApiResponse;
import com.mall.common.BusinessException;
import com.mall.domain.enums.OrderStatus;
import com.mall.dto.Dtos;
import com.mall.service.AdminService;
import com.mall.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public ApiResponse<Dtos.DashboardView> dashboard() {
        return ApiResponse.success(adminService.dashboard());
    }

    @GetMapping("/products")
    public ApiResponse<Dtos.PageResult<Dtos.ProductView>> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(adminService.products(keyword, categoryId, page, size));
    }

    @PostMapping("/products")
    public ApiResponse<Dtos.ProductView> createProduct(
            @Valid @RequestBody Dtos.ProductRequest request) {
        return ApiResponse.success(adminService.saveProduct(null, request));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<Dtos.ProductView> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Dtos.ProductRequest request) {
        return ApiResponse.success(adminService.saveProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        adminService.disableProduct(id);
        return ApiResponse.success();
    }

    @GetMapping("/categories")
    public ApiResponse<List<Dtos.CategoryView>> categories() {
        return ApiResponse.success(adminService.categories());
    }

    @PostMapping("/categories")
    public ApiResponse<Dtos.CategoryView> createCategory(
            @Valid @RequestBody Dtos.CategoryRequest request) {
        return ApiResponse.success(adminService.saveCategory(null, request));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<Dtos.CategoryView> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Dtos.CategoryRequest request) {
        return ApiResponse.success(adminService.saveCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        adminService.disableCategory(id);
        return ApiResponse.success();
    }

    @GetMapping("/orders")
    public ApiResponse<Dtos.PageResult<Dtos.OrderView>> orders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(adminService.orders(keyword, status, page, size));
    }

    @PatchMapping("/orders/{orderNo}/status")
    public ApiResponse<Dtos.OrderView> updateOrderStatus(
            @PathVariable String orderNo,
            @Valid @RequestBody Dtos.StatusRequest request) {
        try {
            return ApiResponse.success(
                    orderService.updateStatus(orderNo, OrderStatus.valueOf(request.status()))
            );
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("不支持的订单状态");
        }
    }
}
