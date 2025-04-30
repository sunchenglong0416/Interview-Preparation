

Java 的 AQS（AbstractQueuedSynchronizer）是 Java 并发包（java.util.concurrent）中的一个核心框架，用于构建各种同步器（如锁、信号量、栅栏等）。AQS 提供了一个基于 FIFO 队列的框架，简化了同步器的实现过程。以下是关于 AQS 的详细解释、具体实现及其工作原理。
1. 什么是 AQS
- **定义**：AbstractQueuedSynchronizer 是一个抽象类，提供了基于 FIFO 队列的同步器框架。
- **作用**：简化了同步器的实现，开发者可以通过继承 AQS 并实现少量的方法来创建自定义的同步器。
- **核心组件**：

- **同步状态（State）**：一个 int 类型的变量，用于表示同步状态。
- AQS（AbstractQueuedSynchronizer）设置锁的状态是基于 CAS（Compare-And-Swap）实现的
- **等待队列**：一个 FIFO 的双向链表，用于存储等待获取锁的线程。
2. AQS 的主要方法
- **获取锁**：
- acquire(int arg)：独占式获取锁，如果获取失败则加入等待队列。
- acquireInterruptibly(int arg)：可中断的独占式获取锁。
- tryAcquireNanos(int arg, long nanosTimeout)：在指定时间内尝试获取锁。
- **释放锁**：
- release(int arg)：释放锁。
- **共享式获取锁**：
- acquireShared(int arg)：共享式获取锁，如果获取失败则加入等待队列。
- acquireSharedInterruptibly(int arg)：可中断的共享式获取锁。
- tryAcquireSharedNanos(int arg, long nanosTimeout)：在指定时间内尝试共享式获取锁。
- **释放共享锁**：
- releaseShared(int arg)：释放共享锁。
- **条件变量**：
- newCondition()：创建一个条件变量，用于线程间的等待和通知。
3. AQS 的具体实现
   AQS 是一个抽象类，提供了基本的同步机制，具体的同步器通过继承 AQS 并实现以下方法来定义同步逻辑：
- **独占式同步**：
- protected boolean tryAcquire(int arg)：尝试独占式获取锁。
- protected boolean tryRelease(int arg)：尝试释放独占式锁。
- **共享式同步**：
- protected int tryAcquireShared(int arg)：尝试共享式获取锁。
- protected boolean tryReleaseShared(int arg)：尝试释放共享式锁。
  以下是一些基于 AQS 的具体实现：
  1.ReentrantLock
- **描述**：可重入锁，支持公平锁和非公平锁。
- 可重入锁，也叫递归锁，指的是同一个线程在外层方法获取锁之后，在进入该线程的内层方法时如果需要再次获取该锁，是可以直接获取而不会被阻塞的。这意味着线程可以多次进入被同一把锁保护的代码块。
- **实现**：

- NonfairSync 和 FairSync 内部类分别实现了非公平锁和公平锁的逻辑。
- 通过重写 tryAcquire 和 tryRelease 方法来实现锁的获取和释放。
  2.Semaphore
- **描述**：信号量，用于控制同时访问某个资源的线程数量。
- **实现**：

- 通过重写 tryAcquireShared 和 tryReleaseShared 方法来实现信号量的获取和释放。
  3.CountDownLatch
- **描述**：倒计数器，用于等待一组线程完成操作。
- **实现**：

- 通过重写 tryAcquireShared 和 tryReleaseShared 方法来实现倒计数器的逻辑。
  4.CyclicBarrier
- **描述**：循环屏障，用于一组线程相互等待，直到所有线程都到达屏障点。
- **实现**：

- 通过重写 tryAcquireShared 和 tryReleaseShared 方法来实现屏障的逻辑。
  5.ReentrantReadWriteLock
- **描述**：可重入读写锁，允许多个读线程同时访问，但写线程独占访问。
- **实现**：

- ReadLock 和 WriteLock 内部类分别实现了读锁和写锁的逻辑。
- 通过重写 tryAcquire、tryRelease、tryAcquireShared 和 tryReleaseShared 方法来实现读写锁的逻辑。
4. AQS 的工作原理
   AQS 的核心是通过一个 FIFO 的等待队列来管理等待获取锁的线程。具体工作原理如下：
*  1.**获取锁**：
- **尝试获取锁**：线程调用 acquire 或 acquireShared 方法尝试获取锁。
- **成功**：如果锁可用，线程立即获取锁。
- **失败**：如果锁不可用，线程被封装成一个 Node 对象并加入等待队列。
- **阻塞**：线程进入阻塞状态，等待被唤醒。
*  2.**释放锁**：
- **释放锁**：线程调用 release 或 releaseShared 方法释放锁。
- **唤醒线程**：释放锁后，等待队列中的一个线程被唤醒并尝试获取锁。
- **状态更新**：同步状态（state）被更新。
*  3.**条件变量**：
- **等待**：线程调用 Condition 的 await 方法进入等待状态。
- **唤醒**：线程调用 Condition 的 signal 或 signalAll 方法唤醒等待的线程。
  5. 示例代码
     以下是一个简单的 ReentrantLock 实现示例，展示了如何继承 AQS 并实现独占式锁的逻辑：
  ~~~ java
     import java.util.concurrent.locks.AbstractQueuedSynchronizer;
      public class SimpleReentrantLock {
      private final Sync sync = new Sync();
      private static class Sync extends AbstractQueuedSynchronizer {
          // 尝试获取锁
          protected boolean tryAcquire(int acquires) {
              final Thread current = Thread.currentThread();
              int c = getState();
              if (c == 0) {
                  if (compareAndSetState(0, acquires)) {
                      setExclusiveOwnerThread(current);
                      return true;
                  }
              } else if (current == getExclusiveOwnerThread()) {
                  int nextc = c + acquires;
                  setState(nextc);
                  return true;
              }
              return false;
          }

          // 尝试释放锁
          protected boolean tryRelease(int releases) {
              int c = getState() - releases;
              if (Thread.currentThread() != getExclusiveOwnerThread()) {
                  throw new IllegalMonitorStateException();
              }
              boolean free = false;
              if (c == 0) {
                  free = true;
                  setExclusiveOwnerThread(null);
              }
              setState(c);
              return free;
          }

          // 是否持有独占锁
          protected boolean isHeldExclusively() {
              return getExclusiveOwnerThread() == Thread.currentThread();
          }

          // 创建条件变量
          final ConditionObject newCondition() {
              return new ConditionObject();
          }
      }

      public void lock() {
          sync.acquire(1);
      }

      public boolean tryLock() {
          return sync.tryAcquire(1);
      }

      public void unlock() {
          sync.release(1);
      }

      public Condition newCondition() {
          return sync.newCondition();
      }

      public static void main(String[] args) {
          SimpleReentrantLock lock = new SimpleReentrantLock();

          Runnable task = () -> {
              lock.lock();
              try {
                  System.out.println(Thread.currentThread().getName() + " acquired the lock");
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
              } finally {
                  lock.unlock();
                  System.out.println(Thread.currentThread().getName() + " released the lock");
              }
          };

          Thread t1 = new Thread(task, "Thread-1");
          Thread t2 = new Thread(task, "Thread-2");

          t1.start();
          t2.start();
      }
  }
  ~~~
总结
- **AQS**：AbstractQueuedSynchronizer 是 Java 并发包中的核心框架，用于构建各种同步器。
- **主要方法**：提供了获取锁、释放锁、共享式获取锁、释放共享锁和条件变量的方法。
- **具体实现**：ReentrantLock、Semaphore、CountDownLatch、CyclicBarrier 和 ReentrantReadWriteLock 等都是基于 AQS 的具体实现。
- **工作原理**：通过 FIFO 的等待队列管理等待获取锁的线程，支持独占式和共享式同步。
- **适用场景**：适用于需要自定义同步逻辑的复杂并发控制场景。
  通过理解 AQS 的工作原理和具体实现，可以更好地利用 Java 并发包中的同步器，并在必要时创建自定义的同步器。