- 线程定义
  线程是操作系统能够进行运算调度的最小单位，它被包含在进程之中，是进程中的实际运作单位。
  理解线程的定义、结构、生命周期、运行状态切换以及常用的方法对于编写高效的多线程程序至关重要。
  以下是详细的解释：
### 1. 线程的定义
- **定义**：线程是操作系统能够进行运算调度的最小单位，是进程中的一个实体，是被系统独立调度和分派的基本单位。
- **特点**：
    - **轻量级**：线程比进程更轻量级，创建和切换的开销较小。
    - **共享资源**：线程共享进程的资源（如内存、文件句柄等），但每个线程有自己的栈空间。
    - **并发执行**：多个线程可以并发执行，提高程序的执行效率。
### 2. 线程的结构
一个线程通常包含以下几个部分：
- **程序计数器（Program Counter, PC）**：保存当前线程执行的指令地址。
- **栈（Stack）**：存储线程的局部变量、方法调用栈帧等。
- **寄存器（Registers）**：存储线程的临时数据和状态信息。
- **线程状态（Thread State）**：表示线程当前的运行状态。
- **线程优先级（Thread Priority）**：影响线程调度的优先级。
- **线程ID（Thread ID）**：唯一标识一个线程。
### 3. 线程的生命周期
线程的生命周期可以分为以下几个状态：
1. **新建（New）**
    - **描述**：线程对象被创建但尚未启动。
    - **示例**：
      ```java
      Thread thread = new Thread(() -> {
          System.out.println("Thread is running");
      });
      ```
2. **可运行（Runnable）**
    - **描述**：线程已经启动，等待 CPU 调度。
    - **示例**：
      ```java
      thread.start(); // 线程进入可运行状态
      ```
3. **阻塞（Blocked）**
    - **描述**：线程因为等待监视器锁而被阻塞。
    - **示例**：
      ```java
      synchronized (lock) {
          // 线程进入阻塞状态，等待获取锁
      }
      ```
4. **等待（Waiting）**
    - **描述**：线程无限期等待另一个线程执行特定操作。
    - **示例**：
      ```java
      thread.wait(); // 线程进入等待状态
      ```
5. **超时等待（Timed Waiting）**
    - **描述**：线程在指定时间内等待另一个线程执行特定操作。
    - **示例**：
      ```java
      thread.sleep(1000); // 线程进入超时等待状态，1秒后自动恢复
      thread.join(1000); // 线程进入超时等待状态，等待另一个线程1秒
      ```
6. **终止（Terminated）**
    - **描述**：线程执行完毕或因异常终止。
    - **示例**：
      ```java
      thread.run(); // 线程执行完毕，进入终止状态
      ```
### 4. 线程状态切换
线程在不同状态之间切换的常见路径如下：
- **新建 → 可运行**：调用 `start()` 方法。
- **可运行 → 阻塞**：线程尝试获取被其他线程持有的锁。
- **阻塞 → 可运行**：持有锁的线程释放锁。
- **可运行 → 等待**：线程调用 `wait()` 方法。
- **等待 → 可运行**：其他线程调用 `notify()` 或 `notifyAll()` 方法。
- **可运行 → 超时等待**：线程调用 `sleep(long millis)`、`join(long millis)` 或 `wait(long timeout)` 方法。
- **超时等待 → 可运行**：超时时间到达或被其他线程唤醒。
- **可运行 → 终止**：线程执行完毕或因异常终止。
### 5. 常用的方法
#### 5.1 启动线程
- **方法**：`start()`
- **描述**：启动线程，使其进入可运行状态。
- **示例**：
  ```java
  Thread thread = new Thread(() -> {
      System.out.println("Thread is running");
  });
  thread.start(); // 线程从新建状态进入可运行状态
  ```
#### 5.2 线程睡眠
- **方法**：`sleep(long millis)`
- **描述**：使当前线程进入超时等待状态，指定时间后自动恢复。
- **示例**：
  ```java
  try {
      Thread.sleep(1000); // 线程进入超时等待状态，1秒后自动恢复
  } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
  }
  ```
#### 5.3 线程等待
- **方法**：`wait()`
- **描述**：使当前线程进入等待状态，直到其他线程调用 `notify()` 或 `notifyAll()` 方法。
- **示例**：
  ```java
  synchronized (lock) {
      try {
          lock.wait(); // 线程进入等待状态
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
      }
  }
  ```
#### 5.4 线程通知
- **方法**：`notify()`
- **描述**：唤醒一个等待的线程。
- **示例**：
  ```java
  synchronized (lock) {
      lock.notify(); // 唤醒一个等待的线程
  }
  ```
- **方法**：`notifyAll()`
- **描述**：唤醒所有等待的线程。
- **示例**：
  ```java
  synchronized (lock) {
      lock.notifyAll(); // 唤醒所有等待的线程
  }
  ```
#### 5.5 线程加入
- **方法**：`join()`
- **描述**：当前线程等待指定线程执行完毕。
- **示例**：
  ```java
  try {
      thread.join(); // 当前线程进入超时等待状态，直到 thread 执行完毕
  } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
  }
  ```
- **方法**：`join(long millis)`
- **描述**：当前线程等待指定线程执行完毕或超时。
- **示例**：
  ```java
  try {
      thread.join(1000); // 当前线程进入超时等待状态，等待 thread 1秒或执行完毕
  } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
  }
  ```
#### 5.6 线程中断
- **方法**：`interrupt()`
- **描述**：中断线程。
- **示例**：
  ```java
  thread.interrupt(); // 中断线程
  ```
- **方法**：`isInterrupted()`
- **描述**：检查线程是否被中断。
- **示例**：
  ```java
  if (Thread.currentThread().isInterrupted()) {
      System.out.println("Thread is interrupted");
  }
  ```
- **方法**：`interrupted()`
- **描述**：检查当前线程是否被中断，并清除中断状态。
- **示例**：
  ```java
  if (Thread.interrupted()) {
      System.out.println("Thread is interrupted");
  }
  ```
#### 5.7 线程优先级
- **方法**：`setPriority(int newPriority)`
- **描述**：设置线程的优先级。
- **示例**：
  ```java
  thread.setPriority(Thread.MAX_PRIORITY); // 设置线程优先级为最高
  ```
- **方法**：`getPriority()`
- **描述**：获取线程的优先级。
- **示例**：
  ```java
  int priority = thread.getPriority();
  ```
#### 5.8 线程名称
- **方法**：`setName(String name)`
- **描述**：设置线程的名称。
- **示例**：
  ```java
  thread.setName("MyThread");
  ```
- **方法**：`getName()`
- **描述**：获取线程的名称。
- **示例**：
  ```java
  String name = thread.getName();
  ```
#### 5.9 线程ID
- **方法**：`getId()`
- **描述**：获取线程的唯一标识符。
- **示例**：
  ```java
  long id = thread.getId();
  ```
### 6. 线程状态切换图
以下是一个简化的线程状态切换图：
```
[新建] --start()--> [可运行]
[可运行] --获取锁--> [阻塞]
[阻塞] --释放锁--> [可运行]
[可运行] --wait()--> [等待]
[等待] --notify()/notifyAll()--> [可运行]
[可运行] --sleep()/join()/wait(timeout)--> [超时等待]
[超时等待] --超时/唤醒--> [可运行]
[可运行] --执行完毕/异常--> [终止]
```
### 7. 示例代码
以下是一个综合示例，展示了线程的创建、启动、睡眠、等待、通知和中断：
```java
public class ThreadExample {
    private static final Object lock = new Object();
    private static boolean running = true;
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (running) {
                synchronized (lock) {
                    try {
                        System.out.println("Thread is running");
                        lock.wait(1000); // 线程进入超时等待状态，1秒后自动恢复
                    } catch (InterruptedException e) {
                        System.out.println("Thread interrupted");
                        Thread.currentThread().interrupt(); // 重新设置中断状态
                    }
                }
            }
            System.out.println("Thread is terminated");
        });

        thread.setName("MyThread");
        thread.setPriority(Thread.MAX_PRIORITY);
        System.out.println("Thread ID: " + thread.getId());
        System.out.println("Thread Name: " + thread.getName());
        System.out.println("Thread Priority: " + thread.getPriority())
        thread.start(); // 线程从新建状态进入可运行状态
        try {
            Thread.sleep(5000); // 主线程进入超时等待状态，5秒后自动恢复
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        synchronized (lock) {
            running = false;
            lock.notify(); // 唤醒等待的线程
        }
        try {
            thread.join(); // 主线程等待 thread 执行完毕
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Main thread is terminated");
    }
}
```
### 8. 总结
- **定义**：线程是操作系统能够进行运算调度的最小单位，是进程中的一个实体。
- **结构**：包括程序计数器、栈、寄存器、线程状态、线程优先级和线程ID。
- **生命周期**：包括新建、可运行、阻塞、等待、超时等待和终止。
- **状态切换**：通过调用不同的方法和条件来实现状态切换。
- **常用方法**：
    - `start()`：启动线程。
    - `sleep(long millis)`：使线程进入超时等待状态。
    - `wait()`：使线程进入等待状态。
    - `notify()` 和 `notifyAll()`：唤醒等待的线程。
    - `join()` 和 `join(long millis)`：等待指定线程执行完毕。
    - `interrupt()`：中断线程。
    - `isInterrupted()` 和 `interrupted()`：检查线程是否被中断。
    - `setPriority(int newPriority)` 和 `getPriority()`：设置和获取线程优先级。
    - `setName(String name)` 和 `getName()`：设置和获取线程名称。
    - `getId()`：获取线程ID。
      通过理解线程的定义、结构、生命周期、运行状态切换以及常用的方法，可以更好地管理和控制线程，编写高效的多线程程序。
- 创建线程的方法
- 继承 Thread 类
- 实现 Runnable 接口
- 实现 Callable 接口
- 线程池定义
  线程池是 Java 中用于管理和复用线程的一种机制，可以显著提高应用程序的性能和资源利用率。
  线程池通过预先创建一组线程，并将任务分配给这些线程来执行，避免了频繁创建和销毁线程的开销。
  以下是关于线程池的分类、定义、各项参数以及任务执行过程的详细解释。
### 1. 线程池的定义
- **定义**：线程池是一种线程管理机制，它维护一个线程集合，可以重复使用这些线程来执行任务，从而减少线程创建和销毁的开销，提高系统性能。
- **优点**：
    - **资源复用**：线程可以被复用，避免频繁创建和销毁线程。
    - **控制并发**：可以控制并发线程的数量，防止系统资源耗尽。
    - **提高响应速度**：任务到达时，线程已经存在，可以立即执行。
    - **管理方便**：提供了一系列管理线程的方法，便于监控和管理。
### 2. 线程池的分类
Java 提供了多种线程池实现，主要通过 `java.util.concurrent.Executors` 工具类创建。以下是常见的线程池分类：
1. **Fixed Thread Pool**
    - **描述**：创建一个固定大小的线程池，所有线程都在核心线程中。
    - **适用场景**：适用于负载较重的任务，且任务执行时间较短的场景。
2. **Cached Thread Pool**
    - **描述**：创建一个可缓存的线程池，线程池的大小会根据需要动态调整。
    - *适用场景**：适用于执行大量短生命周期的任务，且任务数量不确定的场景。
3. **Single Thread Executor**
    - **描述**：创建一个单线程的线程池，所有任务都在同一个线程中按顺序执行。
    - **适用场景**：适用于需要保证任务按顺序执行的场景。
4. **Scheduled Thread Pool**
    - **描述**：创建一个支持定时和周期性任务执行的线程池。
    - **适用场景**：适用于需要定时或周期性执行任务的场景
5. **Work Stealing Pool**
    - **描述**：创建一个工作窃取线程池，适用于多任务处理，线程可以从其他队列中窃取任务。
    - **适用场景**：适用于多任务处理，任务之间存在依赖关系的场景。

### 3. 线程池的参数

线程池的主要参数包括：

- **核心线程数（corePoolSize）**：线程池中保持活动状态的最小线程数。
- **最大线程数（maximumPoolSize）**：线程池中允许的最大线程数。
- **线程存活时间（keepAliveTime）**：当线程数超过核心线程数时，多余的空闲线程在终止前等待新任务的最长时间。
- **时间单位（unit）**：`keepAliveTime` 的时间单位。
- **任务队列（workQueue）**：用于保存等待执行任务的队列。
- **线程工厂（threadFactory）**：用于创建新线程的工厂。
- **拒绝策略（handler）**：当任务无法执行时的拒绝策略。

### 4. 常见的线程池参数

#### 4.1 Fixed Thread Pool

- **核心线程数**：等于最大线程数。
- **线程存活时间**：0（不适用）。
- **任务队列**：`LinkedBlockingQueue`，无界队列。
- **拒绝策略**：`AbortPolicy`（默认），直接抛出 `RejectedExecutionException`。

#### 4.2 Cached Thread Pool

- **核心线程数**：0。
- **最大线程数**：`Integer.MAX_VALUE`。
- **线程存活时间**：60秒。
- **任务队列**：`SynchronousQueue`，不存储元素的阻塞队列。
- **拒绝策略**：`AbortPolicy`（默认），直接抛出 `RejectedExecutionException`。

#### 4.3 Single Thread Executor

- **核心线程数**：1。
- **最大线程数**：1。
- **线程存活时间**：0（不适用）。
- **任务队列**：`LinkedBlockingQueue`，无界队列。
- **拒绝策略**：`AbortPolicy`（默认），直接抛出 `RejectedExecutionException`。

#### 4.4 Scheduled Thread Pool

- **核心线程数**：指定数量。
- **最大线程数**：`Integer.MAX_VALUE`。
- **线程存活时间**：0（不适用）。
- **任务队列**：`DelayedWorkQueue`，用于存储定时任务。
- **拒绝策略**：`AbortPolicy`（默认），直接抛出 `RejectedExecutionException`。

#### 4.5 Work Stealing Pool

- **核心线程数**：等于可用处理器的数量（`Runtime.getRuntime().availableProcessors()`）。
- **最大线程数**：`Integer.MAX_VALUE`。
- **线程存活时间**：60秒。
- **任务队列**：`ForkJoinPool.WorkQueue`，用于工作窃取。
- **拒绝策略**：`AbortPolicy`（默认），直接抛出 `RejectedExecutionException`。

### 5. 线程池的创建

通过 `Executors` 工具类可以方便地创建不同类型的线程池。

#### 5.1 Fixed Thread Pool

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // 创建一个固定大小的线程池，大小为5
        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            executor.execute(() -> {
                System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread().getName());
            });
        }
        executor.shutdown(); // 关闭线程池
    }
}
```


#### 5.2 Cached Thread Pool

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool(); // 创建一个可缓存的线程池
        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            executor.execute(() -> {
                System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread().getName());
            });
        }
        executor.shutdown(); // 关闭线程池
    }
}
```


#### 5.3 Single Thread Executor

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutorExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor(); // 创建一个单线程的线程池
        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            executor.execute(() -> {
                System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread().getName());
            });
        }
        executor.shutdown(); // 关闭线程池
    }
}
```


#### 5.4 Scheduled Thread Pool

```java
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExample {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5); // 创建一个支持定时和周期性任务的线程池
        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            executor.schedule(() -> {
                System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread().getName());
            }, 1, TimeUnit.SECONDS); // 延迟1秒执行
        }
        // 不需要手动关闭，因为任务是定时执行的
    }
}
```


#### 5.5 Work Stealing Pool

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkStealingPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newWorkStealingPool(); // 创建一个工作窃取线程池
        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            executor.execute(() -> {
                System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread().getName());
            });
        }
        executor.shutdown(); // 关闭线程池
    }
}
```


### 6. 任务的执行过程

线程池的任务执行过程可以分为以下几个步骤：

1. **提交任务**：
    - **方法**：`execute(Runnable command)` 或 `submit(Callable<T> task)`。
    - **描述**：将任务提交到线程池的任务队列中。

2. **线程池处理任务**：
    - **核心线程**：如果当前线程数小于核心线程数，创建新线程执行任务。
    - **任务队列**：如果当前线程数等于核心线程数，任务被放入任务队列中等待执行。
    - **最大线程数**：如果任务队列已满且当前线程数小于最大线程数，创建新线程执行任务。
    - **拒绝策略**：如果任务队列已满且当前线程数等于最大线程数，根据拒绝策略处理任务。

3. **任务执行**：
    - **线程执行**：线程从任务队列中取出任务并执行。
    - **任务完成**：任务执行完毕后，线程返回线程池等待下一个任务。

4. **线程池关闭**：
    - **方法**：`shutdown()` 或 `shutdownNow()`。
    - **描述**：`shutdown()` 会等待所有任务执行完毕后关闭线程池，`shutdownNow()` 会尝试停止所有正在执行的任务并关闭线程池。

### 7. 线程池的拒绝策略

当线程池无法接受新任务时，会根据拒绝策略处理任务。常见的拒绝策略包括：

- **AbortPolicy**（默认）：
    - **描述**：直接抛出 `RejectedExecutionException`。
    - **示例**：
      ```java
      ExecutorService executor = Executors.newFixedThreadPool(5, new ThreadPoolExecutor.AbortPolicy());
      ```


- **CallerRunsPolicy**：
    - **描述**：由提交任务的线程执行任务。
    - **示例**：
      ```java
      ExecutorService executor = Executors.newFixedThreadPool(5, new ThreadPoolExecutor.CallerRunsPolicy());
      ```


- **DiscardPolicy**：
    - **描述**：直接丢弃任务，不抛出异常。
    - **示例**：
      ```java
      ExecutorService executor = Executors.newFixedThreadPool(5, new ThreadPoolExecutor.DiscardPolicy());
      ```


- **DiscardOldestPolicy**：
    - **描述**：丢弃任务队列中最老的任务，然后尝试提交新任务。
    - **示例**：
      ```java
      ExecutorService executor = Executors.newFixedThreadPool(5, new ThreadPoolExecutor.DiscardOldestPolicy());
      ```


### 8. 自定义线程池

通过 `ThreadPoolExecutor` 类可以创建自定义的线程池，配置更灵活。

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExample {
    public static void main(String[] args) {
        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepAliveTime = 5000;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 0; i < 20; i++) {
            int taskNumber = i;
            executor.execute(() -> {
                System.out.println("Task " + taskNumber + " is running on thread " + Thread.currentThread().getName());
            });
        }

        executor.shutdown(); // 关闭线程池
    }
}
```


### 9. 总结

- **定义**：线程池是一种线程管理机制，通过预先创建和复用线程来提高系统性能。
- **分类**：
    - **Fixed Thread Pool**：固定大小的线程池。
    - **Cached Thread Pool**：可缓存的线程池，大小动态调整。
    - **Single Thread Executor**：单线程的线程池。
    - **Scheduled Thread Pool**：支持定时和周期性任务的线程池。
    - **Work Stealing Pool**：支持工作窃取的线程池。
- **参数**：
    - **核心线程数**：线程池中保持活动状态的最小线程数。
    - **最大线程数**：线程池中允许的最大线程数。
    - **线程存活时间**：当线程数超过核心线程数时，多余的空闲线程在终止前等待新任务的最长时间。
    - **时间单位**：`keepAliveTime` 的时间单位。
    - **任务队列**：用于保存等待执行任务的队列。
    - **线程工厂**：用于创建新线程的工厂。
    - **拒绝策略**：当任务无法执行时的拒绝策略。
- **任务执行过程**：
    1. 提交任务到任务队列。
    2. 线程池根据当前线程数和任务队列的状态创建或复用线程执行任务。
    3. 任务执行完毕后，线程返回线程池等待下一个任务。
    4. 关闭线程池。
- **拒绝策略**：
    - **AbortPolicy**：直接抛出 `RejectedExecutionException`。
    - **CallerRunsPolicy**：由提交任务的线程执行任务。
    - **DiscardPolicy**：直接丢弃任务，不抛出异常。
    - **DiscardOldestPolicy**：丢弃任务队列中最老的任务，然后尝试提交新任务。
      通过理解线程池的分类、定义、各项参数以及任务执行过程，可以更好地管理和优化线程池，提高应用程序的性能和资源利用率。
      线程池创建方式
- Executors工厂类创建
- 定长线程池  newFixedThreadPool
- 缓存线程池 newCachedThreadPool
- 单线程线程池 newSingleThreadExecutor
- 创建一个支持定时和周期性任务的线程池  ThreadPoolExecutor

Springboot  ThreadPoolTaskExecutor
在 Spring Boot 中创建和配置线程池是一个常见的任务，可以显著提高应用程序的性能和资源利用率。
Spring Boot 提供了多种方式来创建和配置线程池，包括使用 `@Async` 注解和手动配置 `ThreadPoolTaskExecutor`。以下是详细的步骤和示例代码。

### 1. 使用 `@Async` 注解

`@Async` 注解可以方便地将方法标记为异步执行，Spring Boot 会自动使用一个默认的线程池来执行这些方法。

#### 1.1 启用异步支持

首先，在 Spring Boot 应用程序的主类或配置类上添加 `@EnableAsync` 注解，以启用异步支持。

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootAsyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAsyncApplication.class, args);
    }
}
```


#### 1.2 创建异步方法

在需要异步执行的方法上添加 `@Async` 注解。

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    @Async
    public void asyncMethodWithVoidReturnType() {
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName());
    }

    @Async
    public CompletableFuture<String> asyncMethodWithReturnType() {
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
            return CompletableFuture.completedFuture("Hello, World !!");
        } catch (InterruptedException e) {
            //
            return CompletableFuture.completedFuture("Exception occurred");
        }
    }
}
```


#### 1.3 调用异步方法

在控制器或其他服务中调用异步方法。

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/async")
    public String asyncMethod() {
        asyncService.asyncMethodWithVoidReturnType();
        CompletableFuture<String> future = asyncService.asyncMethodWithReturnType();
        return "Processing in separate thread...";
    }
}
```


### 2. 手动配置 `ThreadPoolTaskExecutor`

手动配置 `ThreadPoolTaskExecutor` 可以提供更细粒度的控制，包括核心线程数、最大线程数、线程存活时间、任务队列等。

#### 2.1 配置线程池

创建一个配置类来配置 `ThreadPoolTaskExecutor`。

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 任务队列容量
        executor.setThreadNamePrefix("TaskExecutor-"); // 线程名称前缀
        executor.initialize();
        return executor;
    }
}
```


#### 2.2 使用自定义线程池

在异步方法中指定使用自定义的线程池。

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class AsyncService {

    @Async("taskExecutor")
    public void asyncMethodWithVoidReturnType() {
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName());
    }

    @Async("taskExecutor")
    public Future<String> asyncMethodWithReturnType() {
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
            return new AsyncResult<>("Hello, World !!");
        } catch (InterruptedException e) {
            //
            return new AsyncResult<>("Exception occurred");
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethodWithCompletableFuture() {
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
            return CompletableFuture.completedFuture("Hello, World !!");
        } catch (InterruptedException e) {
            //
            return CompletableFuture.completedFuture("Exception occurred");
        }
    }
}
```


#### 2.3 调用异步方法

在控制器或其他服务中调用异步方法。

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/async")
    public String asyncMethod() {
        asyncService.asyncMethodWithVoidReturnType();
        Future<String> future = asyncService.asyncMethodWithReturnType();
        CompletableFuture<String> completableFuture = asyncService.asyncMethodWithCompletableFuture();
        return "Processing in separate thread...";
    }
}
```


### 3. 配置线程池的详细参数

以下是一些常见的线程池参数及其配置方法：

- **核心线程数（corePoolSize）**：线程池中保持活动状态的最小线程数。
- **最大线程数（maxPoolSize）**：线程池中允许的最大线程数。
- **线程存活时间（keepAliveTime）**：当线程数超过核心线程数时，多余的空闲线程在终止前等待新任务的最长时间。
- **时间单位（unit）**：`keepAliveTime` 的时间单位。
- **任务队列（queueCapacity）**：用于保存等待执行任务的队列容量。
- **线程名称前缀（threadNamePrefix）**：线程名称的前缀，便于识别线程。
- **拒绝策略（rejectedExecutionHandler）**：当任务无法执行时的拒绝策略。

#### 3.1 完整配置示例

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 任务队列容量
        executor.setThreadNamePrefix("TaskExecutor-"); // 线程名称前缀
        executor.setKeepAliveSeconds(60); // 线程存活时间
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略
        executor.initialize();
        return executor;
    }
}
```


### 4. 使用 `ScheduledExecutorService` 创建定时任务线程池

如果需要创建定时任务线程池，可以使用 `ScheduledExecutorService`。

#### 4.1 配置定时任务线程池

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ScheduledConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 线程池大小
        scheduler.setThreadNamePrefix("ScheduledTask-"); // 线程名称前缀
        scheduler.initialize();
        return scheduler;
    }
}
```


#### 4.2 创建定时任务

使用 `@Scheduled` 注解创建定时任务。

```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void reportCurrentTime() {
        System.out.println("Current time is " + System.currentTimeMillis() +
                " on thread " + Thread.currentThread().getName());
    }
}
```


### 5. 总结

- **启用异步支持**：使用 `@EnableAsync` 注解启用异步支持。
- **创建异步方法**：使用 `@Async` 注解标记需要异步执行的方法。
- **手动配置线程池**：通过 `ThreadPoolTaskExecutor` 手动配置线程池的参数。
- **配置参数**：
    - **核心线程数**：`setCorePoolSize(int corePoolSize)`
    - **最大线程数**：`setMaxPoolSize(int maxPoolSize)`
    - **线程存活时间**：`setKeepAliveSeconds(int keepAliveSeconds)`
    - **任务队列容量**：`setQueueCapacity(int queueCapacity)`
    - **线程名称前缀**：`setThreadNamePrefix(String threadNamePrefix)`
    - **拒绝策略**：`setRejectedExecutionHandler(RejectedExecutionHandler handler)`
- **定时任务线程池**：使用 `ThreadPoolTaskScheduler` 创建定时任务线程池。
- **使用 `@Scheduled` 注解**：创建定时任务。

通过以上步骤和示例代码，你可以在 Spring Boot 中创建和配置线程池，以提高应用程序的性能和资源利用率。


在使用 `ThreadPoolTaskExecutor` 时，有时需要在主线程和子线程之间传递变量。
由于线程池中的线程是复用的，直接传递变量可能会导致线程安全问题。为了安全地传递变量，可以使用 `taskDecorator` 来装饰任务，
确保每个任务都能访问到所需的变量。

### 使用 `taskDecorator` 实现主线程和子线程之间的变量传递

`taskDecorator` 是 `ThreadPoolTaskExecutor` 提供的一个接口，允许你在任务执行前后进行一些自定义操作，例如传递上下文信息。

#### 1. 创建一个 `TaskDecorator`

首先，创建一个实现 `TaskDecorator` 接口的类，用于在任务执行前后传递变量。

```java
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextTaskDecorator implements TaskDecorator {

    private final Map<Thread, Map<String, Object>> threadLocalContext = new ConcurrentHashMap<>();

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, Object> context = new ConcurrentHashMap<>(getContext());
        return () -> {
            try {
                setContext(context);
                runnable.run();
            } finally {
                clearContext();
            }
        };
    }

    public void setContext(Map<String, Object> context) {
        threadLocalContext.put(Thread.currentThread(), context);
    }

    public void clearContext() {
        threadLocalContext.remove(Thread.currentThread());
    }

    public Map<String, Object> getContext() {
        return threadLocalContext.get(Thread.currentThread());
    }

    public void put(String key, Object value) {
        Map<String, Object> context = threadLocalContext.get(Thread.currentThread());
        if (context == null) {
            context = new ConcurrentHashMap<>();
            threadLocalContext.put(Thread.currentThread(), context);
        }
        context.put(key, value);
    }

    public Object get(String key) {
        Map<String, Object> context = threadLocalContext.get(Thread.currentThread());
        if (context != null) {
            return context.get(key);
        }
        return null;
    }
}
```


#### 2. 配置 `ThreadPoolTaskExecutor` 使用 `taskDecorator`

在配置类中配置 `ThreadPoolTaskExecutor` 并设置 `taskDecorator`。

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 任务队列容量
        executor.setThreadNamePrefix("TaskExecutor-"); // 线程名称前缀
        executor.setKeepAliveSeconds(60); // 线程存活时间
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略

        // 设置 taskDecorator
        ContextTaskDecorator taskDecorator = new ContextTaskDecorator();
        executor.setTaskDecorator(taskDecorator);

        executor.initialize();
        return executor;
    }

    @Bean
    public ContextTaskDecorator contextTaskDecorator() {
        return new ContextTaskDecorator();
    }
}
```


#### 3. 在主线程中设置上下文变量

在主线程中设置需要传递的变量。

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    @Autowired
    private ContextTaskDecorator contextTaskDecorator;

    @Async("taskExecutor")
    public void asyncMethodWithVoidReturnType() {
        String value = (String) contextTaskDecorator.get("myKey");
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName() +
                ", Value - " + value);
    }

    public void executeAsyncWithContext() {
        // 设置上下文变量
        contextTaskDecorator.put("myKey", "Hello, World !!");

        // 执行异步方法
        asyncMethodWithVoidReturnType();

        // 清除上下文变量（可选）
        contextTaskDecorator.clearContext();
    }
}
```


#### 4. 调用异步方法

在控制器或其他服务中调用异步方法，并设置上下文变量。

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/async")
    public String asyncMethod() {
        asyncService.executeAsyncWithContext();
        return "Processing in separate thread...";
    }
}
```


### 完整示例

以下是完整的示例代码，展示了如何使用 `taskDecorator` 在主线程和子线程之间传递变量。

#### 1. `ContextTaskDecorator` 类

```java
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextTaskDecorator implements TaskDecorator {

    private final Map<Thread, Map<String, Object>> threadLocalContext = new ConcurrentHashMap<>();

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, Object> context = new ConcurrentHashMap<>(getContext());
        return () -> {
            try {
                setContext(context);
                runnable.run();
            } finally {
                clearContext();
            }
        };
    }

    public void setContext(Map<String, Object> context) {
        threadLocalContext.put(Thread.currentThread(), context);
    }

    public void clearContext() {
        threadLocalContext.remove(Thread.currentThread());
    }

    public Map<String, Object> getContext() {
        return threadLocalContext.get(Thread.currentThread());
    }

    public void put(String key, Object value) {
        Map<String, Object> context = threadLocalContext.get(Thread.currentThread());
        if (context == null) {
            context = new ConcurrentHashMap<>();
            threadLocalContext.put(Thread.currentThread(), context);
        }
        context.put(key, value);
    }

    public Object get(String key) {
        Map<String, Object> context = threadLocalContext.get(Thread.currentThread());
        if (context != null) {
            return context.get(key);
        }
        return null;
    }
}
```


#### 2. `AsyncConfig` 配置类

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor(ContextTaskDecorator contextTaskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 任务队列容量
        executor.setThreadNamePrefix("TaskExecutor-"); // 线程名称前缀
        executor.setKeepAliveSeconds(60); // 线程存活时间
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略

        // 设置 taskDecorator
        executor.setTaskDecorator(contextTaskDecorator);

        executor.initialize();
        return executor;
    }

    @Bean
    public ContextTaskDecorator contextTaskDecorator() {
        return new ContextTaskDecorator();
    }
}
```


#### 3. `AsyncService` 服务类

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    @Autowired
    private ContextTaskDecorator contextTaskDecorator;

    @Async("taskExecutor")
    public void asyncMethodWithVoidReturnType() {
        String value = (String) contextTaskDecorator.get("myKey");
        System.out.println("Execute method asynchronously. " +
                "Thread name - " + Thread.currentThread().getName() +
                ", Value - " + value);
    }

    public void executeAsyncWithContext() {
        // 设置上下文变量
        contextTaskDecorator.put("myKey", "Hello, World !!");

        // 执行异步方法
        asyncMethodWithVoidReturnType();

        // 清除上下文变量（可选）
        contextTaskDecorator.clearContext();
    }
}
```


#### 4. `AsyncController` 控制器类

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/async")
    public String asyncMethod() {
        asyncService.executeAsyncWithContext();
        return "Processing in separate thread...";
    }
}
```


### 解释

1. **`ContextTaskDecorator` 类**：
    - **`decorate` 方法**：在任务执行前后设置和清除上下文变量。
    - **`put` 和 `get` 方法**：用于设置和获取上下文变量。
    - **`setContext` 和 `clearContext` 方法**：用于设置和清除当前线程的上下文。

2. **`AsyncConfig` 配置类**：
    - **`taskExecutor` 方法**：配置 `ThreadPoolTaskExecutor` 并设置 `taskDecorator`。
    - **`contextTaskDecorator` 方法**：创建 `ContextTaskDecorator` 实例。

3. **`AsyncService` 服务类**：
    - **`executeAsyncWithContext` 方法**：在主线程中设置上下文变量，并调用异步方法。
    - **`asyncMethodWithVoidReturnType` 方法**：在子线程中获取上下文变量并执行任务。

4. **`AsyncController` 控制器类**：
    - **`asyncMethod` 方法**：通过控制器调用服务方法，触发异步任务。

### 运行示例

启动 Spring Boot 应用程序后，访问 `/async` 端点：

```
http://localhost:8080/async
```


控制台输出示例：

```
Processing in separate thread...
Execute method asynchronously. Thread name - TaskExecutor-1, Value - Hello, World !!
```


### 注意事项

- **线程安全**：`ContextTaskDecorator` 使用 `ConcurrentHashMap` 来存储上下文变量，确保线程安全。
- **上下文管理**：在任务执行完毕后，清除上下文变量以避免内存泄漏。
- **复杂上下文**：如果需要传递复杂的上下文信息，可以考虑使用 `InheritableThreadLocal` 或其他线程安全的上下文管理机制。

通过使用 `taskDecorator`，可以安全地在主线程和子线程之间传递变量，确保线程池中的任务能够正确地访问所需的上下文信息。

线程相关-Questions
ThreadLocal是什么
### ThreadLocal 底层存储原理
#### 整体架构
`ThreadLocal` 是 Java 中的一个类，它为每个使用该变量的线程都提供一个独立的变量副本，
每个线程都可以独立地改变自己的副本，而不会影响其他线程所对应的副本。
其底层主要依赖于 `Thread` 类、`ThreadLocal` 类和 `ThreadLocalMap` 类来实现。

#### 核心类及关联关系
- **Thread 类**：`Thread` 类中有一个类型为 `ThreadLocalMap` 的成员变量 `threadLocals`，用于存储该线程中所有的 `ThreadLocal` 变量及其对应的值。
```java
public class Thread implements Runnable {
    // ...
    ThreadLocal.ThreadLocalMap threadLocals = null;
    // ...
}
```
- **ThreadLocal 类**：`ThreadLocal` 类是一个工具类，主要提供了 `get()`、`set()`、`remove()` 等方法来操作线程本地变量。当调用 `ThreadLocal` 的 `set()` 或 `get()` 方法时，会去访问当前线程的 `threadLocals` 成员变量。
- **ThreadLocalMap 类**：`ThreadLocalMap` 是 `ThreadLocal` 的一个静态内部类，它是一个定制的哈希表，用于存储 `ThreadLocal` 变量和其对应的值。`ThreadLocalMap` 的键是 `ThreadLocal` 对象的弱引用，值是用户设置的具体对象。

#### 存储和获取过程
- **存储（`set()` 方法）**
```java
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}
```
    - 首先获取当前线程 `t`。
    - 然后通过 `getMap(t)` 方法获取当前线程的 `ThreadLocalMap` 对象 `map`。
    - 如果 `map` 不为空，则调用 `map.set(this, value)` 方法将当前 `ThreadLocal` 对象作为键，`value` 作为值存储到 `ThreadLocalMap` 中。
    - 如果 `map` 为空，则调用 `createMap(t, value)` 方法为当前线程创建一个新的 `ThreadLocalMap`，并将 `ThreadLocal` 对象和 `value` 存储进去。
- **获取（`get()` 方法）**
```java
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}
```
    - 同样先获取当前线程 `t`。
    - 通过 `getMap(t)` 方法获取当前线程的 `ThreadLocalMap` 对象 `map`。
    - 如果 `map` 不为空，则调用 `map.getEntry(this)` 方法根据当前 `ThreadLocal` 对象作为键去查找对应的 `Entry` 对象。
    - 如果找到 `Entry` 对象，则返回其存储的值；如果 `map` 为空或者没有找到对应的 `Entry`，则调用 `setInitialValue()` 方法设置初始值并返回。

### 发生内存泄露的几种情况

#### 1. 线程长期存活且 `ThreadLocal` 不再使用但未手动移除
`ThreadLocalMap` 中的键是 `ThreadLocal` 对象的弱引用。当外部对 `ThreadLocal` 对象的强引用被回收时，由于 `ThreadLocalMap` 中的键是弱引用，在垃圾回收时，这个 `ThreadLocal` 对象会被回收，其对应的键会变为 `null`。但此时 `ThreadLocalMap` 中的值是强引用，不会被回收。如果线程一直存活，`ThreadLocalMap` 也会一直存在，这些值就无法被回收，从而造成内存泄露。
```java
public class ThreadLocalMemoryLeakExample {
    private static ThreadLocal<LargeObject> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            threadLocal.set(new LargeObject());
            // 模拟业务操作
            // ...
            // 这里没有调用 threadLocal.remove()
        });
        thread.start();
        // 后续 thread 线程可能一直存活
    }
}

class LargeObject {
    // 占用大量内存的对象
    private byte[] data = new byte[1024 * 1024];
}
```

#### 2. 线程池使用 `ThreadLocal` 未清理
在使用线程池时，线程会被复用。如果在一个线程中使用了 `ThreadLocal` 并设置了值，但在任务执行完毕后没有调用 `remove()` 方法清理，当该线程被复用时，`ThreadLocal` 中仍然保留着之前的值，可能会导致数据混乱，并且随着任务的不断执行，`ThreadLocalMap` 中的数据会越来越多，最终可能导致内存泄露。
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolThreadLocalMemoryLeakExample {
    private static ThreadLocal<LargeObject> threadLocal = new ThreadLocal<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        executorService.submit(() -> {
            threadLocal.set(new LargeObject());
            // 模拟业务操作
            // ...
            // 没有调用 threadLocal.remove()
        });

        executorService.submit(() -> {
            // 此时 threadLocal 可能还保留着上一个任务设置的值
            // ...
        });
    }
}
```

为了避免 `ThreadLocal` 内存泄露，在 `ThreadLocal` 不再使用时，应及时调用 `remove()` 方法清除其在 `ThreadLocalMap` 中的数据。
Tomcat线程池和Web服务线程池的区别
## Tomcat 线程池
- Tomcat 线程池通常会在启动时创建一定数量的初始线程（核心线程数），这些线程处于空闲状态，等待接收并处理请求。当有新的 HTTP 请求到达时，会优先分配这些已创建的空闲线程来处理。
- 当并发请求数超过了初始创建的线程数时，Tomcat 会根据配置继续创建新的线程，直到达到最大线程数限制。
- 如果请求数继续增加，超过了最大线程数，此时新的请求就会进入任务队列进行等待，直到有线程空闲出来可以处理它们。
  Web 服务线程池
- Web 服务线程池的工作方式通常是当有任务提交时，首先会尝试将任务放入任务队列。
- 如果任务队列未满，任务就会在队列中等待被线程池中的线程取出执行。
- 当任务队列已满时，才会根据线程池的配置创建新的线程来处理任务，不过也会受到最大线程数的限制。如果达到最大线程数且队列已满，后续的任务可能会根据具体的拒绝策略进行处理，如直接拒绝、丢弃最旧的任务等。
  这种差异主要是由于两者的设计目标和应用场景不同导致的。Tomcat 作为 Web 服务器，需要快速响应 HTTP 请求，所以先创建一定数量的线程来减少请求处理的延迟。而 Web 服务线程池更侧重于根据业务任务的实际情况动态调整线程数量，以避免线程过多消耗系统资源。