# 1.简介
## a.Netty是异步事件驱动的网络应用框架，可用快速开发网络应用
## b.Netty对TCP和UDP进行了高度封装，同时保留了底层API的直接使用
## c.Netty屏蔽了底层通信协议开发的复杂性，提供了简单易用的API可供开发者专注于业务逻辑
## d.最流行的NIO框架，许多开元组件的底层RPC都是使用的Netty，如Dubbo，ES等
## 3.Spring Cloud Gateway 基于 Spring WebFlux 构建，而 Spring WebFlux 默认使用 Netty 作为服务器。 
# 2.特性
## a.设计方面
    i.对各种传输协议提供统一的API  根据参数来区分
    ii.高度可定制的线程模型-单线程，线程池
## b.易用性
    i.完善的Javadoc文档和示例代码
    ii.无需额外依赖
## c.性能
    i.更好的吞吐量，更低的延迟
    ii.更少的资源消耗
    iii.最小化的不必要的内存拷贝
## d.安全性
    i.完整的SSL和TLS支持
# 3.简单的Http服务器
    a.服务端代码
```java
package com.emqx;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
public class Main {
public static void main(String[] args) {
// 创建两个线程组
//  接收客户端请求
NioEventLoopGroup bossGroup = new NioEventLoopGroup();
//  处理IO请求
NioEventLoopGroup workerGroup = new NioEventLoopGroup();
//bossGroup 和 workerGroup 是两个线程池, 它们默认线程数为 CPU 核心数乘以 2

        //服务端启动辅助类 boostrap 用来为 Netty 程序的启动组装配置一些必须要组件
        //channel 方法用于指定服务器端监听套接字通道 NioServerSocketChannel，其内部管理了一个 Java NIO 中的ServerSocketChannel实例
        //channelHandler 方法用于设置业务职责链，责任链是我们下面要编写的，责任链具体是什么，它其实就是由一个个的 ChannelHandler 串联而成，形成的链式结构。正是这一个个的 ChannelHandler 帮我们完成了要处理的事情。
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //ddLast 方法将一个一个的 ChannelHandler 添加到责任链上并给它们取个名称（不取也可以，Netty 会给它个默认名称），这样就形成了链式结构。
                            // 在请求进来或者响应出去时都会经过链上这些 ChannelHandler 的处理。
                            //Netty 支持 Http（超文本传输协议），必须要给它提供相应的编解码器
                            //http编码处理器
                            pipeline.addLast(new HttpServerCodec());
                            //将多个http消息片段聚合成为完成的HTTP消息 最大长度为65536
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //自定义handles
                            pipeline.addLast(new HttpServerChannelHandler0());
                        }
                    });
            serverBootstrap.bind(8088).sync().channel().closeFuture().sync();
        }catch (Exception e){
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }}}
```

## b.自定义http处理器
```java
        package com.emqx;
        import io.netty.buffer.ByteBuf;
        import io.netty.buffer.Unpooled;
        import io.netty.channel.ChannelHandlerContext;
        import io.netty.channel.SimpleChannelInboundHandler;
        import io.netty.handler.codec.http.*;
        import io.netty.util.CharsetUtil;
        public class HttpServerChannelHandler0 extends SimpleChannelInboundHandler<HttpObject> {
        private HttpRequest request;
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
        request = (HttpRequest) msg;
        String uri = request.getUri();
        HttpMethod method = request.getMethod();
        System.out.printf("请求URI: " + uri + " 请求方法: " + method.name());
        }
        if (msg instanceof HttpContent) {
        HttpContent content = (HttpContent) msg;
        ByteBuf buf = content.content();
        System.out.printf("请求体: "+buf.toString(CharsetUtil.UTF_8));
        ByteBuf helloWorld = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, helloWorld);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH,helloWorld.readableBytes());
        ctx.writeAndFlush(response);
        }  }}
        c.客户端代码
        i.客户端启动类编写基本和服务端类似，在客户端我们只用到了一个线程池，服务端使用了两个，因为服务端要处理 n 条连接，而客户端相对来说只处理一条，因此一个线程池足以。
        ii.客户端代码
        public class HttpClient {
        public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
        Bootstrap b = new Bootstrap();
        b.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpClientHandler());
        }
        });
        // 启动客户端.
        ChannelFuture f = b.connect(host, port).sync();
        f.channel().closeFuture().sync();
        } finally {
        group.shutdownGracefully();
        }
        }
        }
        客户端处理器实现
        public class HttpClient {
        
        public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
        Bootstrap b = new Bootstrap();
        b.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpClientHandler());
        }
        });
        // 启动客户端.
        ChannelFuture f = b.connect(host, port).sync();
        f.channel().closeFuture().sync();
        
               } finally {
                   group.shutdownGracefully();
               }
        }
        }
```
- Channel、ChannelPipeline、ChannelHandler、ChannelHandlerContext 之间的关系
- channel 是框架自己定义的一个通道接口，
- Netty实现的客户端NIO套接字通道是NioSocketChannel,
- 提供的服务端NIO套接字通道是NioServerSocketChannel
- 当服务端和客户端建立一个新的连接时，一个新的Channel将被建立，同时会自动分配到它专属的ChannelPipeline
- ChannelPipeline是一个拦截流经channel的入站和出站事件的channelhandler实例链，ChannelHandle之间的交互是组成应用程序数据和事件处理逻辑的核心
-

![netty执行流程](/src/main/resources/images/nettyworkflow.png "可选标题")

上图描述了 IO 事件如何被一个 ChannelPipeline 的 ChannelHandler 处理的。
ChannelHandler分为 ChannelInBoundHandler 和 ChannelOutboundHandler 两种，如果一个入站 IO 事件被触发，这个事件会从第一个开始依次通过 ChannelPipeline中的 ChannelInBoundHandler，先添加的先执行。
若是一个出站 I/O 事件，则会从最后一个开始依次通过 ChannelPipeline 中的 ChannelOutboundHandler，后添加的先执行，然后通过调用在 ChannelHandlerContext 中定义的事件传播方法传递给最近的 ChannelHandler。
在 ChannelPipeline 传播事件时，它会测试 ChannelPipeline 中的下一个 ChannelHandler 的类型是否和事件的运动方向相匹配。
如果某个ChannelHandler不能处理则会跳过，并将事件传递到下一个ChannelHandler，直到它找到和该事件所期望的方向相匹配的为止。
假设我们创建下面这样一个 pipeline：
```java
ChannelPipeline p = ...;
p.addLast("1", new InboundHandlerA());
p.addLast("2", new InboundHandlerB());
p.addLast("3", new OutboundHandlerA());
p.addLast("4", new OutboundHandlerB());
p.addLast("5", new InboundOutboundHandlerX());
```
在上面示例代码中，inbound 开头的 handler 意味着它是一个ChannelInBoundHandler。outbound 开头的 handler 意味着它是一个 ChannelOutboundHandler。
当一个事件进入 inbound 时 handler 的顺序是 1，2，3，4，5；当一个事件进入 outbound 时，handler 的顺序是 5，4，3，2，1。在这个最高准则下，ChannelPipeline 跳过特定 ChannelHandler 的处理：
- 3，4 没有实现 ChannelInboundHandler，因而一个 inbound 事件的处理顺序是 1，2，5。
- 1，2 没有实现 ChannelOutBoundhandler，因而一个 outbound 事件的处理顺序是 5，4，3。
- 5 同时实现了 ChannelInboundHandler 和 channelOutBoundHandler，所以它同时可以处理 inbound 和 outbound 事件。
  ChannelHandler 可以通过添加、删除或者替换其他的 ChannelHandler 来实时地修改 ChannelPipeline 的布局。
  （它也可以将它自己从 ChannelPipeline 中移除。）这是 ChannelHandler 最重要的能力之一。
  ChannelHandlerContext 代表了 ChannelHandler 和 ChannelPipeline 之间的关联，每当有 ChannelHandler 添加到 ChannelPipeline 中时，都会创建 ChannelHandlerContext。
  ChannelHandlerContext 的主要功能是管理它所关联的 ChannelHandler 和在同一个 ChannelPipeline 中的其他 ChannelHandler 之间的交互。
- 事件从一个 ChannelHandler 到下一个 ChannelHandler 的移动是由 ChannelHandlerContext 上的调用完成的。
  ![netty执行流程](/src/main/resources/images/nettyhandle.png "可选标题")

```java
ChannelHandlerContext ctx = ...;   // 获得 ChannelHandlerContext引用
// write()将会把缓冲区发送到下一个ChannelHandler  
ctx.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));

//流经整个pipeline
ctx.channel().write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));
```
如果我们想有一些事件流全部通过 ChannelPipeline，有两个不同的方法可以做到：
- 调用 Channel 的方法
- 调用 ChannelPipeline 的方法
  那为什么你可能会需要在 ChannelPipeline 某个特定的位置开始传递事件呢？
- 减少因为让事件穿过那些对它不感兴趣的 ChannelHandler 而带来的开销
- 避免事件被那些可能对它感兴趣的 ChannlHandler 处理
  Netty 线程模型 Reactor
  Reactor 单线程模型
  eactor 单线程模型指的是所有的 IO 操作都在同一个 NIO 线程上面完成。作为 NIO 服务端接收客户端的 TCP 连接，作为 NIO 客户端向服务端发起 TCP 连接，读取通信对端的请求或向通信对端发送消息请求或者应答消息。
  由于 Reactor 模式使用的是异步非阻塞 IO，所有的 IO 操作都不会导致阻塞，理论上一个线程可以独立处理所有 IO 相关的操作。


在实例化 NioEventLoopGroup 时，构造器参数是 1，表示 NioEventLoopGroup 的线程池大小是 1。然后接着我们调用 b.group(bossGroup) 设置了服务器端的 EventLoopGroup，因此 bossGroup和 workerGroup 就是同一个 NioEventLoopGroup 了。
Reactor 多线程模型
对于一些小容量应用场景，可以使用单线程模型，但是对于高负载、大并发的应用却不合适，需要对该模型进行改进，演进为 Reactor 多线程模型。
Rector 多线程模型与单线程模型最大的区别就是有一组 NIO 线程处理 IO 操作。
在该模型中有专门一个 NIO 线程 -Acceptor 线程用于监听服务端，接收客户端的 TCP 连接请求；而 1 个 NIO 线程可以同时处理N条链路，但是 1 个链路只对应 1 个 NIO 线程，防止发生并发操作问题。
网络 IO 操作-读、写等由一个 NIO 线程池负责，线程池可以采用标准的 JDK 线程池实现，它包含一个任务队列和 N 个可用的线程，由这些 NIO 线程负责消息的读取、解码、编码和发送。



EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup();
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
.channel(NioServerSocketChannel.class)
...bossGroup 中只有一个线程，而 workerGroup 中的线程是 CPU 核心数乘以 2，
那么就对应 Recator 的多线程模型。
主从 Reactor 多线程模型
在并发极高的情况单独一个 Acceptor 线程可能会存在性能不足问题，为了解决性能问题，产生主从 Reactor 多线程模型。
主从 Reactor 线程模型的特点是：服务端用于接收客户端连接的不再是 1 个单独的 NIO 线程，而是一个独立的 NIO 线程池。
Acceptor 接收到客户端 TCP 连接请求处理完成后，将新创建的 SocketChannel 注册到 IO 线程池（sub reactor 线程池）的某个 IO 线程上，由它负责 SocketChannel 的读写和编解码工作。
Acceptor 线程池仅仅只用于客户端的登陆、握手和安全认证，一旦链路建立成功，就将链路注册到后端 subReactor 线程池的 IO 线程上，由 IO 线程负责后续的 IO 操作。

根据前面所讲的两个线程模型，很容想到 Netty 实现多线程的方式如下：
EventLoopGroup bossGroup = new NioEventLoopGroup(4);
EventLoopGroup workerGroup = new NioEventLoopGroup();
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
.channel(NioServerSocketChannel.class)
...
Netty面试题
以下为你提供一些常见的 Netty 面试题，涵盖基础概念、原理、
使用场景等多个方面，同时附上参考答案，助你更好地应对面试。
、、
### 基础概念类
#### 1. 什么是 Netty？它有什么作用？
Netty 是一个基于 Java NIO 的高性能网络编程框架，用于快速开发可维护、高性能、高并发的网络服务器和客户端程序。其作用包括：
- 简化网络编程：封装了 Java NIO 的复杂操作，提供了简单易用的 API。
- 提高性能：采用异步非阻塞 I/O 模型、高效的线程模型和零拷贝技术，提升了系统的并发处理能力和数据传输效率。
- 支持多种协议：可以方便地实现 TCP、UDP、HTTP 等多种网络协议。
#### 2. Netty 有哪些主要组件？
Netty 的主要组件有：
- `Channel`：表示网络连接的通道，用于数据的读写操作。
- `EventLoop`：负责处理 I/O 事件和任务调度，一个 `EventLoop` 可以管理多个 `Channel`。
- `ChannelFuture`：表示异步操作的结果，通过它可以监听操作的完成状态。
- `ChannelHandler`：处理 `Channel` 上的 I/O 事件和数据，分为 `ChannelInboundHandler` 和 `ChannelOutboundHandler`。
- `ChannelPipeline`：`ChannelHandler` 的容器，负责管理和调用 `ChannelHandler`。
- `ByteBuf`：Netty 提供的字节缓冲区，用于高效地处理字节数据。
### 原理机制类
#### 1. 简述 Netty 的线程模型。
Netty 主要有三种线程模型：
- **单线程模型**：所有的 I/O 操作都由一个线程完成，包括接受客户端连接、读写数据等。这种模型适用于小型应用或测试环境，但在高并发场景下性能较差。
- **多线程模型**：有一个专门的线程负责接受客户端连接，而读写操作由多个线程池中的线程完成。这种模型提高了并发处理能力
  但当连接数过多时，接受连接的线程可能会成为瓶颈。
- **主从多线程模型**：有一个主 `EventLoopGroup` 负责接受客户端连接，
  将连接注册到从 `EventLoopGroup` 中的一个 `EventLoop` 上，由从 `EventLoopGroup` 中的线程负责后续的读写操作。这种模型充分利用了多核 CPU 的优势，适用于高并发场景。
#### 2. Netty 是如何实现异步非阻塞 I/O 的？
Netty 基于 Java NIO 的 `Selector` 机制实现异步非阻塞 I/O：
- `EventLoop` 不断轮询 `Selector`，检查是否有 I/O 事件发生。
- 当有新的连接到来或数据可读/可写时，`Selector` 会通知 `EventLoop`。
- `EventLoop` 会将对应的 I/O 事件分发给注册的 `ChannelHandler`
  进行处理。
- 在处理 I/O 操作时，Netty 采用异步方式，即线程不会阻塞等待操作完成，
  而是可以继续处理其他任务，操作完成后通过 `ChannelFuture` 通知结果。

### 使用场景与实践类
#### 1. 什么场景下适合使用 Netty？
适合使用 Netty 的场景包括：
- **高性能网络服务器**：如游戏服务器、聊天服务器、文件传输服务器等，需要处理大量并发连接和高吞吐量的数据传输。
- **RPC 框架**：像 Dubbo 这样的 RPC 框架，利用 Netty 实现高效的远程服务调用。
- **实时数据处理系统**：如金融交易系统、实时监控系统等，对数据的实时性和处理速度要求较高。

#### 2. 如何在 Netty 中处理粘包和拆包问题？
Netty 提供了多种方式处理粘包和拆包问题：
- **固定长度解码器**：`FixedLengthFrameDecoder`，将字节流按照固定长度进行拆分。
- **行解码器**：`LineBasedFrameDecoder`，以换行符作为分隔符进行拆包。
- **分隔符解码器**：`DelimiterBasedFrameDecoder`，可以自定义分隔符进行拆包。
- **长度域解码器**：`LengthFieldBasedFrameDecoder`，通过指定长度字段的位置和长度来确定消息的边界。

### 优化与问题解决类
#### 1. 如何优化 Netty 应用的性能？
可以从以下几个方面优化 Netty 应用的性能：
- **合理配置线程模型**：根据应用的并发需求选择合适的线程模型，充分利用多核 CPU 的资源。
- **调整缓冲区大小**：根据实际业务需求调整 `ByteBuf` 的大小，避免缓冲区过小导致频繁的内存分配和复制，或过大造成内存浪费。
- **使用零拷贝技术**：利用 Netty 的零拷贝功能，减少数据在用户空间和内核空间之间的复制次数。
- **优化 `ChannelHandler` 链**：避免在 `ChannelHandler` 中进行耗时操作，将耗时任务异步处理。

#### 2. 遇到 Netty 内存泄漏问题如何排查和解决？
排查和解决 Netty 内存泄漏问题的步骤如下：
- **启用内存泄漏检测**：在启动时设置 `ResourceLeakDetector.level` 为 `ADVANCED` 或 `PARANOID` 级别，以便更详细地检测内存泄漏。
- **分析日志信息**：当检测到内存泄漏时，Netty 会输出详细的日志信息，包括泄漏对象的创建位置和引用链，根据这些信息定位泄漏点。
- **检查 `ByteBuf` 使用**：`ByteBuf` 是常见的内存泄漏源，确保在使用完 `ByteBuf` 后及时调用 `release()` 方法释放内存。
- **使用工具辅助分析**：可以使用 VisualVM、YourKit 等内存分析工具，分析堆内存快照，找出内存泄漏的对象和原因。 
