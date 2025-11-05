# 图书管理系统

## 项目简介

这是一个基于Spring Boot + Vue 3的图书管理系统，实现了图书信息管理、用户管理、借阅管理、统计报表等核心功能。

## 技术栈

### 后端
- Java 11
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3
- MySQL 8.0+
- Druid 数据库连接池
- JWT 认证
- Knife4j API文档

### 前端
- Vue 3
- Vue Router 4
- Pinia
- Element Plus
- Axios
- ECharts
- Vite

## 项目结构

```
library1.4/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/
│   │   │   │   ├── common/      # 通用类
│   │   │   │   ├── config/      # 配置类
│   │   │   │   └── ...
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── components/      # 组件
│   │   ├── views/          # 页面
│   │   ├── router/         # 路由
│   │   ├── utils/          # 工具
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
├── database/                # 数据库脚本
│   └── init.sql            # 初始化脚本
├── 需求文档.md              # 需求文档
├── 数据库设计文档.md        # 数据库设计文档
└── README.md               # 项目说明
```

## 开发环境要求

- JDK 11+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- IDE（推荐 IntelliJ IDEA / VS Code）

## 快速开始

### 1. 数据库初始化

```sql
-- 执行数据库初始化脚本
source database/init.sql
```

或使用MySQL客户端工具执行 `database/init.sql` 文件。

### 2. 后端启动

```bash
cd backend

# 修改 application.yml 中的数据库连接信息
# url: jdbc:mysql://localhost:3306/library_db
# username: root
# password: your_password

# 编译运行
mvn clean install
mvn spring-boot:run
```

后端服务启动后访问：
- API文档：http://localhost:8080/api/doc.html
- 服务地址：http://localhost:8080/api

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务启动后访问：http://localhost:3000

## 默认账号

### 系统管理员
- 用户名：admin
- 密码：admin123（实际应为加密后的密码，初始化脚本中已设置）

### 图书管理员
- 用户名：librarian
- 密码：librarian123（实际应为加密后的密码，初始化脚本中已设置）

> **注意**：初始化脚本中的密码是MD5加密后的值，实际使用时请根据需求修改。

## 开发阶段规划

### 第1周：项目搭建和数据库设计 ✅
- [x] 搭建开发环境
- [x] 创建项目框架
- [x] 设计数据库表结构
- [x] 创建数据库和表
- [x] 编写数据库初始化脚本

### 第2周：用户管理模块 ✅
- [x] 用户注册功能
- [x] 用户登录功能（含验证码）
- [x] 用户信息管理（增删改查）
- [x] 权限控制（RBAC）
- [x] 操作日志记录

### 第3周：图书管理模块 ✅
- [x] 图书信息管理（增删改查）
- [x] 图书分类管理
- [x] 图书查询功能（多条件搜索）
- [x] 图书库存管理

### 第4周：基础功能测试和优化
- [ ] 单元测试
- [ ] 功能测试
- [ ] Bug修复
- [ ] 代码优化

## 功能模块

### 1. 图书管理
- 图书信息管理（增删改查）
- 图书分类管理（三级分类）
- 图书查询（多条件搜索）
- 图书库存管理

### 2. 用户管理
- 用户注册/登录
- 用户信息管理
- 权限管理（RBAC）
- 操作日志

### 3. 借阅管理
- 借阅申请
- 图书归还
- 续借功能
- 逾期管理

### 4. 统计报表
- 数据统计
- 图表展示
- 报表导出

### 5. 系统设置
- 借阅规则设置
- 系统参数配置
- 数据备份与恢复

## 数据库设计

详细数据库设计文档请查看 [数据库设计文档.md](./数据库设计文档.md)

### 主要数据表
- `user` - 用户表
- `book` - 图书表
- `category` - 分类表
- `borrow_record` - 借阅记录表
- `operation_log` - 操作日志表
- `system_config` - 系统设置表

## API文档

启动后端服务后，访问 http://localhost:8080/api/doc.html 查看API文档。

## 开发规范

### 代码规范
- Java代码遵循阿里巴巴Java开发手册
- 前端代码遵循Vue官方风格指南
- 使用ESLint进行代码检查

### Git提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

## 许可证

本项目仅供学习使用。

## 联系方式

如有问题，请提交Issue或联系项目组。

---

**项目版本**：v1.0.0  
**创建日期**：2024  
**最后更新**：2024


