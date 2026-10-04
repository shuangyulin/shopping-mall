package com.mall.service;

import com.mall.common.BusinessException;
import com.mall.domain.Category;
import com.mall.domain.Product;
import com.mall.dto.Dtos;
import com.mall.repository.CategoryRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Dtos.CategoryView> categories() {
        return categoryRepository.findByEnabledTrueOrderBySortOrderAscIdAsc().stream()
                .map(this::toCategoryView)
                .toList();
    }

    @Transactional(readOnly = true)
    public Dtos.PageResult<Dtos.ProductView> products(String keyword,
                                                       Long categoryId,
                                                       int page,
                                                       int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Product> result = productRepository.search(
                normalize(keyword),
                categoryId,
                pageable
        );
        return toPage(result);
    }

    @Transactional(readOnly = true)
    public Dtos.ProductView product(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::getStatus)
                .orElseThrow(() -> new BusinessException(404, "商品不存在或已下架"));
        return toProductView(product);
    }

    public Dtos.CategoryView toCategoryView(Category category) {
        return new Dtos.CategoryView(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getSortOrder(),
                category.getEnabled()
        );
    }

    public Dtos.ProductView toProductView(Product product) {
        return new Dtos.ProductView(
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getDescription(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getStock(),
                product.getSales(),
                product.getCover(),
                product.getCategory() == null ? null : product.getCategory().getId(),
                product.getCategory() == null ? null : product.getCategory().getName(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }

    public Dtos.PageResult<Dtos.ProductView> toPage(Page<Product> result) {
        return new Dtos.PageResult<>(
                result.getContent().stream().map(this::toProductView).toList(),
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
