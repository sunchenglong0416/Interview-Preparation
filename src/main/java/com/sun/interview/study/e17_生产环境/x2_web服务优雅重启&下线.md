# Spring Boot 提供了内置的优雅关闭支持。可以通过配置文件或代码来启用和配置优雅关闭。
没错，从 Spring Boot 2.3 版本开始，Spring Boot 提供了内置的优雅关闭支持，能够让应用在关闭时妥善处理正在进行的请求。下面分别介绍通过配置文件和代码来启用与配置优雅关闭的方法。

### 通过配置文件启用和配置优雅关闭
在 Spring Boot 里，借助配置文件（`application.properties` 或者 `application.yml`）就能轻松启用并配置优雅关闭。

#### 在 `application.properties` 中配置
```properties
# 启用优雅关闭
server.shutdown=graceful
# 配置宽限期，单位为秒，这里设置为 30 秒
spring.lifecycle.timeout-per-shutdown-phase=30s
```

#### 在 `application.yml` 中配置
```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

#### 配置说明
- `server.shutdown=graceful`：此配置用于开启优雅关闭功能。开启后，当接收到关闭信号时，Spring Boot 应用会进入优雅关闭模式。
- `spring.lifecycle.timeout-per-shutdown-phase=30s`：这一配置定义了关闭阶段的超时时间。在宽限期内，应用会等待正在处理的请求完成，若超过这个时间仍有请求未完成，应用会强制关闭。

### 通过代码启用和配置优雅关闭
除了使用配置文件，也可以在代码里对优雅关闭进行启用和配置。

#### 代码解释
- `WebServerFactoryCustomizer<TomcatServletWebServerFactory>` 是一个用于定制嵌入式 Tomcat 服务器工厂的接口。
- 在 `customizer` 方法中，添加了一个连接器定制器，对协议处理器的 `connectionTimeout` 和 `gracefulShutdownTimeout` 进行设置。
- `connectionTimeout` 用于设置连接超时时间，`gracefulShutdownTimeout` 则是优雅关闭的超时时间，单位为毫秒，这里都设置成了 30 秒。
