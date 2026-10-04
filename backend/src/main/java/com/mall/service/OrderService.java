package com.mall.service;

import com.mall.common.BusinessException;
import com.mall.domain.*;
import com.mall.domain.enums.OrderStatus;
import com.mall.dto.Dtos;
import com.mall.repository.CartItemRepository;
import com.mall.repository.MallOrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final MallOrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Dtos.OrderView create(Long userId, Dtos.OrderCreateRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        MallOrder order = MallOrder.builder()
                .orderNo(generateOrderNo())
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .receiverName(request.receiverName())
                .receiverPhone(request.receiverPhone())
                .receiverAddress(request.receiverAddress())
                .remark(request.remark())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (!Boolean.TRUE.equals(product.getStatus()) || product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(product.getName() + " 库存不足");
            }
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productCover(product.getCover())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();
            order.getItems().add(orderItem);
            total = total.add(subtotal);
        }
        order.setTotalAmount(total);
        orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setSales(product.getSales() + cartItem.getQuantity());
            productRepository.save(product);
        }
        cartItemRepository.deleteByUserId(userId);
        return toView(order);
    }

    @Transactional(readOnly = true)
    public Dtos.PageResult<Dtos.OrderView> list(Long userId, int page, int size) {
        Page<MallOrder> result = orderRepository.findByUserIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50))
        );
        return new Dtos.PageResult<>(
                result.getContent().stream().map(this::toView).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Transactional(readOnly = true)
    public Dtos.OrderView detail(Long userId, String orderNo) {
        return toView(orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在")));
    }

    @Transactional
    public Dtos.OrderView pay(Long userId, String orderNo) {
        MallOrder order = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("当前订单不能支付");
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        return toView(orderRepository.save(order));
    }

    @Transactional
    public Dtos.OrderView cancel(Long userId, String orderNo) {
        MallOrder order = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("只有待付款订单可以取消");
        }
        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        return toView(orderRepository.save(order));
    }

    @Transactional
    public Dtos.OrderView updateStatus(String orderNo, OrderStatus target) {
        MallOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        if (target == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            restoreStock(order);
        }
        order.setStatus(target);
        if (target == OrderStatus.PAID && order.getPaidAt() == null) {
            order.setPaidAt(LocalDateTime.now());
        } else if (target == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (target == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }
        return toView(orderRepository.save(order));
    }

    public Dtos.OrderView toView(MallOrder order) {
        List<Dtos.OrderItemView> items = order.getItems().stream()
                .map(item -> new Dtos.OrderItemView(
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductCover(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .toList();
        return new Dtos.OrderView(
                order.getOrderNo(),
                order.getStatus().name(),
                order.getStatus().getLabel(),
                order.getTotalAmount(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getReceiverAddress(),
                order.getRemark(),
                items,
                order.getCreatedAt(),
                order.getPaidAt()
        );
    }

    private void restoreStock(MallOrder order) {
        for (OrderItem item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productRepository.save(product);
            });
        }
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return "M" + time + random;
    }
}
