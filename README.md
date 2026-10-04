# 拾光商城

一个适合毕业设计演示的前后端分离购物网站，包含用户端商城和管理后台。

## 功能范围

用户端：

- 用户注册、登录、JWT 登录状态保持
- 首页商品推荐、分类浏览、关键词搜索
- 商品列表、分页、商品详情
- 加入购物车、修改数量、删除购物车商品
- 填写收货信息并提交订单
- 查看订单列表和订单详情
- 模拟支付、取消待付款订单

管理后台：

- 查看用户、商品、订单和有效订单金额统计
- 新增、编辑、下架商品
- 查询订单、更新待发货/已发货/已完成/已取消状态

本项目不包含真实支付、优惠券、秒杀、分布式事务等复杂功能。

## 技术栈

- 后端：Spring Boot 3.3、Spring Security、JWT、Spring Data JPA
- 数据库：H2 文件数据库（默认，开箱即用）
- 可选数据库：MySQL 8
- 前端：Vue 3、Vite、Vue Router、Pinia、Axios

## 项目结构

```text
shopping-mall/
├── backend/                    Spring Boot 后端
│   ├── src/main/java/com/mall/
│   │   ├── common/             通用响应和异常处理
│   │   ├── config/             安全配置和初始化数据
│   │   ├── controller/         REST 接口
│   │   ├── domain/             实体和枚举
│   │   ├── dto/                请求、响应对象
│   │   ├── repository/         数据访问层
│   │   ├── security/           JWT 和安全认证
│   │   └── service/            业务逻辑
│   └── src/main/resources/application.yml
├── frontend/                   Vue 3 前端
│   └── src/
│       ├── api/                Axios 封装
│       ├── components/         公共组件
│       ├── router/             路由与权限守卫
│       ├── stores/             Pinia 状态管理
│       ├── styles/             全局样式
│       └── views/              商城和后台页面
└── README.md
```

## 默认账号

| 类型 | 用户名 | 密码 |
| --- | --- | --- |
| 普通用户 | `user` | `user123` |
| 管理员 | `admin` | `admin123` |

首次启动后端时会自动创建账号、分类和演示商品。

## 启动后端

要求：JDK 17、Maven 3.8+，也可以直接使用 IntelliJ IDEA 的 Maven。

### IDEA 导入方式

1. 打开 IntelliJ IDEA，选择 `File -> Open`。
2. 选择项目根目录 `shopping-mall`，不要只选择 `backend`。
3. IDEA 会根据根目录 `pom.xml` 自动识别 Maven 项目和后端模块。
4. Maven home path 选择：

```text
D:\Software\IntelliJ_IDEA\Idea\plugins\maven-plugin\lib\maven3
```

5. Project SDK 和 Runner JRE 选择：

```text
D:\Software\IntelliJ_IDEA\jdk\jdk-17.0.20
```

6. 等待 Maven 依赖加载完成。
7. 直接运行 IDEA 中预置的 `启动商城后端` 配置，或运行 `com.mall.MallApplication`。

在 `backend` 目录执行：

```powershell
mvn spring-boot:run
```

也可以在 IntelliJ IDEA 中打开 `backend`，运行：

```text
com.mall.MallApplication
```

后端默认地址：

```text
http://localhost:8080
```

H2 控制台：

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/mall
用户名: sa
密码: 留空
```

## 启动前端

要求：Node.js 18+。

```powershell
cd frontend
npm install
npm run dev
```

前端地址：

```text
http://localhost:5173
```

Vite 已配置 `/api` 代理到 `http://localhost:8080`，两个服务同时启动即可使用。

## 生产构建

```powershell
cd frontend
npm run build
```

构建结果在 `frontend/dist`。

后端打包：

```powershell
cd backend
mvn clean package
java -jar target/shopping-mall-backend-1.0.0.jar
```

## 使用 MySQL（可选）

创建数据库：

```sql
CREATE DATABASE shopping_mall DEFAULT CHARACTER SET utf8mb4;
```

启动后端前设置环境变量。PowerShell 示例：

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/shopping_mall?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的密码"
$env:DB_DRIVER="com.mysql.cj.jdbc.Driver"
mvn spring-boot:run
```

JPA 会自动建表并写入演示数据。

## 主要接口

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| GET | `/api/catalog/categories` | 商品分类 |
| GET | `/api/catalog/products` | 商品列表和搜索 |
| GET | `/api/catalog/products/{id}` | 商品详情 |
| GET/POST | `/api/cart` | 查看/加入购物车 |
| PUT/DELETE | `/api/cart/{id}` | 修改/删除购物车项 |
| GET/POST | `/api/orders` | 查询/提交订单 |
| POST | `/api/orders/{orderNo}/pay` | 模拟支付 |
| GET | `/api/admin/dashboard` | 后台统计 |
| GET/POST/PUT/DELETE | `/api/admin/products` | 商品管理 |
| GET/PATCH | `/api/admin/orders` | 订单管理 |

除公开商品接口和登录注册外，其余接口需要在请求头携带：

```text
Authorization: Bearer <token>
```

## 说明

- 图片使用外部图片地址，运行时需要网络。
- 支付按钮只修改订单状态，用于毕业设计演示。
- 商品“下架”采用逻辑下架，不会删除历史订单。
- 管理员下架分类后，前台不再显示该分类。
