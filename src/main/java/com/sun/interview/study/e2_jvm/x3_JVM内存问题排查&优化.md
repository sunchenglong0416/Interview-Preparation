# 内存溢出
    ### 内存溢出概念
内存溢出（Out of Memory，OOM）指的是程序在运行过程中，向操作系统请求的内存超出了系统所能分配的最大内存限制。在 Java 应用里，就是 JVM 无法为新创建的对象分配足够的内存空间，从而抛出 `OutOfMemoryError` 异常。

### 可能出现的原因
#### 堆内存方面
- **对象创建过多**：程序中存在大量的对象实例化操作，并且这些对象长时间存活，导致堆内存被快速耗尽。例如在一个循环中不断创建新对象，却没有及时释放。
```java
List<Object> list = new ArrayList<>();
while (true) {
    list.add(new Object());
}
```
- **内存泄漏**：某些对象已经不再被使用，但由于存在强引用关系，垃圾回收器无法回收这些对象所占用的内存，随着时间推移，内存占用不断增加，最终导致溢出。像静态集合类中存储了大量对象，且未及时清理。
```java
public class MemoryLeakExample {
    private static final List<Object> list = new ArrayList<>();
    public static void addObject(Object obj) {
        list.add(obj);
    }
}
```
- **堆内存设置过小**：JVM 的堆内存初始值和最大值设置不合理，无法满足程序运行时的内存需求。

#### 非堆内存方面
- **元空间（Metaspace）**：动态生成大量的类，如使用字节码生成框架（CGLIB、Byte Buddy 等）动态创建类，会使元空间不断被填充，最终导致元空间溢出。
- **直接内存**：通过 `ByteBuffer` 的 `allocateDirect()` 方法分配大量直接内存，而没有正确释放，会造成直接内存溢出。

#### 其他方面
- **线程过多**：每个线程都会占用一定的栈内存，如果创建了过多的线程，栈内存总和会超出限制，导致栈溢出。

### K8s 部署时自动保存 dump 文件
#### 方法一：使用 JVM 参数和脚本
可以在容器启动时设置 JVM 参数，让 JVM 在发生内存溢出时自动生成堆转储文件。同时编写一个脚本，将生成的 dump 文件保存到持久化存储中。
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: java-app-pod
spec:
  containers:
  - name: java-app
    image: your-java-app-image
    command: ["/bin/sh", "-c"]
    args:
      - "java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof -jar your-app.jar;
        if [ -f /tmp/heapdump.hprof ]; then
          cp /tmp/heapdump.hprof /mnt/persistent-storage/heapdump_$(date +%Y%m%d%H%M%S).hprof;
        fi"
    volumeMounts:
    - name: persistent-storage
      mountPath: /mnt/persistent-storage
  volumes:
  - name: persistent-storage
    persistentVolumeClaim:
      claimName: your-pvc-name
```

### dump 文件分析工具和技巧
#### 分析工具
- **Eclipse Memory Analyzer（MAT）**：功能强大，能帮助快速定位内存中的大对象、泄漏对象以及对象之间的引用关系。它提供了多种视图，如 `Histogram` 展示各类对象的数量和占用内存大小，`Dominator Tree` 显示最大的对象及其依赖关系。
- **VisualVM**：是 JDK 自带的可视化工具，可用于监控 Java 应用的性能和内存使用情况，也能对 dump 文件进行分析。它操作简单，适合初学者。
- **YourKit Java Profiler**：商业性能分析工具，不仅可以分析 dump 文件，还能实时监控 Java 应用的性能，提供详细的内存和 CPU 分析报告。

#### 分析技巧
- **查找大对象**：在工具中查看 `Histogram` 或 `Dominator Tree`，找出占用内存较多的对象，这些对象可能是导致内存溢出的关键。
- **分析对象引用链**：通过查看对象的引用链，确定对象为什么没有被垃圾回收。例如在 MAT 中使用 `Path to GC Roots` 功能查看对象到 GC Roots 的引用路径，判断是否存在强引用导致对象无法被回收。
- **对比不同时间的 dump 文件**：如果有不同时间点生成的多个 dump 文件，对比它们之间的差异，观察哪些对象在不断增加，从而更准确地定位内存问题。
- **关注特定的类或包**：根据应用程序的业务逻辑，重点关注一些可能存在问题的类或包，如与资源管理、缓存管理相关的类。
# 内存泄露

### 内存泄露概念
内存泄露指的是程序在运行过程中，由于某些原因导致部分已经不再使用的内存无法被操作系统或垃圾回收机制回收，随着程序的持续运行，这部分未被回收的内存会不断积累，最终可能会耗尽系统的可用内存资源，引发内存溢出错误，导致程序性能下降甚至崩溃。

### 可能出现的原因
#### 静态集合类持有对象引用
静态集合类（如 `static List`、`static Map` 等）的生命周期和应用程序一致，如果将对象放入静态集合中，并且在对象不再使用时没有从集合中移除，那么这些对象将一直被引用，无法被垃圾回收。例如：
```java
import java.util.ArrayList;
import java.util.List;

public class StaticCollectionMemoryLeak {
    private static final List<Object> staticList = new ArrayList<>();

    public static void addObject(Object obj) {
        staticList.add(obj);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Object obj = new Object();
            addObject(obj);
            // 后续没有从 staticList 移除 obj，obj 无法被回收
        }
    }
}
```

#### 未关闭的资源
像数据库连接、文件句柄、网络连接等资源，如果在使用完毕后没有正确关闭，会导致这些资源一直被占用，无法被回收。例如：
```java
import java.io.FileInputStream;
import java.io.IOException;

public class UnclosedResourceMemoryLeak {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("test.txt");
            // 使用 fis 进行操作，但没有关闭
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### 内部类持有外部类引用
非静态内部类会隐式持有外部类的引用，如果内部类的生命周期比外部类长，会导致外部类无法被回收。例如：
```java
public class OuterClass {
    private byte[] data = new byte[1024 * 1024];

    public class InnerClass {
        // 内部类操作
    }

    public InnerClass getInnerClass() {
        return new InnerClass();
    }

    public static void main(String[] args) {
        OuterClass outer = new OuterClass();
        InnerClass inner = outer.getInnerClass();
        outer = null;
        // 由于 inner 持有 outer 的引用，outer 无法被回收
    }
}
```

#### 缓存使用不当
如果缓存中的对象一直不清理，随着缓存数据的不断增加，会导致内存占用持续上升。

#### 事件监听器和回调函数未移除
在注册事件监听器或回调函数后，如果没有在合适的时机进行反注册，会导致被监听的对象无法被回收。

### 如何生成线程 dump 文件
#### Linux 系统
- **jstack 命令**：`jstack` 是 JDK 自带的工具，可用于生成 Java 进程的线程 dump 文件。首先通过 `ps -ef | grep java` 找到 Java 进程的 PID，然后使用 `jstack <pid> > thread_dump.txt` 命令将线程 dump 信息输出到 `thread_dump.txt` 文件中。
- **kill -3 命令**：向 Java 进程发送 `kill -3 <pid>` 信号，Java 进程会将线程 dump 信息输出到标准错误输出（通常是控制台日志）。可以通过重定向将输出保存到文件中。

#### Windows 系统
- **使用任务管理器**：在任务管理器中找到 Java 进程，右键选择“创建转储文件”，会生成一个 `.dmp` 文件。
- **jstack 命令**：和 Linux 系统类似，先找到 Java 进程的 PID，然后使用 `jstack <pid> > thread_dump.txt` 命令生成线程 dump 文件。

### dump 文件分析工具和技巧
#### 分析工具
- **VisualVM**：是 JDK 自带的可视化工具，它可以连接到正在运行的 Java 进程，也可以打开线程 dump 文件进行分析。提供了线程的状态、调用栈等信息，方便直观地查看线程的运行情况。
- **YourKit Java Profiler**：商业性能分析工具，功能强大，不仅可以分析线程 dump 文件，还能实时监控 Java 应用的性能，对线程的状态、锁竞争等进行详细分析。
- **FastThread.io**：在线工具，只需上传线程 dump 文件，就能快速分析并生成详细的报告，指出线程的状态、是否存在死锁等问题。

#### 分析技巧
- **查看线程状态**：重点关注处于 `BLOCKED`（阻塞）、`WAITING`（等待）、`TIMED_WAITING`（定时等待）状态的线程，分析它们等待的资源和原因。
- **查找死锁**：检查是否存在死锁情况，死锁通常表现为多个线程互相持有对方需要的锁，处于无限等待状态。可以通过工具的死锁检测功能或者手动分析线程的调用栈和锁持有情况来判断。
- **分析热点线程**：找出占用 CPU 时间较长的线程，查看它们的调用栈，确定是哪些方法或代码块消耗了大量的 CPU 资源。
- **对比不同时间的 dump 文件**：如果有多个不同时间点的线程 dump 文件，对比它们之间的差异，观察线程状态和调用栈的变化，有助于发现问题的发展趋势。 



# 发生内存溢出后，服务是否会重启取决于多种因素，以下是一些常见的情况：
- **自动重启机制**：如果应用程序所在的运行环境（如容器、服务器等）配置了自动重启策略，当检测到内存溢出导致进程崩溃时，可能会自动重启服务。例如，在Kubernetes中，可以通过设置 `restartPolicy` 来定义容器在出现故障（包括内存溢出导致的崩溃）时的重启策略，常见的策略有 `Always`（总是重启）、`OnFailure`（仅在容器失败时重启）等。
- **进程终止但无自动重启配置**：如果没有自动重启机制，当发生内存溢出时，Java虚拟机（JVM）会抛出 `OutOfMemoryError` 异常。如果这个异常没有被捕获和处理，那么当前的Java进程通常会终止，服务也就停止运行了，不会自动重启。
- **异常被捕获处理**：在某些情况下，开发人员可能会在代码中捕获 `OutOfMemoryError` 异常，并尝试进行一些恢复操作，如释放一些资源、进行内存整理等。如果这些操作能够成功执行，使程序恢复到正常状态，那么服务可能不会重启。但这种情况比较少见，因为内存溢出通常意味着系统资源已经严重不足，很难通过简单的处理来恢复。

一般来说，内存溢出是比较严重的问题，即使服务自动重启，也只是一种应急措施，关键是要及时排查和解决导致内存溢出的根本原因，以避免服务频繁出现问题，影响系统的稳定性和可用性。