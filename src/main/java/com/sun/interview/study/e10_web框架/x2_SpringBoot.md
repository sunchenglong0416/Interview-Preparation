# SpringBoot启动过程
Spring Boot 的启动过程是一个复杂但高度自动化的流程，旨在简化 Spring 应用程序的配置和部署。
以下是 Spring Boot 启动过程的详细步骤和关键点：
```
1. 启动类调用 SpringApplication.run()
2. 创建 SpringApplication 实例
3. 准备环境 (Environment)
4. 创建 ApplicationContext
5. 准备 ApplicationContext
   - 注册 BeanDefinition
   - 加载 ApplicationContextInitializer
   - 加载 ApplicationListener
6. 刷新 ApplicationContext
   - 实例化单例 Bean
   - 启动嵌入式 Web 服务器
   - 发布应用程序事件
7. 启动完成
   - 输出启动信息
   - 执行 CommandLineRunner 和 ApplicationRunner
```
### 1. 启动类
Spring Boot 应用程序通常从一个带有 `@SpringBootApplication` 注解的主类开始。
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}
```
### 2. 启动过程详细步骤
#### 2.1 启动 `SpringApplication`
- **创建 `SpringApplication` 实例**：
  ```java
  SpringApplication app = new SpringApplication(SpringBootDemoApplication.class);
  ```
- **配置 `SpringApplication`**：
    - 自动推断应用程序的类型（Servlet、Reactive 等）。
    - 加载 `ApplicationContextInitializer` 和 `ApplicationListener`。
    - 配置默认的 Web 应用程序类型。
#### 2.2 运行 `SpringApplication`
- **调用 `run` 方法**：
  ```java
  app.run(args);
  ```
- **启动日志**：
    - 输出启动日志，包括版本信息、配置信息等。
#### 2.3 准备环境
- **创建 `Environment`**：
    - 根据应用程序类型创建 `Environment`（如 `StandardServletEnvironment`）。
    - 加载配置文件（如 `application.properties` 或 `application.yml`）。
- **配置属性**：
    - 读取配置文件中的属性，并将其加载到 `Environment` 中。
#### 2.4 创建 `ApplicationContext`
- **根据应用程序类型创建 `ApplicationContext`**：
    - **Servlet 应用**：创建 `AnnotationConfigServletWebServerApplicationContext`。
    - **Reactive 应用**：创建 `AnnotationConfigReactiveWebServerApplicationContext`。
    - **非 Web 应用**：创建 `AnnotationConfigApplicationContext`。
#### 2.5 准备 `ApplicationContext`
- **注册 `BeanDefinition`**：
    - 通过 `@ComponentScan` 扫描并注册组件（如 `@Component`, `@Service`, `@Repository`, `@Controller` 等）。
    - 注册配置类（如带有 `@Configuration` 注解的类）。
- **加载 `ApplicationContextInitializer`**：
    - 加载并应用 `ApplicationContextInitializer`，用于在 `ApplicationContext` 初始化之前进行一些自定义配置。

- **加载 `ApplicationListener`**：
    - 加载并注册 `ApplicationListener`，用于监听应用程序事件。

#### 2.6 刷新 `ApplicationContext`

- **实例化单例 Bean**：
    - 创建并初始化所有单例 Bean。
    - 处理 Bean 的依赖注入和初始化回调（如 `@PostConstruct`）。

- **启动嵌入式 Web 服务器**：
    - 如果是 Web 应用程序，启动嵌入式 Web 服务器（如 Tomcat, Jetty, Undertow）。
    - 配置和启动 Web 服务器，监听指定的端口。

- **发布应用程序事件**：
    - 发布各种应用程序事件（如 `ApplicationStartedEvent`, `ApplicationReadyEvent`）。
    - 通知 `ApplicationListener` 处理这些事件。

#### 2.7 启动完成

- **输出启动信息**：
    - 输出启动完成信息，包括应用程序的上下文路径、端口号等。

- **执行 `CommandLineRunner` 和 `ApplicationRunner`**：
    - 执行所有 `CommandLineRunner` 和 `ApplicationRunner` 实例，用于在应用程序启动后执行一些初始化任务。

### 3. 关键点

#### 3.1 自动配置

- **`@SpringBootApplication` 注解**：
    - 包含 `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan` 三个注解。
    - `@EnableAutoConfiguration`：启用自动配置，根据类路径中的依赖自动配置 Spring 应用程序。

- **自动配置类**：
    - 通过 `spring.factories` 文件中的 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 键加载自动配置类。
    - 根据条件注解（如 `@ConditionalOnClass`, `@ConditionalOnMissingBean`）决定是否启用某个自动配置。

#### 3.2 条件注解

- **条件注解**：
    - 用于控制自动配置类的启用条件。
    - 常见的条件注解包括 `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`, `@ConditionalOnWebApplication` 等。

#### 3.3 嵌入式 Web 服务器

- **嵌入式 Web 服务器**：
    - Spring Boot 支持多种嵌入式 Web 服务器，如 Tomcat, Jetty, Undertow。
    - 通过 `spring-boot-starter-web` 或 `spring-boot-starter-reactor-netty` 等依赖自动配置嵌入式 Web 服务器。

#### 3.4 应用程序事件

- **应用程序事件**：
    - Spring Boot 在启动过程中会发布多种应用程序事件。
    - 常见的事件包括 `ApplicationStartingEvent`, `ApplicationEnvironmentPreparedEvent`, `ApplicationPreparedEvent`, `ApplicationStartedEvent`, `ApplicationReadyEvent`, `ApplicationFailedEvent` 等。

- **监听应用程序事件**：
    - 通过实现 `ApplicationListener` 接口或使用 `@EventListener` 注解来监听和处理应用程序事件。

#### 3.5 命令行参数

- **命令行参数**：
    - 通过 `SpringApplication.run(args)` 方法传递命令行参数。
    - 命令行参数可以覆盖配置文件中的属性。

#### 3.6 自定义配置

- **自定义配置**：
    - 通过 `@Configuration` 注解定义自定义配置类。
    - 通过 `@Bean` 注解定义 Bean。

### 4. 启动过程示意图

以下是一个简化的 Spring Boot 启动过程示意图：


### 5. 示例代码

以下是一个简单的 Spring Boot 应用程序启动过程的示例代码：

#### 5.1 启动类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}
```


#### 5.2 自定义配置类

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
```


#### 5.3 自定义 Bean

```java
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class MyBean {

    @PostConstruct
    public void init() {
        System.out.println("MyBean initialized");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("MyBean destroyed");
    }
}
```


#### 5.4 监听应用程序事件

```java
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("Application is ready");
    }
}
```


### 6. 启动日志

启动 Spring Boot 应用程序时，控制台会输出详细的启动日志，包括：

- **版本信息**：
  ```
  .   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/
:: Spring Boot ::                (v2.7.5)
  ```


- **配置信息**：
  ```
2023-10-10 12:34:56.789  INFO 12345 --- [           main] c.e.s.SpringBootDemoApplication          : Starting SpringBootDemoApplication using Java 17 on my-machine with PID 12345
2023-10-10 12:34:56.790  INFO 12345 --- [           main] c.e.s.SpringBootDemoApplication          : No active profile set, falling back to default profiles: default
  ```


- **自动配置报告**：
  ```
2023-10-10 12:34:57.890  INFO 12345 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2023-10-10 12:34:57.900  INFO 12345 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 10 ms. Found 0 JPA repository interfaces.
  ```


- **嵌入式 Web 服务器启动**：
  ```
2023-10-10 12:34:58.000  INFO 12345 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2023-10-10 12:34:58.010  INFO 12345 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-10-10 12:34:58.010  INFO 12345 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.65]
  ```


- **应用程序事件**：
  ```
2023-10-10 12:34:58.100  INFO 12345 --- [           main] c.e.s.MyApplicationListener              : Application is ready
  ```


- **启动完成**：
  ```
2023-10-10 12:34:58.110  INFO 12345 --- [           main] c.e.s.SpringBootDemoApplication          : Started SpringBootDemoApplication in 1.234 seconds (JVM running for 1.567)
  ```


### 7. 总结

- **启动类**：通过 `@SpringBootApplication` 注解定义主类。
- **创建 `SpringApplication` 实例**：初始化 `SpringApplication` 实例，配置环境和 `ApplicationContext`。
- **准备环境**：加载配置文件，设置属性。
- **创建 `ApplicationContext`**：根据应用程序类型创建相应的 `ApplicationContext`。
- **准备 `ApplicationContext`**：注册 Bean 定义，加载初始化器和监听器。
- **刷新 `ApplicationContext`**：实例化单例 Bean，启动嵌入式 Web 服务器，发布应用程序事件。
- **启动完成**：输出启动信息，执行 `CommandLineRunner` 和 `ApplicationRunner`。

通过理解 Spring Boot 的启动过程，可以更好地掌握其内部机制，优化应用程序的配置和性能。
