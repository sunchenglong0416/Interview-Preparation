1.过滤器&拦截器
在 Spring 应用程序中，过滤器（Filter）和拦截器（Interceptor）都是用于处理请求和响应的机制，但它们在实现方式、作用范围和适用场景上有所不同。
以下是详细的对比分析：

### 1. 过滤器（Filter）

#### 1.1 定义

- **过滤器**：过滤器是 Java Servlet 规范的一部分，用于在请求到达 Servlet 之前或响应返回客户端之前对请求和响应进行预处理或后处理。
- **实现**：通过实现 `javax.servlet.Filter` 接口来创建过滤器。

#### 1.2 工作流程

1. **请求到达**：客户端请求到达 Web 服务器。
2. **过滤器链**：请求通过过滤器链，每个过滤器可以对请求进行预处理。
3. **Servlet 处理**：请求到达目标 Servlet 进行处理。
4. **响应返回**：响应通过过滤器链，每个过滤器可以对响应进行后处理。
5. **客户端接收**：响应返回给客户端。

#### 1.3 优点

- **标准规范**：遵循 Java Servlet 规范，适用于所有基于 Servlet 的 Web 应用。
- **轻量级**：实现简单，性能开销小。
- **范围广**：可以处理所有进入 Web 应用的请求和响应。

#### 1.4 缺点

- **功能有限**：只能处理 HTTP 请求和响应，无法直接访问 Spring 的上下文或依赖注入。
- **配置复杂**：通常需要在 `web.xml` 或通过注解进行配置。

#### 1.5 适用业务场景

- **日志记录**：记录请求和响应的日志。
- **字符编码**：设置请求和响应的字符编码。
- **权限验证**：简单的权限验证和重定向。
- **压缩响应**：压缩响应内容以减少传输数据量。
- **跨域处理**：处理跨域请求。

### 2. 拦截器（Interceptor）

#### 2.1 定义

- **拦截器**：拦截器是 Spring 框架的一部分，用于在请求到达控制器（Controller）之前或响应返回视图之前对请求和响应进行预处理或后处理。
- **实现**：通过实现 `org.springframework.web.servlet.HandlerInterceptor` 接口来创建拦截器。

#### 2.2 工作流程

1. **请求到达**：客户端请求到达 Web 服务器。
2. **前置拦截**：请求通过拦截器链的前置拦截方法（`preHandle`），可以对请求进行预处理。
3. **控制器处理**：请求到达目标控制器进行处理。
4. **后置拦截**：控制器处理完成后，通过拦截器链的后置拦截方法（`postHandle`），可以对响应进行后处理。
5. **视图渲染**：响应返回给客户端之前，通过拦截器链的完成方法（`afterCompletion`），可以进行一些清理工作。
6. **客户端接收**：响应返回给客户端。

#### 2.3 优点

- **Spring 集成**：与 Spring 框架紧密集成，可以访问 Spring 的上下文和依赖注入。
- **功能丰富**：可以访问请求和响应对象，以及控制器和视图对象。
- **配置简单**：通过 Spring 的配置文件或注解进行配置，易于管理。

#### 2.4 缺点

- **依赖 Spring**：只能在 Spring 应用中使用，不适用于非 Spring 应用。
- **性能开销**：相比过滤器，拦截器的性能开销稍大，因为需要与 Spring 上下文交互。

#### 2.5 适用业务场景

- **权限验证**：复杂的权限验证和重定向。
- **日志记录**：记录请求和响应的日志。
- **性能监控**：监控请求处理时间。
- **国际化处理**：处理国际化和本地化。
- **事务管理**：在请求处理前后管理事务。
- **参数验证**：在请求到达控制器之前进行参数验证。

### 3. 对比总结

| 特性               | 过滤器（Filter）                                      | 拦截器（Interceptor）                                      |
|--------------------|-------------------------------------------------------|------------------------------------------------------------|
| **定义**           | Java Servlet 规范的一部分                               | Spring 框架的一部分                                        |
| **实现接口**       | `javax.servlet.Filter`                                  | `org.springframework.web.servlet.HandlerInterceptor`       |
| **作用范围**       | 处理所有进入 Web 应用的请求和响应                       | 处理进入控制器的请求和响应                                   |
| **访问上下文**     | 无法直接访问 Spring 上下文和依赖注入                    | 可以访问 Spring 上下文和依赖注入                             |
| **功能**           | 只能处理 HTTP 请求和响应                                | 可以访问请求、响应、控制器和视图对象                         |
| **配置方式**       | `web.xml` 或注解                                        | Spring 配置文件或注解                                        |
| **性能开销**       | 较小，不依赖 Spring 上下文                              | 较大，依赖 Spring 上下文                                     |
| **适用场景**       | 日志记录、字符编码、权限验证、压缩响应、跨域处理          | 权限验证、日志记录、性能监控、国际化处理、事务管理、参数验证 |

### 4. 示例代码

#### 过滤器示例

```java
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 前置处理
        System.out.println("Request URL: " + httpRequest.getRequestURL());

        // 继续处理请求
        chain.doFilter(request, response);

        // 后置处理
        System.out.println("Response Status: " + httpResponse.getStatus());
    }

    @Override
    public void destroy() {
        // 销毁操作
    }
}
```


**配置过滤器**：

```java
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFilter> loggingFilter(){
        FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MyFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
```


#### 拦截器示例

```java
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前置处理
        System.out.println("Request URL: " + request.getRequestURL());
        return true; // 继续处理请求
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 后置处理
        System.out.println("Response Status: " + response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 完成处理
        System.out.println("Request and Response processing completed");
    }
}
```


**配置拦截器**：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private MyInterceptor myInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor).addPathPatterns("/api/*");
    }
}
```


### 5. 总结

- **过滤器（Filter）**：
    - **标准规范**：遵循 Java Servlet 规范。
    - **轻量级**：实现简单，性能开销小。
    - **范围广**：处理所有进入 Web 应用的请求和响应。
    - **适用场景**：日志记录、字符编码、权限验证、压缩响应、跨域处理。

- **拦截器（Interceptor）**：
    - **Spring 集成**：与 Spring 框架紧密集成。
    - **功能丰富**：可以访问请求、响应、控制器和视图对象。
    - **配置简单**：通过 Spring 配置文件或注解进行配置。
    - **适用场景**：复杂的权限验证、日志记录、性能监控、国际化处理、事务管理、参数验证。

通过理解过滤器和拦截器的区别和适用场景，可以更好地选择合适的机制来处理请求和响应，提高应用程序的性能和安全性。
# 2.AOP
Spring AOP（面向切面编程）是一种强大的编程范式，用于在不修改现有代码的情况下，向应用程序中添加横切关注点（如日志记录、事务管理、安全性等）。
Spring AOP 通过代理机制实现这些功能。以下是 Spring AOP 的实现原理、使用的代理方式以及适用业务场景的详细分析。
## Spring AOP使用了拦截器来实现面向切面编程。
在Spring AOP中，当配置好切面（Aspect）、通知（Advice）等相关组件后，
Spring会在运行时创建代理对象。对于JDK动态代理，会使用`InvocationHandler`来拦截方法调用；
对于CGLIB代理，会使用`MethodInterceptor`来拦截方法调用。
这些拦截器会在目标方法执行前后，根据配置的通知类型，执行相应的增强逻辑，如前置通知、后置通知、环绕通知等。通过拦截器，Spring AOP能够将横切关注点（如日志记录、事务管理、权限验证等）与业务逻辑分离，实现代码的模块化和可维护性。
### 1. Spring AOP 实现原理
#### 1.1 代理模式
Spring AOP 使用代理模式来实现横切关注点的织入。代理模式允许在不修改目标对象的情况下，通过代理对象来拦截和增强目标对象的行为。
#### 1.2 动态代理
Spring AOP 主要使用动态代理来创建代理对象。动态代理可以在运行时生成代理对象，从而实现对目标对象的拦截和增强。
#### 1.3 切面（Aspect）
- **切面**：包含横切关注点的模块，如日志记录、事务管理等。
- **通知（Advice）**：定义在切面中的具体增强逻辑，如前置通知、后置通知、环绕通知等。
- **切入点（Pointcut）**：定义了通知应该应用到哪些连接点（Join Point），即哪些方法调用。
- **连接点（Join Point）**：程序执行过程中的具体点，如方法调用、异常抛出等。
- **目标对象（Target Object）**：被代理的对象。
- **织入（Weaving）**：将切面应用到目标对象的过程。
#### 1.4 代理对象的创建
- **JDK 动态代理**：适用于实现了接口的目标对象。
- **CGLIB 代理**：适用于没有实现接口的目标对象。
### 2. 使用的代理方式
Spring AOP 主要使用以下两种代理方式：
#### 2.1 JDK 动态代理
- **适用场景**：目标对象实现了至少一个接口。
- **实现方式**：使用 `java.lang.reflect.Proxy` 类生成代理对象。
- **优点**：
    - **性能较好**：基于接口的代理，性能相对较高。
    - **标准规范**：遵循 Java 标准规范。
- **缺点**：
    - **仅支持接口**：只能代理实现了接口的对象。
#### 2.2 CGLIB 代理
- **适用场景**：目标对象没有实现接口。
- **实现方式**：使用 CGLIB 库生成目标对象的子类作为代理对象。
- **优点**：
    - **支持无接口类**：可以代理没有实现接口的对象。
    - **灵活性高**：通过生成子类来实现代理。
- **缺点**：
    - **性能稍差**：基于子类的代理，性能相对较低。
    - **无法代理 `final` 方法**：CGLIB 无法代理 `final` 方法。
### 3. 通知（Advice）类型
Spring AOP 支持多种通知
- **前置通知（Before Advice）**：在目标方法执行之前执行。
- **后置通知（After Advice）**：在目标方法执行之后执行，无论方法是否抛出异常。
- **返回通知（After Returning Advice）**：在目标方法成功执行并返回结果之后执行。
- **异常通知（After Throwing Advice）**：在目标方法抛出异常后执行。
- **环绕通知（Around Advice）**：环绕目标方法的执行，可以在方法执行前后添加自定义
### 4. 切点（Pointcut）表达式
Spring AOP 使用切点表达式来定义通知应该应用到哪些连接点。常用的切点表达式语言是 AspectJ 表达式语言。
- **示例**：
  ```java
  @Pointcut("execution(* com.example.service.*.*(..))")
  public void serviceMethods() {}
  ```
### 5. 适用业务场景
#### 5.1 日志记录
- **描述**：在方法调用前后记录日志。
- **示例**：
  ```java
  @Aspect
  @Component
  public class LoggingAspect {
      @Before("execution(* com.example.service.*.*(..))")
      public void logBefore(JoinPoint joinPoint) {
          System.out.println("Before method: " + joinPoint.getSignature().getName());
      }
      @After("execution(* com.example.service.*.*(..))")
      public void logAfter(JoinPoint joinPoint) {
          System.out.println("After method: " + joinPoint.getSignature().getName());
      }
  }
  ```
#### 5.2 事务管理
- **描述**：在方法调用前后管理事务。
- **示例**：
  ```java
  @Aspect
  @Component
  public class TransactionAspect {
      @Around("execution(* com.example.service.*.*(..))")
      public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
          TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
          try {
              Object result = joinPoint.proceed();
              transactionManager.commit(status);
              return result;
          } catch (Exception e) {
              transactionManager.rollback(status);
              throw e;
          }
      }
  }
  ```
#### 5.3 安全性检查

- **描述**：在方法调用前进行权限验证。
- **示例**：
  ```java
  @Aspect
  @Component
  public class SecurityAspect {
      @Before("execution(* com.example.service.*.*(..))")
      public void checkSecurity(JoinPoint joinPoint) {
          if (!isUserAuthorized()) {
              throw new SecurityException("User not authorized");
          }
      }
      private boolean isUserAuthorized() {
          // 权限验证逻辑
          return true;
      }
  }
  ```
#### 5.4 性能监控
- **描述**：监控方法的执行时间。
- **示例**：
  ```java
  @Aspect
  @Component
  public class PerformanceAspect {
      @Around("execution(* com.example.service.*.*(..))")
      public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
          long startTime = System.currentTimeMillis();
          Object result = joinPoint.proceed();
          long endTime = System.currentTimeMillis();
          System.out.println("Method " + joinPoint.getSignature().getName() + " took " + (endTime - startTime) + "ms");
          return result;
      }
  }
  ```
#### 5.5 参数验证
- **描述**：在方法调用前进行参数验证。
- **示例**：
  ```java
  @Aspect
  @Component
  public class ValidationAspect {

      @Before("execution(* com.example.service.*.*(..)) && args(param)")
      public void validateParameter(String param) {
          if (param == null || param.isEmpty()) {
              throw new IllegalArgumentException("Parameter cannot be null or empty");
          }
      }
  }
  ```
### 6. 配置 Spring AOP
#### 6.1 启用 AOP 支持
在 Spring Boot 应用中，可以通过以下方式启用 AOP 支持：
- **注解方式**：
  ```java
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.context.annotation.EnableAspectJAutoProxy;
  @SpringBootApplication
  @EnableAspectJAutoProxy
  public class SpringAopApplication {
      public static void main(String[] args) {
          SpringApplication.run(SpringAopApplication.class, args);
      }
  }
  ```
- **XML 配置方式**：
  ```xml
  <aop:aspectj-autoproxy />
  ```
#### 6.2 定义切面
通过实现 `@Aspect` 注解来定义切面，并使用 `@Before`、`@After`、`@AfterReturning`、`@AfterThrowing` 和 `@Around` 注解来定义通知。
- **示例**：
  ```java
  import org.aspectj.lang.JoinPoint;
  import org.aspectj.lang.annotation.After;
  import org.aspectj.lang.annotation.Aspect;
  import org.aspectj.lang.annotation.Before;
  import org.springframework.stereotype.Component;
  @Aspect
  @Component
  public class LoggingAspect {
      @Before("execution(* com.example.service.*.*(..))")
      public void logBefore(JoinPoint joinPoint) {
          System.out.println("Before method: " + joinPoint.getSignature().getName());
      }
      @After("execution(* com.example.service.*.*(..))")
      public void logAfter(JoinPoint joinPoint) {
          System.out.println("After method: " + joinPoint.getSignature().getName());
      }
  }
  ```
### 7. 代理方式选择
Spring AOP 会根据目标对象是否实现了接口来选择代理方式：
- **实现了接口**：使用 JDK 动态代理。
- **未实现接口**：使用 CGLIB 代理。
#### 7.1 强制使用 CGLIB 代理
可以通过配置强制使用 CGLIB 代理：
- **注解方式**：
  ```java
  @EnableAspectJAutoProxy(proxyTargetClass = true)
  ```
- **XML 配置方式**：
  ```xml
  <aop:aspectj-autoproxy proxy-target-class="true" />
  ```
### 8. 总结
- **实现原理**：
    - 使用代理模式和动态代理机制。
    - 通过切面、通知、切入点等组件实现横切关注点的织入。
- **代理方式**：
    - **JDK 动态代理**：适用于实现了接口的目标对象。
    - **CGLIB 代理**：适用于没有实现接口的目标对象。
- **通知类型**：
    - 前置通知（Before Advice）
    - 后置通知（After Advice）
    - 返回通知（After Returning Advice）
    - 异常通知（After Throwing Advice）
    - 环绕通知（Around Advice）
- **适用业务场景**：
    - 日志记录
    - 事务管理
    - 安全性检查
    - 性能监控
    - 参数验证
### 9. 示例代码
#### 9.1 定义切面
```java
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getSignature().getName());
    }
    @After("execution(* com.example.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After method: " + joinPoint.getSignature().getName());
    }
}
```
#### 9.2 启用 AOP 支持
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringAopApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAopApplication.class, args);
    }
}
```
#### 9.3 定义目标对象
```java
import org.springframework.stereotype.Service;

@Service
public class MyService {

    public void doSomething() {
        System.out.println("Doing something in MyService");
    }
}
```
#### 9.4 测试 AOP
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private MyService myService;
    @Override
    public void run(String... args) throws Exception {
        myService.doSomething();
    }
}
```
### 10. 运行结果
启动 Spring Boot 应用程序后，控制台输出示例：
```
Before method: doSomething
Doing something in MyService
After method: doSomething
```
### 11. 注意事项
- **性能**：动态代理会带来一定的性能开销，特别是在高并发场景下。
- **代理对象**：目标对象通过代理对象进行调用，因此直接调用目标对象的方法不会触发 AOP 通知。
- **配置**：确保正确配置 AOP 支持，并选择合适的代理方式。

通过理解 Spring AOP 的实现原理、使用的代理方式以及适用业务场景，可以更好地利用 AOP 来实现横切关注点的分离和模块化开发。
3. IOC 循环依赖
   Spring IOC（Inversion of Control，控制反转）是 Spring 框架的核心概念之一，它通过依赖注入（Dependency Injection, DI）来管理对象的创建和依赖关系。
   以下是关于 Spring IOC 的概念以及如何解决循环依赖的详细解释。
### 1. Spring IOC 概念
#### 1.1 控制反转（Inversion of Control, IOC）
- **定义**：控制反转是一种设计原则，对象的创建和依赖关系的管理不再由对象本身负责，而是由外部容器（如 Spring 容器）来负责。
- **目的**：降低对象之间的耦合度，提高代码的可维护性和可测试性。
#### 1.2 依赖注入（Dependency Injection, DI）
- **定义**：依赖注入是 IOC 的一种实现方式，通过构造函数、Setter 方法或字段注入的方式，将对象的依赖关系从外部注入到对象中。
- **类型**：
    - **构造函数注入**：通过构造函数传递依赖。
    - **Setter 方法注入**：通过 Setter 方法传递依赖。
    - **字段注入**：通过字段直接注入依赖（不推荐，因为难以测试和维护）。

#### 1.3 Spring 容器

- **BeanFactory**：Spring 的基础容器，负责管理 Bean 的创建和依赖注入。
- **ApplicationContext**：扩展了 `BeanFactory`，提供了更多的企业级功能，如国际化、事件传播、AOP 等。

### 2. 如何解决循环依赖

循环依赖是指两个或多个 Bean 互相依赖，形成一个闭环。Spring 容器在处理循环依赖时会采取一些策略来解决这些问题。以下是 Spring 解决循环依赖的机制和方法：

#### 2.1 三种状态

Spring 在创建 Bean 时会将 Bean 分为三种状态：

- **Singleton Factory**：Bean 工厂状态，表示 Bean 正在创建中。
- **Singleton Object**：Bean 对象状态，表示 Bean 已经创建完成。
- **Early Singleton**：早期 Bean 对象状态，表示 Bean 已经实例化但尚未完成属性注入和初始化。

#### 2.2 解决循环依赖的步骤

Spring 主要通过以下步骤解决循环依赖：

1. **实例化 Bean**：
    - 创建 Bean 的实例，但不进行属性注入和初始化。

2. **放入 Singleton Factory**：
    - 将 Bean 的实例放入 `singletonFactories` 缓存中，以便在后续的依赖注入过程中使用。

3. **属性注入**：
    - 进行属性注入，如果遇到依赖的 Bean 也在创建中，则从 `singletonFactories` 缓存中获取早期 Bean 对象（Early Singleton）。

4. **完成初始化**：
    - 完成 Bean 的初始化过程，包括调用 `@PostConstruct` 方法和 `InitializingBean` 接口的 `afterPropertiesSet` 方法。

5. **放入 Singleton Object**：
    - 将完成初始化的 Bean 对象放入 `singletonObjects` 缓存中。

#### 2.3 支持的循环依赖类型

Spring 主要支持以下两种循环依赖类型：

- **构造器循环依赖**：
    - **描述**：两个 Bean 通过构造函数互相依赖。
    - **支持情况**：Spring 不支持构造器循环依赖，会抛出 `BeanCurrentlyInCreationException` 异常。
    - **原因**：构造器循环依赖无法通过提前暴露 Bean 实例来解决。

- **Setter 方法循环依赖**：
    - **描述**：两个 Bean 通过 Setter 方法互相依赖。
    - **支持情况**：Spring 支持 Setter 方法循环依赖。
    - **实现机制**：
        1. **实例化 Bean**：创建 Bean 的实例，但不进行属性注入和初始化。
        2. **放入 Singleton Factory**：将 Bean 的实例放入 `singletonFactories` 缓存中。
        3. **属性注入**：进行属性注入，如果遇到依赖的 Bean 也在创建中，则从 `singletonFactories` 缓存中获取早期 Bean 对象（Early Singleton）。
        4. **完成初始化**：完成 Bean 的初始化过程。
        5. **放入 Singleton Object**：将完成初始化的 Bean 对象放入 `singletonObjects` 缓存中。

### 3. 解决循环依赖的示例

#### 3.1 Setter 方法循环依赖

假设有两个 Bean `A` 和 `B`，它们通过 Setter 方法互相依赖。

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeanA {

    private BeanB beanB;

    @Autowired
    public void setBeanB(BeanB beanB) {
        this.beanB = beanB;
    }

    public void doSomething() {
        System.out.println("BeanA is doing something");
        beanB.doSomething();
    }
}

@Component
public class BeanB {

    private BeanA beanA;

    @Autowired
    public void setBeanA(BeanA beanA) {
        this.beanA = beanA;
    }

    public void doSomething() {
        System.out.println("BeanB is doing something");
        beanA.doSomething();
    }
}
```


#### 3.2 配置 Spring 容器

确保在 Spring Boot 应用中启用组件扫描。

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringIocApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringIocApplication.class, args);
        BeanA beanA = context.getBean(BeanA.class);
        beanA.doSomething();
    }
}
```


#### 3.3 运行结果

启动 Spring Boot 应用程序后，控制台输出示例：

```
BeanA is doing something
BeanB is doing something
BeanA is doing something
```


### 4. 解决循环依赖的关键点

- **提前暴露 Bean 实例**：通过 `singletonFactories` 缓存提前暴露 Bean 实例，允许在属性注入过程中获取早期 Bean 对象。
- **Setter 方法注入**：支持通过 Setter 方法注入依赖，避免构造器循环依赖的问题。
- **缓存机制**：
    - **singletonObjects**：存储已经完全初始化的 Bean 对象。
    - **earlySingletonObjects**：存储已经实例化但尚未完成属性注入和初始化的 Bean 对象。
    - **singletonFactories**：存储 Bean 的实例工厂，用于在属性注入过程中获取早期 Bean 对象。

### 5. 总结

#### Spring IOC 概念

- **控制反转（IOC）**：对象的创建和依赖关系管理由外部容器负责。
- **依赖注入（DI）**：通过构造函数、Setter 方法或字段注入的方式，将依赖关系从外部注入到对象中。
- **Spring 容器**：负责管理 Bean 的创建和依赖注入，包括 `BeanFactory` 和 `ApplicationContext`。

#### 解决循环依赖

- **支持的循环依赖类型**：
    - **Setter 方法循环依赖**：通过提前暴露 Bean 实例来解决。
    - **构造器循环依赖**：不支持，会抛出异常。

- **关键步骤**：
    1. 实例化 Bean。
    2. 放入 `singletonFactories` 缓存。
    3. 属性注入。
    4. 完成初始化。
    5. 放入 `singletonObjects` 缓存。

- **缓存机制**：
    - **singletonObjects**：存储已经完全初始化的 Bean 对象。
    - **earlySingletonObjects**：存储已经实例化但尚未完成属性注入和初始化的 Bean 对象。
    - **singletonFactories**：存储 Bean 的实例工厂，用于在属性注入过程中获取早期 Bean 对象。

### 6. 注意事项

- **避免构造器循环依赖**：尽量使用 Setter 方法或构造函数参数较少的方式来避免构造器循环依赖。
- **合理设计依赖关系**：通过合理的设计和重构，减少循环依赖的发生。
- **测试**：在开发过程中进行充分的测试，确保循环依赖问题得到解决。

通过理解 Spring IOC 的概念以及如何解决循环依赖，可以更好地利用 Spring 框架来管理对象的创建和依赖关系，提高代码的可维护性和可测试性。
