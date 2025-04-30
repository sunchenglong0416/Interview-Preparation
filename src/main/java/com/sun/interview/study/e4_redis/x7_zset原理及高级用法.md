# zset 底层数据结构

Redis 的 `ZSET`（有序集合）在数据量 **少** 和 **多** 时，底层存储结构会根据配置和实际数据大小自动调整，以平衡内存占用与性能。这种优化机制称为 **压缩列表（ziplist）到跳跃表（skiplist）的转换**。

---

## 🧱 Redis ZSET 的两种底层实现方式

| 实现方式 | 数据结构 | 特点 |
|----------|-----------|------|
| **压缩列表（ziplist）** | 一块连续内存，紧凑存储 | 节省内存，适合小数据量 |
| **跳跃表 + 哈希表（skiplist + dict）** | 多个节点动态分配 | 高性能，适合大数据量 |

---

## 🔁 数据量变化时的存储结构切换

Redis 默认使用 `ziplist` 存储 `ZSET`，当满足一定条件时会自动切换为 `skiplist`：

### ✅ 切换条件（默认配置）

```bash
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
```


- 当 `ZSET` 中成员数量超过 `zset-max-ziplist-entries`（默认 128），或任意一个 member 的长度超过 `zset-max-ziplist-value`（默认 64 字节）时，Redis 会将底层结构从 `ziplist` 升级为 `skiplist + dict`。
- 这个过程是 **不可逆的**，即一旦升级为 skiplist，即使后续删除部分数据也不会再降级回 ziplist。

---

## 🧠 为什么要做这种切换？

| 方面 | ziplist（小数据） | skiplist（大数据） |
|------|------------------|--------------------|
| **内存占用** | 更紧凑，节省内存 | 占用更多内存 |
| **插入/更新性能** | O(N)，慢 | O(log N)，快 |
| **适用场景** | 小型排行榜、缓存等 | 大型排序集合、频繁更新 |

---

## 🔍 如何查看当前 ZSET 的编码类型？

你可以使用 `OBJECT ENCODING key` 命令查看某个 `ZSET` 的当前编码：

```bash
127.0.0.1:6379> OBJECT ENCODING myzset
"ziplist"
```


或者：

```bash
"skiplist"
```


---

## 🛠️ 示例：观察切换过程

```bash
# 创建一个小的 ZSET（不会触发切换）
ZADD small_zset 1 a 2 b 3 c ... （共 50 个元素）

OBJECT ENCODING small_zset
"ziplist"

# 添加超过阈值的元素
ZADD big_zset 1 a 2 b ... (添加超过 128 个元素)

OBJECT ENCODING big_zset
"skiplist"
```


---

## ⚙️ 如何自定义切换阈值？

在 `redis.conf` 文件中可以修改以下两个参数：

```conf
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
```


> 修改后重启 Redis 生效。

---

## ✅ 总结

| 条件 | 底层结构 | 编码类型 | 特点 |
|------|------------|-------------|--------|
| 数据量小（成员数 ≤ 128 且每个 member ≤ 64 字节） | `ziplist` | `OBJ_ENCODING_ZIPLIST` | 内存友好，性能较低 |
| 数据量大（超过上述限制） | `skiplist + dict` | `OBJ_ENCODING_SKIPLIST` | 性能高，内存占用稍大 |

---

如果你有特定的业务场景，比如需要控制内存或优化性能，也可以告诉我，我可以提供更具体的调优建议。