package com.mall.service;

import com.mall.common.BusinessException;
import com.mall.domain.CartItem;
import com.mall.domain.Product;
import com.mall.domain.User;
import com.mall.dto.Dtos;
import com.mall.repository.CartItemRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Dtos.CartView getCart(Long userId) {
        return buildCart(cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @Transactional
    public Dtos.CartView add(Long userId, Dtos.CartItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .filter(Product::getStatus)
                .orElseThrow(() -> new BusinessException(404, "商品不存在或已下架"));
        if (product.getStock() <= 0) {
            throw new BusinessException("商品库存不足");
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                .orElseGet(() -> {
                    User user = userRepository.getReferenceById(userId);
                    return CartItem.builder()
                            .user(user)
                            .product(product)
                            .quantity(0)
                            .build();
                });
        int newQuantity = item.getQuantity() + request.quantity();
        if (newQuantity > product.getStock()) {
            throw new BusinessException("购买数量超过库存");
        }
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Transactional
    public Dtos.CartView update(Long userId, Long itemId, int quantity) {
        if (quantity < 1) {
            throw new BusinessException("数量至少为1");
        }
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new BusinessException(404, "购物车商品不存在"));
        if (quantity > item.getProduct().getStock()) {
            throw new BusinessException("购买数量超过库存");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Transactional
    public Dtos.CartView remove(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new BusinessException(404, "购物车商品不存在"));
        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Transactional
    public void clear(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private Dtos.CartView buildCart(List<CartItem> items) {
        List<Dtos.CartLine> lines = items.stream().map(item -> {
            Product product = item.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new Dtos.CartLine(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getCover(),
                    product.getPrice(),
                    item.getQuantity(),
                    product.getStock(),
                    subtotal
            );
        }).toList();
        int totalQuantity = lines.stream().mapToInt(Dtos.CartLine::quantity).sum();
        BigDecimal totalAmount = lines.stream()
                .map(Dtos.CartLine::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dtos.CartView(lines, totalQuantity, totalAmount);
    }
}
