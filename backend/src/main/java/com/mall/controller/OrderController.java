package com.mall.controller;

import com.mall.common.ApiResponse;
import com.mall.dto.Dtos;
import com.mall.security.UserPrincipal;
import com.mall.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ApiResponse<Dtos.PageResult<Dtos.OrderView>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(orderService.list(principal.getId(), page, size));
    }

    @PostMapping
    public ApiResponse<Dtos.OrderView> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody Dtos.OrderCreateRequest request) {
        return ApiResponse.success(orderService.create(principal.getId(), request));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<Dtos.OrderView> detail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String orderNo) {
        return ApiResponse.success(orderService.detail(principal.getId(), orderNo));
    }

    @PostMapping("/{orderNo}/pay")
    public ApiResponse<Dtos.OrderView> pay(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String orderNo) {
        return ApiResponse.success(orderService.pay(principal.getId(), orderNo));
    }

    @PostMapping("/{orderNo}/cancel")
    public ApiResponse<Dtos.OrderView> cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String orderNo) {
        return ApiResponse.success(orderService.cancel(principal.getId(), orderNo));
    }
}
