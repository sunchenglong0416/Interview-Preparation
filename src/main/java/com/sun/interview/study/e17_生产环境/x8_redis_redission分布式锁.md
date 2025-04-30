# redisson 分布式锁方案

Redisson 分布式锁与“普通”分布式锁（如基于 Redis 的 SETNX 或 Redlock 实现）在功能、可靠性、可扩展性等方面有显著区别。以下是它们的详细对比：

---

## 🧩 一、核心概念对比

| 特性 | Redisson 分布式锁 | 普通分布式锁（如 SETNX/Redlock） |
|------|------------------|-------------------------------|
| 基于技术 | Redis + Lua 脚本 | Redis 命令（SETNX / SET / EVAL） |
| 是否支持看门狗机制 | ✅ 支持自动续期 | ❌ 不支持，需手动管理超时 |
| 是否支持可重入 | ✅ 支持 | ❌ 通常不支持，需要额外实现 |
| 是否支持锁释放校验 | ✅ 使用 Lua 脚本保证原子性 | ❌ 可能误删其他线程持有的锁 |
| 是否支持异步/响应式编程 | ✅ 支持 Future、Reactive、RxJava 等 | ❌ 一般只支持同步方式 |
| 是否支持多节点协调 | ✅ 支持联锁（MultiLock）、红锁（RedLock） | ❌ 需要自行实现 |

---

## 🔐 二、安全性对比

| 安全特性 | Redisson | 普通锁 |
|----------|----------|--------|
| 加锁是否原子 | ✅ 是（Lua 脚本） | ✅ 是（使用 `SET key value NX PX`） |
| 解锁是否原子 | ✅ 是（Lua 脚本） | ❌ 否（需判断 UUID 再删除） |
| 是否防误删 | ✅ 是（通过唯一标识和 Lua 判断） | ✅ 可以实现，但需额外逻辑 |
| 是否防止脑裂 | ✅ 支持红锁算法（RedLock） | ✅ 可用 RedLock 实现 |
| 是否支持公平锁 | ✅ 支持 Fair Lock | ❌ 通常不支持 |

---

## ⏱️ 三、可用性对比

| 可靠性特性 | Redisson | 普通锁 |
|------------|----------|--------|
| 自动续期（看门狗） | ✅ 支持 | ❌ 不支持 |
| 锁持有时间控制 | ✅ 可设置 leaseTime | ✅ 可设置 expireTime |
| 多种锁类型支持 | ✅ 支持读写锁、信号量、闭锁等 | ❌ 通常只支持基础互斥锁 |
| 异常恢复能力 | ✅ 看门狗保障业务未完成不释放锁 | ❌ 若业务异常中断，锁可能提前释放 |

---

## 📈 四、性能与易用性对比

| 性能/易用性 | Redisson | 普通锁 |
|-------------|----------|--------|
| 易用性 | ✅ 提供高级 API，封装良好 | ❌ 需要开发者自行封装 |
| 性能开销 | ⚠️ 略高（因封装和看门狗） | ✅ 更轻量 |
| 文档和社区 | ✅ 社区活跃，文档完善 | ✅ Redis 官方文档支持 |
| 依赖复杂度 | ✅ 需引入 Redisson 客户端库 | ✅ 只需 Redis 客户端即可 |

---

## 🧪 五、典型应用场景对比

| 场景 | 推荐方案 |
|------|-----------|
| 简单任务互斥 | 普通锁（SETNX / Lua） |
| 长时间任务执行 | ✅ Redisson（带看门狗） |
| 高并发下的资源控制 | ✅ Redisson（公平锁、读写锁） |
| 微服务间协作加锁 | ✅ Redisson（联锁 MultiLock） |
| 多 Redis 实例部署 | ✅ Redisson（红锁 RedLock） |
| 对性能非常敏感 | ❌ Redisson（可关闭看门狗优化） |

---

## 📝 六、代码示例对比

### Redisson 示例（推荐）

```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");

RedissonClient redisson = Redisson.create(config);
RLock lock = redisson.getLock("myLock");

lock.lock(); // 自动续期，默认30秒
try {
    // 执行业务逻辑
} finally {
    lock.unlock();
}
```


### 普通 Redis 分布式锁（SETNX 示例）

```java
String lockKey = "myLock";
String clientId = UUID.randomUUID().toString();

// 加锁
Boolean isLocked = jedis.setnx(lockKey, clientId) == 1;
if (isLocked) {
    jedis.expire(lockKey, 30); // 设置过期时间
}

// 业务逻辑...

// 解锁（需 Lua 脚本保证原子性）
String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
jedis.eval(luaScript, Collections.singletonList(lockKey), Collections.singletonList(clientId));
```


---

## ✅ 总结：如何选择？

| 需求 | 推荐方案 |
|------|----------|
| 快速实现简单锁 | 普通 Redis 分布式锁 |
| 稳定性要求高 | ✅ Redisson |
| 需要自动续期 | ✅ Redisson |
| 需要多种锁类型（读写锁、闭锁、信号量） | ✅ Redisson |
| 需要跨多个 Redis 实例保证一致性 | ✅ Redisson（RedLock） |
| 对性能极度敏感 | ❌ Redisson（可调优） |

---

Redisson 的分布式锁（如 `RLock`）中，**看门狗（Watchdog）机制并不会无限续期**，它的设计是为了在锁持有期间防止锁因超时而被释放。但续期是有条件的，不会永久持续。

---

### 🔍 看门狗机制的核心逻辑

1. **自动续期触发条件：**
    - 只要线程仍然持有锁，并且锁未被释放；
    - 并且当前线程还在执行中（没有主动 unlock）；
    - Redisson 会周期性地（默认每 1/3 的 `lockWatchdogTimeout` 时间）对锁进行续期。

2. **续期时间规则：**
    - 默认情况下，锁的过期时间为 `30_000` 毫秒（即 30 秒），这个值由 `Config.lockWatchdogTimeout` 控制。
    - 每次续期都会将锁的过期时间重置为 `lockWatchdogTimeout`。

3. **续期终止条件：**
    - 当调用 `unlock()` 方法时，看门狗停止，锁被释放；
    - 如果线程异常退出或宕机，看门狗线程也随之终止，锁会在 `lockWatchdogTimeout` 时间后自动释放（依赖 Redis 的 TTL 机制）；
    - 如果设置了 **明确的超时时间**（如 `lock(10, TimeUnit.SECONDS)`），则不会启用看门狗机制。

---

### ✅ 示例代码说明

```java
RLock lock = redisson.getLock("myLock");
lock.lock(); // 不带超时时间，启用看门狗机制
try {
    // 执行业务逻辑
} finally {
    lock.unlock(); // 释放锁，看门狗停止
}
```


- 上述代码中，只要未调用 `unlock()`，Redisson 会每隔约 10 秒（30s 的 1/3）自动续期一次锁。

---

### ⚠️ 注意事项

| 场景 | 是否启用看门狗 |
|------|----------------|
| `lock()`（无参） | ✅ 启用，默认续期 |
| `lock(timeout, unit)` | ❌ 不启用，锁会在指定时间后自动释放 |
| `tryLock()` + 无等待时间 | ❌ 不启用 |
| `tryLock(waitTime, leaseTime, unit)` | ❌ 不启用，leaseTime 到期自动释放 |

---

### 📝 总结

| 问题 | 回答 |
|------|------|
| Redisson 看门狗会无限续期吗？ | ❌ 不会无限续期，只有在线程持有锁且未手动释放的情况下才会周期性续期。一旦调用 `unlock()` 或线程异常退出，续期就会终止，锁最终会被释放。 |

如果你希望限制最大持有时间，建议使用带超时参数的 `lock(...)` 或 `tryLock(...)` 方法。