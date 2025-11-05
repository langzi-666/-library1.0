# 图书管理系统后端

## 技术栈

- Spring Boot 2.7.18
- MyBatis Plus 3.5.3
- MySQL 8.0+
- Druid 数据库连接池
- JWT 认证
- Knife4j API文档

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/library/
│   │   │   ├── common/          # 通用类
│   │   │   │   ├── Result.java  # 统一响应结果
│   │   │   │   └── BaseEntity.java
│   │   │   ├── config/          # 配置类
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/      # 控制器（待开发）
│   │   │   ├── service/         # 服务层（待开发）
│   │   │   ├── mapper/          # Mapper接口（待开发）
│   │   │   ├── entity/          # 实体类（待开发）
│   │   │   └── LibraryManagementSystemApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
└── pom.xml
```

## 配置说明

### 数据库配置

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 环境配置

- **开发环境**：`application-dev.yml`
- **生产环境**：`application-prod.yml`

启动时使用：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 开发指南

### 1. 创建实体类

在 `entity` 包下创建实体类，继承 `BaseEntity`：

```java
@Data
@TableName("user")
public class User extends BaseEntity {
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;
    
    private String username;
    // ...
}
```

### 2. 创建Mapper接口

在 `mapper` 包下创建Mapper接口：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 自定义查询方法
}
```

### 3. 创建Service

在 `service` 包下创建Service接口和实现类：

```java
public interface UserService extends IService<User> {
    // 业务方法
}
```

### 4. 创建Controller

在 `controller` 包下创建Controller：

```java
@RestController
@RequestMapping("/user")
public class UserController {
    // 接口方法
}
```

## API文档

启动项目后访问：http://localhost:8080/api/doc.html

## 开发规范

1. 统一使用 `Result<T>` 作为接口返回类型
2. 实体类继承 `BaseEntity` 获取公共字段
3. 使用 `@TableName` 指定表名
4. 使用 `@TableId` 指定主键
5. 遵循RESTful API设计规范


