package com.mall.controller;

import com.mall.common.ApiResponse;
import com.mall.dto.Dtos;
import com.mall.security.UserPrincipal;
import com.mall.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ApiResponse<Dtos.CartView> cart(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(cartService.getCart(principal.getId()));
    }

    @PostMapping
    public ApiResponse<Dtos.CartView> add(@AuthenticationPrincipal UserPrincipal principal,
                                          @Valid @RequestBody Dtos.CartItemRequest request) {
        return ApiResponse.success(cartService.add(principal.getId(), request));
    }

    @PutMapping("/{itemId}")
    public ApiResponse<Dtos.CartView> update(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable Long itemId,
                                             @RequestBody Dtos.CartItemRequest request) {
        return ApiResponse.success(cartService.update(principal.getId(), itemId, request.quantity()));
    }

    @DeleteMapping("/{itemId}")
    public ApiResponse<Dtos.CartView> remove(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable Long itemId) {
        return ApiResponse.success(cartService.remove(principal.getId(), itemId));
    }

    @DeleteMapping
    public ApiResponse<Void> clear(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clear(principal.getId());
        return ApiResponse.success();
    }
}
