package com.mall.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class Dtos {
    private Dtos() {
    }

    public record RegisterRequest(
            @NotBlank(message = "用户名不能为空")
            @Size(min = 3, max = 20, message = "用户名长度需为3-20位")
            String username,
            @NotBlank(message = "密码不能为空")
            @Size(min = 6, max = 32, message = "密码长度需为6-32位")
            String password,
            @Size(max = 50, message = "昵称不能超过50字")
            String nickname,
            @Email(message = "邮箱格式不正确")
            String email
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = "用户名不能为空") String username,
            @NotBlank(message = "密码不能为空") String password
    ) {
    }

    public record AuthResponse(String token, UserView user) {
    }

    public record UserView(Long id, String username, String nickname, String email, String avatar, String role) {
    }

    public record CategoryView(Long id, String name, String icon, Integer sortOrder, Boolean enabled) {
    }

    public record ProductView(
            Long id,
            String name,
            String subtitle,
            String description,
            BigDecimal price,
            BigDecimal originalPrice,
            Integer stock,
            Integer sales,
            String cover,
            Long categoryId,
            String categoryName,
            Boolean status,
            LocalDateTime createdAt
    ) {
    }

    public record ProductRequest(
            @NotBlank(message = "商品名称不能为空") String name,
            String subtitle,
            String description,
            @NotNull(message = "价格不能为空")
            @DecimalMin(value = "0.01", message = "价格必须大于0")
            BigDecimal price,
            BigDecimal originalPrice,
            @NotNull(message = "库存不能为空")
            @Min(value = 0, message = "库存不能小于0")
            Integer stock,
            @NotBlank(message = "商品图片不能为空") String cover,
            @NotNull(message = "商品分类不能为空") Long categoryId,
            Boolean status
    ) {
    }

    public record CategoryRequest(
            @NotBlank(message = "分类名称不能为空") String name,
            String icon,
            Integer sortOrder,
            Boolean enabled
    ) {
    }

    public record CartItemRequest(
            @NotNull(message = "商品不能为空") Long productId,
            @NotNull(message = "数量不能为空")
            @Min(value = 1, message = "数量至少为1")
            Integer quantity
    ) {
    }

    public record CartLine(
            Long id,
            Long productId,
            String productName,
            String cover,
            BigDecimal price,
            Integer quantity,
            Integer stock,
            BigDecimal subtotal
    ) {
    }

    public record CartView(List<CartLine> items, Integer totalQuantity, BigDecimal totalAmount) {
    }

    public record OrderCreateRequest(
            @NotBlank(message = "收货人不能为空") String receiverName,
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
            String receiverPhone,
            @NotBlank(message = "收货地址不能为空") String receiverAddress,
            String remark
    ) {
    }

    public record OrderItemView(
            Long productId,
            String productName,
            String productCover,
            BigDecimal price,
            Integer quantity,
            BigDecimal subtotal
    ) {
    }

    public record OrderView(
            String orderNo,
            String status,
            String statusText,
            BigDecimal totalAmount,
            String receiverName,
            String receiverPhone,
            String receiverAddress,
            String remark,
            List<OrderItemView> items,
            LocalDateTime createdAt,
            LocalDateTime paidAt
    ) {
    }

    public record StatusRequest(@NotNull(message = "订单状态不能为空") String status) {
    }

    public record DashboardView(
            long userCount,
            long productCount,
            long orderCount,
            long pendingOrderCount,
            BigDecimal salesAmount
    ) {
    }

    public record PageResult<T>(
            List<T> content,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }
}
