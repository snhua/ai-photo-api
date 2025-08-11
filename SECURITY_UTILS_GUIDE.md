# SecurityUtils 功能完善指南

## 概述

`SecurityUtils` 是一个安全工具类，用于获取当前登录用户的信息。经过完善后，现在可以正确获取用户 ID、openid 等信息。

## 主要功能

### 1. 获取当前用户 ID

```java
Long userId = SecurityUtils.getCurrentUserId();
```

**功能说明**:

- 从 Spring Security 上下文中获取当前认证信息
- 通过 openid 调用 UserService 获取用户信息
- 返回用户的数据库 ID

**返回值**:

- 成功：返回用户 ID (Long 类型)
- 失败：抛出 UnauthorizedException 异常

**使用场景**:

- 在需要获取当前用户 ID 的接口中使用
- 例如：文件上传、订单创建等

### 2. 获取当前用户 openid

```java
String openid = SecurityUtils.getCurrentUserOpenid();
```

**功能说明**:

- 从 Spring Security 上下文中获取当前用户的 openid
- 直接返回认证信息中的用户名（即 openid）

**返回值**:

- 成功：返回用户 openid (String 类型)
- 失败：抛出 UnauthorizedException 异常

### 3. 检查用户认证状态

```java
boolean isAuthenticated = SecurityUtils.isAuthenticated();
```

**功能说明**:

- 检查当前用户是否已认证
- 不抛出异常，只返回布尔值

**返回值**:

- true：用户已认证
- false：用户未认证

### 4. 检查用户权限

```java
boolean hasAuthority = SecurityUtils.hasAuthority("ROLE_USER");
```

**功能说明**:

- 检查当前用户是否具有指定权限
- 支持角色和权限检查

**返回值**:

- true：用户具有指定权限
- false：用户不具有指定权限

## 实现原理

### 1. 依赖注入

SecurityUtils 通过 Spring 的依赖注入获取 UserService：

```java
@Component
public class SecurityUtils {
    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtils.userService = userService;
    }
}
```

### 2. 用户 ID 获取流程

```java
public static Long getCurrentUserId() {
    // 1. 获取认证信息
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 2. 检查认证状态
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new UnauthorizedException("用户未登录");
    }

    // 3. 获取用户openid
    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails) {
        String openid = ((UserDetails) principal).getUsername();

        // 4. 通过openid获取用户信息
        User user = userService.getUserByOpenid(openid);
        if (user != null) {
            return user.getId();
        } else {
            throw new UnauthorizedException("用户不存在");
        }
    }

    throw new UnauthorizedException("无法获取用户信息");
}
```

## 使用示例

### 1. 在 Controller 中使用

```java
@GetMapping("/user/info")
public Result<User> getUserInfo() {
    try {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);
        return Result.success(user);
    } catch (UnauthorizedException e) {
        return Result.error(401, e.getMessage());
    }
}
```

### 2. 在 Service 中使用

```java
@Service
public class OrderService {

    public Result<Order> createOrder(OrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 使用userId创建订单
        // ...
    }
}
```

### 3. 在 COS 上传中使用

```java
@GetMapping("/cos/temp-auth")
public Result<Map<String, Object>> getTempAuth(...) {
    try {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Object> authInfo = cosService.getUploadPolicy(userId, fileName, fileType);
        return Result.success(authInfo);
    } catch (UnauthorizedException e) {
        return Result.error(401, e.getMessage());
    }
}
```

## 错误处理

### 1. 用户未登录

```java
try {
    Long userId = SecurityUtils.getCurrentUserId();
} catch (UnauthorizedException e) {
    // 处理用户未登录的情况
    return Result.error(401, "用户未登录");
}
```

### 2. 用户不存在

```java
try {
    Long userId = SecurityUtils.getCurrentUserId();
} catch (UnauthorizedException e) {
    // 处理用户不存在的情况
    return Result.error(401, "用户不存在");
}
```

## 测试

提供了完整的测试类 `SecurityUtilsTest`，包含以下测试用例：

1. **正常获取用户 ID**: 测试正常情况下的用户 ID 获取
2. **未认证用户**: 测试未认证用户的异常处理
3. **用户不存在**: 测试用户不存在时的异常处理
4. **认证状态检查**: 测试用户认证状态检查
5. **获取 openid**: 测试获取用户 openid 功能

## 注意事项

1. **Spring Security 配置**: 确保 Spring Security 正确配置
2. **UserService 依赖**: 确保 UserService 中有 `getUserByOpenid` 方法
3. **异常处理**: 在业务代码中妥善处理 UnauthorizedException
4. **性能考虑**: 每次调用都会查询数据库，建议在需要时使用

## 最佳实践

1. **统一异常处理**: 在全局异常处理器中处理 UnauthorizedException
2. **缓存用户信息**: 对于频繁访问的用户信息，考虑使用缓存
3. **权限检查**: 结合 `hasAuthority` 方法进行细粒度权限控制
4. **日志记录**: 记录重要的用户操作日志

## 相关文件

- `SecurityUtils.java`: 安全工具类实现
- `SecurityUtilsTest.java`: 测试类
- `UserService.java`: 用户服务接口
- `UserServiceImpl.java`: 用户服务实现
