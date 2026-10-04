package com.mall.service;

import com.mall.common.BusinessException;
import com.mall.domain.Category;
import com.mall.domain.Product;
import com.mall.domain.enums.OrderStatus;
import com.mall.dto.Dtos;
import com.mall.repository.CategoryRepository;
import com.mall.repository.MallOrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MallOrderRepository orderRepository;
    private final CatalogService catalogService;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public Dtos.DashboardView dashboard() {
        BigDecimal sales = orderRepository.sumValidAmount();
        return new Dtos.DashboardView(
                userRepository.count(),
                productRepository.count(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT),
                sales == null ? BigDecimal.ZERO : sales
        );
    }

    @Transactional(readOnly = true)
    public Dtos.PageResult<Dtos.ProductView> products(String keyword,
                                                       Long categoryId,
                                                       int page,
                                                       int size) {
        Page<Product> result = productRepository.adminSearch(
                normalize(keyword),
                categoryId,
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 50),
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );
        return catalogService.toPage(result);
    }

    @Transactional
    public Dtos.ProductView saveProduct(Long id, Dtos.ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(404, "商品分类不存在"));
        Product product = id == null
                ? Product.builder().sales(0).build()
                : productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));
        product.setName(request.name());
        product.setSubtitle(request.subtitle());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setOriginalPrice(request.originalPrice());
        product.setStock(request.stock());
        product.setCover(request.cover());
        product.setCategory(category);
        product.setStatus(request.status() == null || request.status());
        return catalogService.toProductView(productRepository.save(product));
    }

    @Transactional
    public void disableProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在"));
        product.setStatus(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Dtos.CategoryView> categories() {
        return categoryRepository.findAll(Sort.by("sortOrder").ascending().and(Sort.by("id").ascending()))
                .stream()
                .map(catalogService::toCategoryView)
                .toList();
    }

    @Transactional
    public Dtos.CategoryView saveCategory(Long id, Dtos.CategoryRequest request) {
        Category category = id == null
                ? Category.builder().build()
                : categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
        category.setName(request.name());
        category.setIcon(request.icon());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setEnabled(request.enabled() == null || request.enabled());
        return catalogService.toCategoryView(categoryRepository.save(category));
    }

    @Transactional
    public void disableCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
        category.setEnabled(false);
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Dtos.PageResult<Dtos.OrderView> orders(String keyword,
                                                   String status,
                                                   int page,
                                                   int size) {
        OrderStatus orderStatus = status == null || status.isBlank()
                ? null
                : OrderStatus.valueOf(status);
        Page<com.mall.domain.MallOrder> result = orderRepository.adminSearch(
                normalize(keyword),
                orderStatus,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50))
        );
        return new Dtos.PageResult<>(
                result.getContent().stream().map(orderService::toView).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
