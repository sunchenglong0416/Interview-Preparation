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


# 常用注解
|注解类型|注解名称|作用|使用示例|对比说明|
| ---- | ---- | ---- | ---- | ---- |
|组件扫描与注册|`@Component`|通用注解，用于标记一个类为 Spring 组件，可被 Spring 自动扫描并注册为 Bean|```java<br>@Component<br>public class MyComponent {<br>    // 类的具体实现<br>}<br>```|是其他特定组件注解的基础，适用于无明确分层的组件场景|
|组件扫描与注册|`@Repository`|标记数据访问层（DAO 层）组件，是 `@Component` 的特殊化，Spring 会对其进行特定异常转换|```java<br>@Repository<br>public class UserRepository {<br>    // 数据访问方法<br>}<br>```|用于明确数据访问职责，增强了代码语义，便于维护和异常处理|
|组件扫描与注册|`@Service`|标记服务层组件，是 `@Component` 的特殊化，用于表示业务逻辑层的类|```java<br>@Service<br>public class UserService {<br>    // 业务逻辑方法<br>}<br>```|突出业务逻辑处理，使代码结构更清晰|
|组件扫描与注册|`@Controller`|标记控制器层组件，是 `@Component` 的特殊化，在 Spring MVC 中处理 HTTP 请求|```java<br>@Controller<br>public class UserController {<br>    // 请求处理方法<br>}<br>```|专门用于 Web 控制器，处理用户请求|
|依赖注入|`@Autowired`|自动装配依赖的 Bean，默认按类型装配，有多个匹配时尝试按名称匹配，可用于构造函数、字段、方法|```java<br>@Service<br>public class UserService {<br>    @Autowired<br>    private UserRepository userRepository;<br>    // 其他方法<br>}<br>```|依赖注入的常用方式，使用方便，但可能导致依赖关系不清晰|
|依赖注入|`@Qualifier`|与 `@Autowired` 配合使用，当有多个相同类型的 Bean 时，通过指定 Bean 的名称来明确要注入的 Bean|```java<br>@Service<br>public class UserService {<br>    @Autowired<br>    @Qualifier("userRepositoryImpl")<br>    private UserRepository userRepository;<br>    // 其他方法<br>}<br>```|解决 `@Autowired` 按类型匹配时的歧义问题|
|依赖注入|`@Resource`|Java 标准注解，默认按名称装配，名称找不到时按类型装配，可用于字段和方法|```java<br>@Service<br>public class UserService {<br>    @Resource(name = "userRepositoryImpl")<br>    private UserRepository userRepository;<br>    // 其他方法<br>}<br>```|Java 标准，更具通用性，但在 Spring 环境中 `@Autowired` 可能更常用|
|配置类|`@Configuration`|标记一个类为配置类，相当于传统的 XML 配置文件，类中可以定义 Bean|```java<br>@Configuration<br>public class AppConfig {<br>    @Bean<br>    public UserService userService() {<br>        return new UserService();<br>    }<br>}<br>```|替代 XML 配置，使用 Java 代码进行配置，提高了代码的可读性和可维护性|
|配置类|`@Bean`|用于在配置类中定义 Bean，方法的返回值会被注册为 Bean|```java<br>@Configuration<br>public class AppConfig {<br>    @Bean<br>    public UserRepository userRepository() {<br>        return new UserRepository();<br>    }<br>}<br>```|与 `@Configuration` 配合使用，灵活定义 Bean|
|AOP|`@Aspect`|标记一个类为切面类，用于定义切面逻辑，如日志记录、事务管理等|```java<br>@Aspect<br>public class LoggingAspect {<br>    // 切面逻辑方法<br>}<br>```|用于实现面向切面编程，分离业务逻辑和横切关注点|
|AOP|`@Before`|AOP 前置通知注解，在目标方法执行前执行指定的切面逻辑|```java<br>@Aspect<br>public class LoggingAspect {<br>    @Before("execution(* com.example.service.*.*(..))")<br>    public void beforeAdvice() {<br>        System.out.println("Before method execution");<br>    }<br>}<br>```|在目标方法前执行特定操作，如日志记录、权限验证|
|AOP|`@After`|AOP 后置通知注解，在目标方法执行后执行指定的切面逻辑，无论目标方法是否抛出异常|```java<br>@Aspect<br>public class LoggingAspect {<br>    @After("execution(* com.example.service.*.*(..))")<br>    public void afterAdvice() {<br>        System.out.println("After method execution");<br>    }<br>}<br>```|在目标方法后执行操作，如资源释放|
|AOP|`@Around`|AOP 环绕通知注解，可在目标方法执行前后进行增强，控制目标方法的执行|```java<br>@Aspect<br>public class LoggingAspect {<br>    @Around("execution(* com.example.service.*.*(..))")<br>    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {<br>        System.out.println("Before method execution");<br>        Object result = pjp.proceed();<br>        System.out.println("After method execution");<br>        return result;<br>    }<br>}<br>```|功能最强大，可完全控制目标方法执行流程，但使用相对复杂|
|事务管理|`@Transactional`|用于标记事务性方法或类，Spring 会自动管理事务的开启、提交和回滚|```java<br>@Service<br>@Transactional<br>public class UserService {<br>    // 事务性业务逻辑方法<br>}<br>```|简化事务管理，可设置事务的传播行为、隔离级别等属性|
|请求映射|`@RequestMapping`|用于映射 HTTP 请求到控制器的处理方法，可指定请求路径、请求方法等|```java<br>@Controller<br>public class UserController {<br>    @RequestMapping(value = "/users", method = RequestMethod.GET)<br>    public String getUsers() {<br>        // 处理请求的逻辑<br>        return "users";<br>    }<br>}<br>```|是请求映射的通用注解，可处理多种请求方法，在 Spring MVC 中广泛使用|
|请求映射|`@GetMapping`|`@RequestMapping` 的特定化注解，专门用于处理 HTTP GET 请求|```java<br>@Controller<br>public class UserController {<br>    @GetMapping("/users")<br>    public String getUsers() {<br>        // 处理请求的逻辑<br>        return "users";<br>    }<br>}<br>```|代码更简洁，语义更明确，推荐在处理 GET 请求时使用|
|请求映射|`@PostMapping`|`@RequestMapping` 的特定化注解，专门用于处理 HTTP POST 请求|```java<br>@Controller<br>public class UserController {<br>    @PostMapping("/users")<br>    public String createUser() {<br>        // 处理请求的逻辑<br>        return "success";<br>    }<br>}<br>```|代码更简洁，语义更明确，推荐在处理 POST 请求时使用|
|请求映射|`@PutMapping`|`@RequestMapping` 的特定化注解，专门用于处理 HTTP PUT 请求|```java<br>@Controller<br>public class UserController {<br>    @PutMapping("/users/{id}")<br>    public String updateUser() {<br>        // 处理请求的逻辑<br>        return "updated";<br>    }<br>}<br>```|代码更简洁，语义更明确，推荐在处理 PUT 请求时使用|
|请求映射|`@DeleteMapping`|`@RequestMapping` 的特定化注解，专门用于处理 HTTP DELETE 请求|```java<br>@Controller<br>public class UserController {<br>    @DeleteMapping("/users/{id}")<br>    public String deleteUser() {<br>        // 处理请求的逻辑<br>        return "deleted";<br>    }<br>}<br>```|代码更简洁，语义更明确，推荐在处理 DELETE 请求时使用|

# spring事务

在 Spring 中，`@Transactional` 是用于管理事务的核心注解，下面详细介绍其属性以及事务失效的常见场景。

### `@Transactional` 注解属性

#### 1. `propagation`（事务传播行为）
事务传播行为定义了在多个事务方法相互调用时，事务如何传播。常见的传播行为有：
- `Propagation.REQUIRED`：默认值。如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。
- `Propagation.SUPPORTS`：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务方式执行。
- `Propagation.MANDATORY`：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。
- `Propagation.REQUIRES_NEW`：无论当前是否存在事务，都会创建一个新的事务。如果当前存在事务，则将当前事务挂起。
- `Propagation.NOT_SUPPORTED`：以非事务方式执行操作。如果当前存在事务，则将当前事务挂起。
- `Propagation.NEVER`：以非事务方式执行操作。如果当前存在事务，则抛出异常。
- `Propagation.NESTED`：如果当前存在事务，则在嵌套事务内执行；如果当前没有事务，则创建一个新的事务。

#### 2. `isolation`（事务隔离级别）
事务隔离级别定义了一个事务对其他事务的可见性。常见的隔离级别有：
- `Isolation.DEFAULT`：使用数据库的默认隔离级别。
- `Isolation.READ_UNCOMMITTED`：最低的隔离级别，允许读取尚未提交的数据变更，可能会导致脏读、不可重复读和幻读问题。
- `Isolation.READ_COMMITTED`：允许读取已经提交的数据，避免了脏读，但可能会出现不可重复读和幻读问题。
- `Isolation.REPEATABLE_READ`：确保在同一个事务中多次读取同一数据的结果是一致的，避免了脏读和不可重复读，但可能会出现幻读问题。
- `Isolation.SERIALIZABLE`：最高的隔离级别，完全服从 ACID 原则，避免了脏读、不可重复读和幻读问题，但会影响并发性能。

#### 3. `timeout`（事务超时时间）
指定事务的最大执行时间，单位为秒。如果事务执行时间超过该时间，则会自动回滚。例如：`@Transactional(timeout = 5)` 表示事务的最大执行时间为 5 秒。

#### 4. `readOnly`（是否为只读事务）
指定事务是否为只读事务。对于只读事务，数据库可以进行一些优化，提高性能。例如：`@Transactional(readOnly = true)` 表示该事务为只读事务。

#### 5. `rollbackFor` 和 `noRollbackFor`
- `rollbackFor`：指定哪些异常发生时需要回滚事务。可以指定多个异常类，例如：`@Transactional(rollbackFor = {SQLException.class, MyException.class})`。
- `noRollbackFor`：指定哪些异常发生时不需要回滚事务。同样可以指定多个异常类，例如：`@Transactional(noRollbackFor = {MyCustomException.class})`。

### 事务失效的常见场景

#### 1. 方法不是 public 修饰
`@Transactional` 注解只能应用于 public 方法上。如果将其应用于非 public 方法，事务将失效。例如：
```java
class MyService {
    // 事务失效，因为方法不是 public
    @Transactional
    private void doSomething() {
        // 业务逻辑
    }
}
```

#### 2. 自调用问题
在同一个类中，一个方法调用另一个带有 `@Transactional` 注解的方法，事务会失效。这是因为 Spring 的事务是基于 AOP 代理实现的，自调用不会经过代理对象，从而无法触发事务管理。例如：
```java
@Service
public class MyService {
    public void outerMethod() {
        // 自调用，事务失效
        innerMethod();
    }

    @Transactional
    public void innerMethod() {
        // 业务逻辑
    }
}
```

#### 3. 异常被捕获但未抛出
如果在事务方法中捕获了异常但没有重新抛出，Spring 无法感知到异常，从而不会触发事务回滚。例如：
```java
@Service
public class MyService {
    @Transactional
    public void doSomething() {
        try {
            // 业务逻辑，可能会抛出异常
        } catch (Exception e) {
            // 捕获异常但未抛出
            e.printStackTrace();
        }
    }
}
```

#### 4. 异常类型不匹配
如果 `rollbackFor` 指定的异常类型与实际抛出的异常类型不匹配，事务不会回滚。例如：
```java
@Service
public class MyService {
    @Transactional(rollbackFor = SQLException.class)
    public void doSomething() throws MyCustomException {
        // 抛出的异常类型与 rollbackFor 指定的类型不匹配
        throw new MyCustomException();
    }
}
```

#### 5. 数据库不支持事务
如果使用的数据库不支持事务（如 MySQL 的 MyISAM 引擎），即使使用了 `@Transactional` 注解，事务也不会生效。建议使用支持事务的数据库引擎，如 MySQL 的 InnoDB 引擎。

#### 6. 未正确配置事务管理器
如果没有正确配置 Spring 的事务管理器，事务将无法正常工作。例如，在使用 JDBC 进行数据库操作时，需要配置 `DataSourceTransactionManager`。
```java
@Configuration
@EnableTransactionManagement
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        // 配置数据源
        return new DriverManagerDataSource("jdbc:mysql://localhost:3306/mydb", "root", "password");
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```



# 在 Spring 中，`@Transactional` 注解的 `readOnly` 属性设置为 `true` 时，往往能提升系统的性能和效率，下面从数据库层面、事务管理层面以及锁机制层面来详细分析其原因。

### 数据库层面
- **减少日志记录开销**：大多数数据库在执行写操作时，需要记录事务日志，用于保证数据的一致性和可恢复性。这些日志记录操作会带来额外的 I/O 开销。当将 `readOnly` 属性设置为 `true` 时，数据库知道该事务仅包含读操作，因此可以省略部分或全部的日志记录工作。例如在 MySQL 中，对于只读事务，它不会记录事务的变更到二进制日志（binlog）中，这减少了磁盘 I/O 操作，从而提升了效率。
- **优化查询执行计划**：数据库的查询优化器在处理只读事务时，可能会采用一些专门针对只读场景的优化策略。因为只读事务不会修改数据，数据库可以更自由地选择执行计划，例如可以使用更多的缓存数据，避免频繁地从磁盘读取数据。以 Oracle 数据库为例，它可能会为只读事务选择更高效的索引扫描方式，提高查询性能。

### 事务管理层面
- **简化事务管理流程**：在 Spring 框架中，事务管理涉及到一系列的操作，如事务的开启、提交、回滚等。对于只读事务，Spring 可以简化这些管理流程。由于只读事务不会对数据进行修改，因此不需要进行复杂的锁管理和冲突检测。Spring 可以更快地完成事务的开启和关闭操作，减少了事务管理的开销。
- **减少资源竞争**：在多事务并发执行的环境中，写事务可能会与其他事务产生资源竞争，例如对同一数据行的写锁竞争。而只读事务不需要获取写锁，只需要获取共享锁，这大大减少了事务之间的资源竞争。当大量的只读事务同时执行时，它们可以并行进行，提高了系统的并发性能。

### 锁机制层面
- **避免写锁开销**：在数据库中，写操作通常需要获取排他锁，以保证数据的一致性。排他锁会阻止其他事务对同一数据进行读写操作，从而可能导致事务的等待和阻塞。而只读事务只需要获取共享锁，多个只读事务可以同时持有共享锁，不会相互阻塞。这避免了写锁带来的开销，提高了系统的并发处理能力。
- **减少死锁风险**：死锁是多事务并发执行时可能出现的一种严重问题，通常是由于事务之间相互等待对方释放锁而导致的。只读事务由于不需要获取写锁，大大降低了死锁发生的概率。这样可以减少系统为处理死锁而进行的额外操作，如死锁检测和回滚事务，从而提升了系统的整体效率。

综上所述，将 `@Transactional` 的 `readOnly` 属性设置为 `true`，可以从数据库操作、事务管理和锁机制等多个方面减少系统开销，提高并发性能，从而显著提升系统的运行效率。不过，需要注意的是，只有在确保事务确实只包含读操作时，才应该使用 `readOnly=true`，否则可能会导致数据不一致等问题。 