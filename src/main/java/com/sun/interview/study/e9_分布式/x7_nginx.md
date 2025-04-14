### Nginx 介绍
Nginx 是一款轻量级的高性能 Web 服务器、反向代理服务器及电子邮件（IMAP/POP3）代理服务器，由 Igor Sysoev 开发。它因具有高并发处理能力、低内存消耗、丰富的模块库和灵活的配置等特点，在互联网领域被广泛应用。常用于处理静态资源、反向代理、负载均衡、限流等任务。

### 核心模块
- **ngx_http_core_module**：这是 HTTP 核心模块，是 Nginx 处理 HTTP 请求的基础模块。它定义了请求处理的基本流程，包括处理请求头、解析请求 URI、处理请求方法等，还负责配置全局的 HTTP 指令，如 `server`、`location` 块的定义等。
- **ngx_event_core_module**：事件核心模块，负责处理 Nginx 的事件驱动机制。Nginx 采用高效的事件驱动模型（如 epoll、kqueue 等），该模块负责管理这些事件，包括连接的建立、断开、读写事件等，使得 Nginx 能够高效地处理大量并发连接。
- **ngx_mail_core_module**：邮件核心模块，为 Nginx 提供邮件代理服务的基础功能。它支持 IMAP、POP3 和 SMTP 协议，可用于搭建邮件代理服务器，实现邮件的转发、过滤等功能。

### 工作线程数量设置
Nginx 的工作线程数量通常通过 `worker_processes` 和 `worker_connections` 这两个参数来进行配置。
- **worker_processes**：该参数指定了 Nginx 启动的工作进程数量。一般可将其设置为服务器的 CPU 核心数，这样能充分利用 CPU 资源。例如，对于一个 4 核 CPU 的服务器，可在 `nginx.conf` 中这样配置：
```plaintext
worker_processes 4;
```
- **worker_connections**：此参数定义了每个工作进程可以同时处理的最大连接数。需要结合 `worker_processes` 一起考虑，理论上 Nginx 可处理的最大并发连接数为 `worker_processes * worker_connections`。例如：
```plaintext
events {
    worker_connections 1024;
}
```

### 负载均衡策略
- **轮询（Round Robin）**：这是默认的负载均衡策略。Nginx 会按顺序将客户端请求依次分发到后端服务器，每个请求轮流分配给不同的服务器。例如：
```plaintext
upstream backend {
    server backend1.example.com;
    server backend2.example.com;
}
```
- **加权轮询（Weighted Round Robin）**：为每个后端服务器分配一个权重，权重越高的服务器接收的请求就越多。适用于不同性能的服务器组成的集群。例如：
```plaintext
upstream backend {
    server backend1.example.com weight=3;
    server backend2.example.com weight=1;
}
```
- **IP 哈希（IP Hash）**：根据客户端的 IP 地址进行哈希计算，将相同 IP 地址的客户端请求总是分发到同一台后端服务器。这样可以保证客户端的会话一致性。例如：
```plaintext
upstream backend {
    ip_hash;
    server backend1.example.com;
    server backend2.example.com;
}
```
- **最少连接（Least Connections）**：Nginx 会将请求分发到当前连接数最少的后端服务器，以平衡各服务器的负载。例如：
```plaintext
upstream backend {
    least_conn;
    server backend1.example.com;
    server backend2.example.com;
}
```

### 限流策略
- **基于请求速率限流**：使用 `ngx_http_limit_req_module` 模块可以对客户端的请求速率进行限制。例如，限制每个 IP 地址每秒最多只能发起 10 个请求：
```plaintext
http {
    limit_req_zone $binary_remote_addr zone=mylimit:10m rate=10r/s;
    server {
        location / {
            limit_req zone=mylimit;
        }
    }
}
```
- **基于连接数限流**：借助 `ngx_http_limit_conn_module` 模块能够对每个客户端的连接数进行限制。例如，限制每个 IP 地址最多只能同时建立 5 个连接：
```plaintext
http {
    limit_conn_zone $binary_remote_addr zone=perip:10m;
    server {
        location / {
            limit_conn perip 5;
        }
    }
}
``` 