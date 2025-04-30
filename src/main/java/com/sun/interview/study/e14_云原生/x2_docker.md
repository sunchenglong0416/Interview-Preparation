### Docker 简介

Docker 是一个开源的平台，用于自动化部署、扩展和管理应用程序。它使用容器化技术，将应用程序及其依赖打包成一个独立的、可移植的容器，确保应用程序在任何环境中都能一致运行。Docker 提供了一个轻量级、高效的虚拟化解决方案，广泛应用于开发、测试和生产环境。

### **核心组件**

1. **Docker Engine**
    - **功能**：Docker 的核心组件，负责构建、运行和管理容器。
    - **组成部分**：
        - **Docker Daemon**：后台进程，处理 Docker 对象（如镜像、容器、网络、卷）的生命周期。
        - **Docker CLI**：命令行工具，用户通过它与 Docker Daemon 进行交互。
        - **Docker Registry**：存储和分发 Docker 镜像的仓库，如 Docker Hub。

2. **Docker Images**
    - **功能**：只读模板，包含应用程序及其依赖。
    - **特点**：分层存储，共享底层镜像层以节省空间。
    - **示例**：`nginx:latest`

3. **Docker Containers**
    - **功能**：从镜像创建的可运行实例。
    - **特点**：隔离的运行环境，支持读写操作。
    - **示例**：运行一个 Nginx 容器。

4. **Docker Volumes**
    - **功能**：持久化存储数据，独立于容器生命周期。
    - **特点**：支持多种驱动（本地、网络、云存储）。
    - **示例**：将容器内的数据挂载到主机。

5. **Docker Networks**
    - **功能**：管理容器之间的网络通信。
    - **特点**：支持多种网络模式（桥接、主机、覆盖、macvlan）。
    - **示例**：创建自定义桥接网络。

6. **Docker Compose**
    - **功能**：定义和运行多容器 Docker 应用程序。
    - **特点**：使用 YAML 文件配置服务、网络和卷。
    - **示例**：定义一个包含 Web 和数据库服务的多容器应用。

### **常用命令**

以下是一些常用的 Docker 命令，涵盖了镜像管理、容器管理、网络管理、卷管理和 Docker Compose 的基本操作。

#### **1. 镜像管理**

- **拉取镜像**
  ```bash
  docker pull <image_name>:<tag>
  # 示例
  docker pull nginx:latest
  ```


- **列出本地镜像**
  ```bash
  docker images
  ```


- **删除镜像**
  ```bash
  docker rmi <image_id>
  # 示例
  docker rmi nginx:latest
  ```


- **构建镜像**
  ```bash
  docker build -t <image_name>:<tag> <path_to_dockerfile>
  # 示例
  docker build -t myapp:1.0 .
  ```


#### **2. 容器管理**

- **运行容器**
  ```bash
  docker run [options] <image_name>:<tag>
  # 示例
  docker run -d -p 8080:80 --name mynginx nginx:latest
  ```


- **列出正在运行的容器**
  ```bash
  docker ps
  ```


- **列出所有容器（包括停止的）**
  ```bash
  docker ps -a
  ```


- **停止容器**
  ```bash
  docker stop <container_id>
  # 示例
  docker stop mynginx
  ```


- **启动容器**
  ```bash
  docker start <container_id>
  # 示例
  docker start mynginx
  ```


- **删除容器**
  ```bash
  docker rm <container_id>
  # 示例
  docker rm mynginx
  ```


- **进入容器**
  ```bash
  docker exec -it <container_id> /bin/bash
  # 示例
  docker exec -it mynginx /bin/bash
  ```


- **查看容器日志**
  ```bash
  docker logs <container_id>
  # 示例
  docker logs mynginx
  ```


#### **3. 卷管理**

- **创建卷**
  ```bash
  docker volume create <volume_name>
  # 示例
  docker volume create mydata
  ```


- **列出卷**
  ```bash
  docker volume ls
  ```


- **删除卷**
  ```bash
  docker volume rm <volume_name>
  # 示例
  docker volume rm mydata
  ```


- **使用卷**
  ```bash
  docker run -v <volume_name>:/path/in/container <image_name>:<tag>
  # 示例
  docker run -v mydata:/var/www/html nginx:latest
  ```


#### **4. 网络管理**

- **创建网络**
  ```bash
  docker network create <network_name>
  # 示例
  docker network create mynetwork
  ```


- **列出网络**
  ```bash
  docker network ls
  ```


- **删除网络**
  ```bash
  docker network rm <network_name>
  # 示例
  docker network rm mynetwork
  ```


- **连接容器到网络**
  ```bash
  docker network connect <network_name> <container_id>
  # 示例
  docker network connect mynetwork mynginx
  ```


- **断开容器从网络**
  ```bash
  docker network disconnect <network_name> <container_id>
  # 示例
  docker network disconnect mynetwork mynginx
  ```


#### **5. Docker Compose**

- **安装 Docker Compose**
  ```bash
  sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  ```


- **创建 `docker-compose.yml` 文件**
  ```yaml
  version: '3'
  services:
    web:
      image: nginx:latest
      ports:
        - "8080:80"
      volumes:
        - mydata:/var/www/html
    db:
      image: mysql:5.7
      environment:
        MYSQL_ROOT_PASSWORD: example
  volumes:
    mydata:
  ```


- **启动服务**
  ```bash
  docker-compose up -d
  ```


- **停止服务**
  ```bash
  docker-compose down
  ```


- **查看服务状态**
  ```bash
  docker-compose ps
  ```


- **查看服务日志**
  ```bash
  docker-compose logs
  ```


### **示例：运行一个简单的 Nginx 容器**

1. **拉取 Nginx 镜像**
   ```bash
   docker pull nginx:latest
   ```


2. **运行 Nginx 容器**
   ```bash
   docker run -d -p 8080:80 --name mynginx nginx:latest
   ```


3. **访问 Nginx 服务**
    - 打开浏览器，访问 [http://localhost:8080](http://localhost:8080)。
    - 应该能看到 Nginx 的默认欢迎页面。

4. **查看容器日志**
   ```bash
   docker logs mynginx
   ```


5. **停止容器**
   ```bash
   docker stop mynginx
   ```


6. **删除容器**
   ```bash
   docker rm mynginx
   ```


### **总结**

- **核心组件**：Docker Engine、Images、Containers、Volumes、Networks、Docker Compose。
- **常用命令**：镜像管理、容器管理、卷管理、网络管理、Docker Compose。

通过以上介绍和常用命令，您可以快速上手使用 Docker，构建和管理容器化应用程序。Docker 的强大功能和灵活性使其成为现代 DevOps 工作流中的重要工具。