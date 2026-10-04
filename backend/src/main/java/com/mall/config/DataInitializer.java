package com.mall.config;

import com.mall.domain.Category;
import com.mall.domain.Product;
import com.mall.domain.User;
import com.mall.domain.enums.Role;
import com.mall.repository.CategoryRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initUsers();
        if (categoryRepository.count() == 0) {
            initCatalog();
        }
    }

    private void initUsers() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nickname("商城管理员")
                    .email("admin@example.com")
                    .role(Role.ADMIN)
                    .build());
        }
        if (!userRepository.existsByUsername("user")) {
            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .nickname("测试用户")
                    .email("user@example.com")
                    .role(Role.USER)
                    .build());
        }
    }

    private void initCatalog() {
        Category digital = categoryRepository.save(Category.builder()
                .name("数码家电")
                .icon("⌨")
                .sortOrder(1)
                .enabled(true)
                .build());
        Category home = categoryRepository.save(Category.builder()
                .name("家居生活")
                .icon("⌂")
                .sortOrder(2)
                .enabled(true)
                .build());
        Category sports = categoryRepository.save(Category.builder()
                .name("运动户外")
                .icon("◎")
                .sortOrder(3)
                .enabled(true)
                .build());
        Category food = categoryRepository.save(Category.builder()
                .name("食品饮料")
                .icon("◇")
                .sortOrder(4)
                .enabled(true)
                .build());

        productRepository.saveAll(List.of(
                product("无线降噪耳机", "40小时续航，沉浸式聆听",
                        "支持主动降噪与通透模式，适合学习、通勤和日常音乐欣赏。",
                        "399.00", "499.00", 120, 268,
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
                        digital),
                product("智能运动手表", "全天候健康监测",
                        "内置心率、睡眠与多种运动模式，支持消息提醒和长续航。",
                        "699.00", "899.00", 80, 156,
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80",
                        digital),
                product("便携蓝牙音箱", "小体积，大声场",
                        "防水设计，支持立体声串联，宿舍、露营都能轻松使用。",
                        "229.00", "299.00", 96, 312,
                        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80",
                        digital),
                product("北欧简约台灯", "三档色温，护眼照明",
                        "简洁外观搭配柔和光线，适合卧室阅读与桌面学习。",
                        "159.00", "199.00", 150, 98,
                        "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80",
                        home),
                product("陶瓷马克杯套装", "日用美学，两只装",
                        "高温烧制陶瓷杯，杯身圆润，适合咖啡、牛奶和茶饮。",
                        "79.00", "109.00", 200, 421,
                        "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?auto=format&fit=crop&w=900&q=80",
                        home),
                product("轻量瑜伽垫", "防滑回弹，收纳方便",
                        "双面防滑纹理，适合瑜伽、拉伸和家庭健身。",
                        "119.00", "169.00", 110, 187,
                        "https://images.unsplash.com/photo-1592432678016-e910b452f9a2?auto=format&fit=crop&w=900&q=80",
                        sports),
                product("户外露营折叠椅", "加宽椅面，承重稳固",
                        "轻量铝合金骨架，折叠后可放入后备箱，适合野餐露营。",
                        "189.00", "239.00", 65, 75,
                        "https://images.unsplash.com/photo-1523987355523-c7b5b0dd90a7?auto=format&fit=crop&w=900&q=80",
                        sports),
                product("每日坚果礼盒", "科学配比，独立包装",
                        "精选多种坚果与果干，适合早餐、办公室加餐和节日送礼。",
                        "129.00", "169.00", 300, 536,
                        "https://images.unsplash.com/photo-1599599810694-b5b37304c041?auto=format&fit=crop&w=900&q=80",
                        food)
        ));
    }

    private Product product(String name,
                            String subtitle,
                            String description,
                            String price,
                            String originalPrice,
                            int stock,
                            int sales,
                            String cover,
                            Category category) {
        return Product.builder()
                .name(name)
                .subtitle(subtitle)
                .description(description)
                .price(new BigDecimal(price))
                .originalPrice(new BigDecimal(originalPrice))
                .stock(stock)
                .sales(sales)
                .cover(cover)
                .category(category)
                .status(true)
                .build();
    }
}
