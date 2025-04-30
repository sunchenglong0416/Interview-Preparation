
### 1. ConcurrentHashMap 结构

**定义**：
`ConcurrentHashMap` 是 Java 中用于并发环境的哈希表实现。它提供了高效的并发读写操作，适用于多线程环境。

**主要结构**：
- **Segment**：在 Java 7 及之前版本中，`ConcurrentHashMap` 使用多个 `Segment` 来实现分段锁，每个 `Segment` 是一个独立的哈希表。
- **Node**：在 Java 8 及之后版本中，`ConcurrentHashMap` 使用 `Node` 数组和 `TreeBin` 来实现并发控制。

**Java 8 及之后版本的结构**：
- **Node 数组**：存储哈希表的节点。
- **TreeBin**：当链表长度超过一定阈值时，链表会转换为红黑树（`TreeBin`）。
- **CAS 操作**：使用 CAS 操作（Compare-And-Swap）来保证线程安全。
- **锁机制**：使用 `synchronized` 关键字来锁定节点。

### 2. 扩容因子和扩容条件

#### 2.1 扩容因子（Load Factor）

**定义**：
扩容因子用于决定何时进行扩容。`ConcurrentHashMap` 默认的扩容因子是 0.75，
但与 `HashMap` 不同，`ConcurrentHashMap` 不直接使用扩容因子来决定扩容，而是通过控制链表长度和节点数量来决定。

#### 2.2 扩容条件

**Java 8 及之后版本**：
- **链表转换为红黑树**：当某个桶（bucket）中的链表长度超过 8 时，链表会转换为红黑树。
- **红黑树转换为链表**：当某个桶中的红黑树节点数量减少到 6 时，红黑树会转换回链表。
- **扩容**：当 `ConcurrentHashMap` 中的节点数量超过一定阈值时，会进行扩容。具体阈值与当前数组长度和负载因子有关。

**Java 7 及之前版本**：
- **扩容**：当某个 `Segment` 中的节点数量超过其容量的扩容因子时，会进行扩容。

### 3. put 过程

**Java 8 及之后版本**：
1. **计算哈希值**：计算键的哈希值，确定桶的位置。
2. **检查桶状态**：
    - **空桶**：使用 CAS 操作尝试插入新节点。
    - **链表**：使用 `synchronized` 锁定链表头节点，遍历链表并插入新节点。如果链表长度超过 8，则转换为红黑树。
    - **红黑树**：使用 `synchronized` 锁定红黑树的根节点，插入新节点。
3. **扩容**：如果需要，进行扩容操作。

**示例代码**：
```java
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapExample {
    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        // 插入数据
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        System.out.println(map);
    }
}
```


**解释**：
- **计算哈希值**：计算键的哈希值，确定桶的位置。
- **检查桶状态**：根据桶的状态（空桶、链表、红黑树）进行相应的插入操作。
- **扩容**：如果需要，进行扩容操作。

### 4. get 过程

**Java 8 及之后版本**：
1. **计算哈希值**：计算键的哈希值，确定桶的位置。
2. **检查桶状态**：
    - **空桶**：返回 `null`。
    - **链表**：遍历链表，找到匹配的节点并返回其值。
    - **红黑树**：遍历红黑树，找到匹配的节点并返回其值。

**示例代码**：
```java
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapExample {
    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        // 插入数据
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        // 获取数据
        String value1 = map.get("key1");
        String value2 = map.get("key2");
        String value3 = map.get("key3");

        System.out.println("key1: " + value1);
        System.out.println("key2: " + value2);
        System.out.println("key3: " + value3);
    }
}
```


**解释**：
- **计算哈希值**：计算键的哈希值，确定桶的位置。
- **检查桶状态**：根据桶的状态（空桶、链表、红黑树）进行相应的查找操作。

### 5. 如何保证线程安全

**Java 8 及之后版本**：
- **CAS 操作**：使用 CAS 操作来保证无锁的插入和删除操作。
- **synchronized 锁定**：在链表和红黑树的操作中使用 `synchronized` 关键字来锁定节点。
- **分段锁**：在 Java 7 及之前版本中使用多个 `Segment` 来实现分段锁，每个 `Segment` 是一个独立的哈希表。

**具体机制**：
- **CAS 操作**：用于无锁的插入和删除操作，确保线程安全。
- **synchronized 锁定**：在链表和红黑树的操作中使用 `synchronized` 关键字来锁定节点，确保线程安全。
- **扩容**：在扩容过程中，使用 CAS 操作和 `synchronized` 锁定来保证线程安全。

### 6. 详细过程

#### 6.1 put 过程

```java
    final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh; K fk; V fv;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value)))
                break;                   // no lock when adding to empty bin
        }
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else if (onlyIfAbsent // check first node without acquiring lock
                && fh == hash
                && ((fk = f.key) == key || (fk != null && key.equals(fk)))
                && (fv = f.val) != null)
            return fv;
        else {
            V oldVal = null;
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                            (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key, value);
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                    else if (f instanceof ReservationNode)
                        throw new IllegalStateException("Recursive update");
                }
            }
            if (binCount != 0) {
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    addCount(1L, binCount);
    return null;
}
```

1. 判断key或value为null，null则报空指针异常 hashmap是允许key value为null的元数据的
2. 对key进行hash计算  高16位与低16位进行异或操作，以扩散高位的影响到低位 
3. 检查hash表是否为空，如果为空，通过initTable()初始化hash表
4. (n-1)&hash 判断目标位置是否为空 如果为空，则使用casTabAt()方法将Node对象插入到hash表中
5. 如果正在扩容（fh == MOVED） 调用helpTransfer协助扩容。
6. 若onlyIfAbsent为true且当前节点的键值与插入键值相同，则直接返回已有值，避免覆盖。
7. hash冲突下的插入操作，桶位置加锁，遍历链表，查找键值是否相同，如果相同则返回已有值，否则插入新节点。  
   * 如果桶位置是链表结构，则锁住链表头节点；
   * 如果是红黑树，则锁住树的根节点
8. 链表插入新节点 插入以后，判断是否需要转为红黑树，长度>8
9. 更新计数  用于检查是否需要触发扩容逻辑


# 高16位与低16位异或的原因

    在 Java 的 `HashMap` 中，会对键的 `hashCode` 值的高 16 位和低 16 位进行异或（XOR）操作，这主要是为了让哈希值更加均匀地分布，从而减少哈希冲突的发生。下面详细解释其中的原理。
    ### `HashMap` 存储原理概述
    `HashMap` 是基于哈希表实现的，它通过哈希函数将键映射到数组的特定位置（桶）。在 Java 中，`HashMap` 内部有一个数组，每个数组元素称为一个桶（bucket），
    键值对会根据键的哈希值被存储到对应的桶中。当多个键的哈希值映射到同一个桶时，就会发生哈希冲突。
    ### 异或操作的作用
    #### 减少哈希冲突
    - **哈希函数与数组索引计算**：`HashMap` 在确定键值对存储的桶位置时，会使用如下公式计算索引：`index = (n - 1) & hash`，其中 `n` 是 `HashMap` 数组的长度，`hash` 是键的哈希值。在大多数情况下，`HashMap` 的数组长度 `n` 不会很大，一般是 2 的幂次方。例如，初始长度为 16（即 `n = 16`），二进制表示为 `0001 0000`，`n - 1` 的二进制表示为 `0000 1111`。
    - **问题分析**：当使用 `(n - 1) & hash` 计算索引时，实际上只使用了 `hash` 值的低几位。如果 `hash` 值的高位变化对索引计算没有影响，那么即使键的 `hashCode` 值差异很大，但低几位相同，就会导致这些键映射到同一个桶中，增加哈希冲突的概率。
    - **异或操作的解决方案**：通过将 `hashCode` 值的高 16 位和低 16 位进行异或操作，可以让高位的信息也参与到索引的计算中。异或操作的特点是相同为 0，不同为 1。这样可以让哈希值的分布更加均匀，减少哈希冲突的发生。以下是 Java 8 中 `HashMap` 计算哈希值的代码：


