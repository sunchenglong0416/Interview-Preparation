happens-before 是 Java 内存模型（JMM）中的一个重要概念，它用于描述两个操作之间的内存可见性。如果操作 A happens-before 操作 B，那么 A 的结果对 B 是可见的，即 B 操作能够看到 A 操作所做的内存修改。下面详细介绍 happens-before 及其规则：
happens - before
在多线程环境中，为了提高性能，编译器和处理器可能会对指令进行重排序，这可能会导致程序的执行顺序与代码的编写顺序不一致。`happens-before` 规则为程序员提供了一种方式来确保在某些情况下，操作之间的顺序和可见性是可预测的，从而避免了由于重排序而导致的并发问题。
happens - before 规则
1. 程序顺序规则（Program Order Rule）
   一个线程中的每个操作，`happens-before` 该线程中任意后续的操作。也就是说，在单线程环境下，代码的执行顺序是按照编写顺序依次执行的（虽然可能存在指令重排序，但最终结果和顺序执行是一致的）。
```java
int a = 1; // 操作 A
int b = a + 2; // 操作 B
```
在这个例子中，操作 A `happens-before` 操作 B，操作 B 能够看到操作 A 对变量 `a` 的赋值结果。
2. 监视器锁规则（Monitor Lock Rule）
   对一个锁的解锁操作，`happens-before` 后续对这个锁的加锁操作。这意味着当一个线程释放锁时，它在释放锁之前所做的所有操作的结果，对于下一个获取该锁的线程来说都是可见的。

```java
class MonitorExample {
    private int value = 0;
    private final Object lock = new Object();

    public void writer() {
        synchronized (lock) {
            value = 1; // 操作 A
        } // 解锁操作
    }

    public void reader() {
        synchronized (lock) { // 加锁操作
            int result = value; // 操作 B
        }
    }
}
```
在这个例子中，操作 A `happens-before` 操作 B，因为解锁操作 `happens-before` 加锁操作，所以操作 B 能够看到操作 A 对变量 `value` 的赋值结果。

3. volatile 变量规则（Volatile Variable Rule）
   对一个 `volatile` 变量的写操作，`happens-before` 后续对这个 `volatile` 变量的读操作。`volatile` 关键字保证了变量的可见性，当一个线程写入 `volatile` 变量时，会立即将其刷新到主内存中，而其他线程读取该 `volatile` 变量时，会从主内存中读取最新的值。

```java
class VolatileExample {
    private volatile int flag = 0;

    public void writer() {
        flag = 1; // 操作 A
    }

    public void reader() {
        int result = flag; // 操作 B
    }
}
```
在这个例子中，操作 A `happens-before` 操作 B，操作 B 能够看到操作 A 对 `volatile` 变量 `flag` 的赋值结果。

4. 线程启动规则（Thread Start Rule）
   `Thread.start()` 方法的调用，`happens-before` 该线程中的任意操作。也就是说，当调用 `Thread.start()` 方法启动一个新线程时，主线程在调用 `start()` 方法之前所做的所有操作的结果，对于新线程来说都是可见的。

```java
class ThreadStartExample {
    private int value = 0;

    public void startThread() {
        value = 1; // 操作 A
        Thread t = new Thread(() -> {
            int result = value; // 操作 B
        });
        t.start(); // 启动线程
    }
}
```
在这个例子中，操作 A `happens-before` 操作 B，操作 B 能够看到操作 A 对变量 `value` 的赋值结果。

5. 线程终止规则（Thread Termination Rule）
   线程中的任意操作，`happens-before` 其他线程检测到该线程已经终止（如通过 `Thread.join()` 方法等待线程终止，或者通过 `Thread.isAlive()` 方法判断线程是否终止）。

```java
class ThreadTerminationExample {
    private int value = 0;

    public void runThread() {
        Thread t = new Thread(() -> {
            value = 1; // 操作 A
        });
        t.start();
        try {
            t.join(); // 等待线程终止
            int result = value; // 操作 B
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
在这个例子中，操作 A `happens-before` 操作 B，操作 B 能够看到操作 A 对变量 `value` 的赋值结果。

6. 中断规则（Interruption Rule）
   一个线程调用另一个线程的 `interrupt()` 方法，`happens-before` 被中断线程检测到中断事件（如通过 `Thread.interrupted()` 或 `Thread.isInterrupted()` 方法）。

```java
class InterruptionExample {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // 执行任务
            }
            // 操作 B：检测到中断
        });
        t.start();
        t.interrupt(); // 操作 A：调用 interrupt() 方法
    }
}
```
在这个例子中，操作 A `happens-before` 操作 B，操作 B 能够检测到操作 A 所引发的中断事件。

7. 传递性规则（Transitivity Rule）
   如果操作 A `happens-before` 操作 B，操作 B `happens-before` 操作 C，那么操作 A `happens-before` 操作 C。

```java
int a = 1; // 操作 A
volatile int b = a; // 操作 B
int c = b; // 操作 C
```
根据程序顺序规则，操作 A `happens-before` 操作 B；根据 `volatile` 变量规则，操作 B `happens-before` 操作 C；再根据传递性规则，操作 A `happens-before` 操作 C，所以操作 C 能够看到操作 A 对变量 `a` 的赋值结果。

### 总结
`happens-before` 规则为 Java 程序员提供了一种在多线程环境下确保操作顺序和内存可见性的方式。通过合理运用这些规则，可以编写出正确、高效的并发程序。