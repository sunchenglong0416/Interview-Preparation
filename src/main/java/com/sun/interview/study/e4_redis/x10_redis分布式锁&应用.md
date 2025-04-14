在 Redis 4.0 的最小集群模式下实现分布式锁并确保高可用性，需要考虑以下几个方面：
1. **集群配置**：确保集群的高可用性。
2. **分布式锁实现**：使用 Redis 提供的原子操作来实现分布式锁。
3. **故障转移处理**：确保在主节点故障时，分布式锁仍然能够正常工作。
### 1. 集群配置
### 2. 分布式锁实现
Redis 提供了 `SETNX` 命令来实现分布式锁，
但推荐使用 `SET` 命令的扩展版本，
因为它提供了更多的灵活性和安全性。
SETNX 本身不是一个原子操作  
`SET` 命令的扩展版本  加锁和设置过期实现
在 Redis 中，
SET 命令扩展版本通过单线程和队列机制保证加锁和设置过期时间两个操作的原子性，以下是具体分析：

单线程执行：
Redis 是单线程模型，这意味着所有的命令都是顺序执行的。
当一个客户端发送 SET key value NX EX seconds 这样的命令时，
Redis 服务器会将这个命令作为一个整体来执行，不会被其他客户端的命令打断。
在执行这个命令的过程中，Redis 会先检查键是否存在（NX 选项），如果不存在则设置键值对，并同时设置过期时间（EX 选项）。
这个过程是在一个原子操作中完成的，不会出现只执行了设置键值对而没有设置过期时间的情况，或者相反的情况。
命令队列：
当多个客户端同时向 Redis 发送命令时，这些命令会被放入一个队列中。
Redis 会按照队列中的顺序依次执行这些命令，确保每个命令都是原子执行的。
即使有多个客户端同时尝试获取分布式锁，SET 命令的扩展版本也能保证在同一时刻只有一个客户端的命令被执行，从而保证了加锁和设置过期时间的原子性。
#### 使用 `SET` 命令实现分布式锁
```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;
import java.util.Collections;
public class DistributedLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private JedisCluster jedisCluster;
    public DistributedLock(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }
    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        SetParams params = SetParams.setParams().nx().px(expireTime);
        String result = jedisCluster.set(lockKey, requestId, params);
        return LOCK_SUCCESS.equals(result);
    }
    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedisCluster.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        return RELEASE_SUCCESS.equals(result);
    }
    private static final Long RELEASE_SUCCESS = 1L;
}
```
### 3. 故障转移处理
在 Redis 集群中，哨兵（Sentinel）会监控主节点的状态。如果主节点故障，哨兵会自动将一个从节点提升为主节点。
为了确保分布式锁在故障转移后仍然能够正常工作，需要注意以下几点：
- **锁的唯一性**：确保每个客户端请求的锁标识（`requestId`）是唯一的，通常是 UUID。
- **锁的过期时间**：设置合理的锁过期时间，以避免死锁。
- **重试机制**：在获取锁失败时，客户端应该有重试机制，以确保在主节点故障转移后能够重新获取锁。
#### 示例重试机制
```java
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DistributedLockExample {
    private DistributedLock distributedLock;
    private JedisCluster jedisCluster;

    public DistributedLockExample(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
        this.distributedLock = new DistributedLock(jedisCluster);
    }

    public void performOperationWithLock() {
        String lockKey = "myLock";
        String requestId = UUID.randomUUID().toString();
        int expireTime = 10000; // 10秒

        boolean success = false;
        int retryCount = 0;
        int maxRetries = 5;

        while (!success && retryCount < maxRetries) {
            success = distributedLock.tryGetDistributedLock(lockKey, requestId, expireTime);
            if (!success) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100); // 等待100毫秒后重试
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                retryCount++;
            }
        }

        if (success) {
            try {
                // 执行需要加锁的操作
                System.out.println("Lock acquired, performing operation...");
            } finally {
                // 释放锁
                distributedLock.releaseDistributedLock(lockKey, requestId);
                System.out.println("Lock released.");
            }
        } else {
            System.out.println("Failed to acquire lock after retries.");
        }
    }
}
```
### 总结
1. **集群配置**：确保 Redis 集群的高可用性，使用哨兵监控主节点。
2. **分布式锁实现**：使用 `SET` 命令的扩展版本来实现分布式锁，确保锁的唯一性和过期时间。
3. **故障转移处理**：在主节点故障时，哨兵会自动进行故障转移，客户端需要有重试机制以确保能够重新获取锁。

通过以上步骤，你可以在 Redis 4.0 的最小集群模式下实现高可用的分布式锁。