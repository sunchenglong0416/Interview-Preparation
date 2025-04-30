# 三节点ES高可用集群搭建

在三台物理机器上部署一个最小的高可用 Elasticsearch 集群，每台机器配置为 8 核心（8C）和 64GB 内存（64G），需要合理配置分片和主节点以确保集群的稳定性和高可用性。以下是一个详细的配置方案。

### 1. 集群配置概述

**目标**：
- **高可用性**：确保集群在节点故障时仍然可用。
- **性能**：合理利用资源，提高搜索和索引性能。
- **最小化配置**：使用最少的配置来实现高可用性。

**配置**：
- **节点数量**：3 台物理机器。
- **主分片**：每个索引至少有一个主分片。
- **副本分片**：每个主分片有一个副本分片。
- **主节点**：每个节点都可以作为主节点，但需要确保集群中至少有一个主节点可用。

### 2. 配置分片和副本

**分片配置**：
- **主分片数量**：每个索引至少有一个主分片。
- **副本分片数量**：每个主分片有一个副本分片。

**示例**：
假设有一个索引 `my_index`，包含 3 个主分片和 1 个副本分片。

```json
PUT /my_index
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "content": { "type": "text" },
      "date": { "type": "date" }
    }
  }
}
```


**解释**：
- **`number_of_shards`**：设置索引的主分片数量为 3。
- **`number_of_replicas`**：设置每个主分片的副本数量为 1。

### 3. 配置主节点

**主节点配置**：
- **主节点**：每个节点都可以作为主节点，但需要确保集群中至少有一个主节点可用。
- **配置参数**：`node.master` 和 `node.data`。

**示例配置**：
每台机器的 `elasticsearch.yml` 文件配置如下：

**Node 1 (`node1`)**：
```yaml
cluster.name: my_cluster
node.name: node1
network.host: 192.168.1.1
discovery.seed_hosts: ["192.168.1.1", "192.168.1.2", "192.168.1.3"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]
node.master: true
node.data: true
```


**Node 2 (`node2`)**：
```yaml
cluster.name: my_cluster
node.name: node2
network.host: 192.168.1.2
discovery.seed_hosts: ["192.168.1.1", "192.168.1.2", "192.168.1.3"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]
node.master: true
node.data: true
```


**Node 3 (`node3`)**：
```yaml
cluster.name: my_cluster
node.name: node3
network.host: 192.168.1.3
discovery.seed_hosts: ["192.168.1.1", "192.168.1.2", "192.168.1.3"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]
node.master: true
node.data: true
```


**解释**：
- **`cluster.name`**：集群名称。
- **`node.name`**：节点名称。
- **`network.host`**：节点的网络地址。
- **`discovery.seed_hosts`**：种子节点列表，用于节点发现。
- **`cluster.initial_master_nodes`**：初始主节点列表，用于集群初始化。
- **`node.master`**：设置节点可以作为主节点。
- **`node.data`**：设置节点可以存储数据。

### 4. 详细配置步骤

#### 4.1 配置每台机器的 `elasticsearch.yml`

**Node 1 (`node1`)**：
```yaml
cluster.name: my_cluster
node.name: node1
network.host: 192.168.1.1
discovery.seed_hosts: ["192.168.1.1", "192.168.1.2", "192.168.1.3"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]
node.master: true
node.data: true
```


**Node 2 (`node2`)**：
```yaml
cluster.name: my_cluster
node.name: node2
network.host: 192.168.1.2
discovery.seed_hosts: ["192.168.1.1", "192.168.1.2", "192.168.1.3"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]
node.master: true
node.data: true
```


**Node 3 (`node3`)**：
```yaml
cluster.name: my_cluster
node.name: node3
network.host: 192.168.1.3
discovery.seed_hosts: ["192.168.1.1", "192.168.1.2", "192.168.1.3"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]
node.master: true
node.data: true
```


**解释**：
- **`cluster.name`**：集群名称。
- **`node.name`**：节点名称。
- **`network.host`**：节点的网络地址。
- **`discovery.seed_hosts`**：种子节点列表，用于节点发现。
- **`cluster.initial_master_nodes`**：初始主节点列表，用于集群初始化。
- **`node.master`**：设置节点可以作为主节点。
- **`node.data`**：设置节点可以存储数据。

#### 4.2 启动每台机器上的 Elasticsearch

**启动命令**：
```sh
./bin/elasticsearch
```


**解释**：
- 启动每台机器上的 Elasticsearch 服务。

### 5. 验证集群状态

**验证集群健康状态**：
```sh
GET /_cluster/health
```


**预期输出**：
```json
{
  "cluster_name" : "my_cluster",
  "status" : "green",
  "timed_out" : false,
  "number_of_nodes" : 3,
  "number_of_data_nodes" : 3,
  "active_primary_shards" : 3,
  "active_shards" : 6,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 0,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 100.0
}
```


**解释**：
- **`status`**：集群的健康状态为 `green`，表示所有主分片和副本分片都可用。
- **`number_of_nodes`**：集群中有 3 个节点。
- **`active_primary_shards`**：有 3 个主分片。
- **`active_shards`**：有 6 个活跃分片（3 个主分片 + 3 个副本分片）。

### 6. 配置索引分片和副本

**创建索引并配置分片和副本**：
```json
PUT /my_index
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "content": { "type": "text" },
      "date": { "type": "date" }
    }
  }
}
```


**解释**：
- **`number_of_shards`**：设置索引的主分片数量为 3。
- **`number_of_replicas`**：设置每个主分片的副本数量为 1。

### 7. 高可用性验证

**模拟节点故障**：
1. **停止 Node 1**：
   ```sh
   ./bin/elasticsearch-stop
   ```


2. **验证集群状态**：
   ```sh
   GET /_cluster/health
   ```


**预期输出**：
```json
{
  "cluster_name" : "my_cluster",
  "status" : "yellow",
  "timed_out" : false,
  "number_of_nodes" : 2,
  "number_of_data_nodes" : 2,
  "active_primary_shards" : 3,
  "active_shards" : 3,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 3,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 50.0
}
```


**解释**：
- **`status`**：集群的健康状态为 `yellow`，表示所有主分片可用，但部分副本分片不可用。
- **`number_of_nodes`**：集群中有 2 个节点。
- **`active_primary_shards`**：有 3 个主分片。
- **`active_shards`**：有 3 个活跃分片（3 个主分片）。
- **`unassigned_shards`**：有 3 个未分配的分片（3 个副本分片）。

**恢复 Node 1**：
1. **启动 Node 1**：
   ```sh
   ./bin/elasticsearch
   ```


2. **验证集群状态**：
   ```sh
   GET /_cluster/health
   ```


**预期输出**：
```json
{
  "cluster_name" : "my_cluster",
  "status" : "green",
  "timed_out" : false,
  "number_of_nodes" : 3,
  "number_of_data_nodes" : 3,
  "active_primary_shards" : 3,
  "active_shards" : 6,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 0,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 100.0
}
```


**解释**：
- **`status`**：集群的健康状态为 `green`，表示所有主分片和副本分片都可用。
- **`number_of_nodes`**：集群中有 3 个节点。
- **`active_primary_shards`**：有 3 个主分片。
- **`active_shards`**：有 6 个活跃分片（3 个主分片 + 3 个副本分片）。

# 3节点集群挂了一个后如何选主
    ES 7.x 及之后版本
    采用分布式仲裁机制：ES 7.x 及之后版本移除了 minimum_master_nodes 参数，利用分布式仲裁机制来选主，通过多数原则确保选出的主节点得到大多数节点的认可。
    挂掉 1 个节点后的选主情况：在 3 节点集群挂掉 1 个节点后，剩下 2 个节点。由于此时具备主节点资格的节点数量为 2，超过了剩余节点数量的半数（1），所以这 2 个节点会进行选主，具体过程如下：
    节点发现与通信：剩余的 2 个具备主节点资格的节点会通过节点发现机制找到彼此，并开始交换状态信息。
    发起选举：当节点发现当前集群没有主节点时，会触发选举流程。每个节点都会向对方发送投票请求，争取成为主节点。
    投票与决策：节点会根据对方的优先级和节点 ID 等因素进行投票。若一个节点获得了另一个节点的投票，也就是获得了超过半数（这里是 1 票以上）的投票，它就会被选为新的主节点。
    主节点确立：当选出主节点后，主节点会向另一个节点发送通知，告知其自己已成为主节点，另一个节点会更新自身的集群状态信息。


# 生产环境ES服务配置优化

## jvm内存设置

* 内存分配
  * 堆内存配置：Elasticsearch 的堆内存（Heap Memory）是其性能的关键因素。通常建议将堆内存设置为物理内存的 50%，但不超过 32GB。
    * 推荐配置：
    堆内存大小：建议设置为物理内存的 50%，但不超过 32GB。
    示例：对于 64GB 内存的节点，建议将堆内存设置为 32GB。
    * jvm.options 文件中设置堆内存大小，避免动态分配内存带来的性能影响
          * -Xms32g
          * -Xmx32g
  * 非堆内存配置：确保非堆内存（Non-Heap Memory）也得到合理配置，以支持文件系统缓存和其他操作。
      * 在 elasticsearch.yml 文件中设置文件系统缓存。
        * bootstrap.memory_lock: true
        * bootstrap.memory_lock：锁定 JVM 堆内存，防止操作系统交换内存。
  * 堆外内存：Elasticsearch 使用堆外内存来存储一些数据结构，如 Lucene 的索引数据。确保有足够的堆外内存。
     * -XX:MaxDirectMemorySize=16g 设置堆外内存的最大大小。
* 垃圾回收器
  * 选择合适的垃圾回收器（Garbage Collector, GC）以优化内存回收性能
  * G1 GC：G1 垃圾回收器适用于大堆内存场景，能够提供较好的暂停时间和吞吐量。
* 禁用压缩指针
  * 对于大堆内存配置，禁用压缩指针可以减少内存碎片和提高性能。
