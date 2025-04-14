核心架构
TDengine 的核心架构主要由管理节点（MN）、数据节点（DN）、客户端三部分组成，各部分协同工作：
- **管理节点（MN）**：作为集群的“大脑”，负责管理集群元数据，像节点信息、数据库和表的结构等。同时还掌控集群拓扑，处理节点的加入、退出等情况，保证集群稳定运行。
- **数据节点（DN）**：是数据存储和查询处理的“主力军”。负责接收客户端的数据写入请求，将数据持久化到磁盘。当接收到查询请求时，会根据请求条件从本地磁盘读取数据进行处理。
- **客户端**：是用户与集群交互的接口，用户可以使用客户端程序（如命令行工具、应用程序）发送 SQL 语句来完成数据的写入、查询等操作。

最小高可用集群搭建
要搭建最小高可用集群，至少需要三个节点，以下是具体步骤：
1. **环境准备**：确保各节点操作系统版本一致，网络连通，且有足够的磁盘空间和内存资源。
2. **下载安装**：从 TDengine 官方网站下载适合的安装包，在每个节点上进行安装。
3. **配置文件修改**：编辑每个节点的 `taos.cfg` 文件，设置节点角色和参数，比如指定管理节点地址、数据存储路径等。
4. **启动节点**：依次启动各个节点，TDengine 会自动完成节点发现和集群组建。
5. **验证集群状态**：使用 `taos` 命令连接到集群，执行 `SHOW DATABASES;` 等命令，确认集群正常工作。

超级表和子表概念
- **超级表**：是一种抽象的表结构，定义了一类数据的通用模式，包含公共列和标签（Tag）。超级表本身不存储数据，主要用于创建和管理子表。
- **子表**：是超级表的具体实例，继承了超级表的结构。每个子表有唯一的标签值，用于标识数据来源或特征，实际存储数据。

利用 Tag 对数据分类
Tag 是 TDengine 中用于标识和区分不同数据源或实体的属性。可以通过以下方式利用 Tag 对数据进行分类：
- **创建超级表时定义 Tag**：在创建超级表时，明确指定 Tag 列，例如：
```sql
CREATE STABLE st (ts TIMESTAMP, value DOUBLE) TAGS (location INT, device_id INT);
```
这里的 `location` 和 `device_id` 就是 Tag。
- **创建子表时指定 Tag 值**：创建子表时，为每个 Tag 赋予具体的值，以此区分不同的子表。例如：
```sql
CREATE TABLE t1 USING st TAGS (1, 101);
CREATE TABLE t2 USING st TAGS (2, 102);
```
这样，就可以根据不同的 Tag 值对数据进行分类，在查询时可以根据 Tag 条件筛选出特定类别的数据。

设备数量超多时提前批量初始化子表
当TDengine中设备数量超多时，提前批量初始化子表是一个提高系统性能和稳定性的有效方法。下面详细介绍相关内容：
### 批量初始化子表的优势
- **提高写入性能**：提前初始化子表可以避免在写入数据时动态创建子表的开销，减少了写入路径上的潜在阻塞点，从而提高数据写入的速度。
- **优化查询性能**：预先创建好子表有助于查询规划器更高效地生成查询计划，因为表结构已经确定，查询优化器可以更好地利用索引和其他优化策略来加速查询。
- **资源预分配**：批量初始化子表可以让系统提前分配相关的存储资源，避免在运行时因频繁创建子表而导致的资源碎片和动态分配开销。
### 实现方式
- **使用脚本语言批量创建**：可以利用Python、Java等编程语言结合TDengine的客户端库来实现批量创建子表。以Python为例，使用`taos`库连接到TDengine数据库，循环生成创建子表的SQL语句并执行。示例代码如下：
```python
import taos
# 连接到TDengine数据库
conn = taos.connect(host='localhost', user='root', password='taosdata', database='mydb')
cursor = conn.cursor()
# 假设设备数量为10000
num_devices = 10000
for i in range(1, num_devices + 1):
    table_name = f"device_{i}"
    # 假设超级表名为meters，根据实际情况修改
    sql = f"CREATE TABLE IF NOT EXISTS {table_name} USING meters TAGS ('location_{i}', {i})"
    cursor.execute(sql)
# 关闭游标和连接
cursor.close()
conn.close()
```
- **使用TDengine的命令行工具**：如果设备数量不是特别巨大，也可以在TDengine的命令行中使用循环语句来批量创建子表。例如，在Linux环境下，可以使用`for`循环结合`taos`命令行工具来实现。示例如下：
```bash
for i in $(seq 1 10000); do
    taos -s "CREATE TABLE IF NOT EXISTS device_$i USING meters TAGS ('location_$i', $i)"
done
```
### 注意事项
- **合理规划资源**：创建大量子表可能会消耗大量的系统资源，包括CPU、内存和磁盘空间。在进行批量初始化之前，需要评估数据库服务器的硬件资源，确保有足够的资源来支持子表的创建和后续的使用。
- **监控创建过程**：由于创建大量子表可能需要较长时间，建议监控创建过程，及时发现并处理可能出现的错误。可以通过查看数据库的日志文件或者使用TDengine提供的监控工具来监控创建操作的进度和状态。
- **考虑并发操作**：如果在初始化子表的同时，还有其他业务操作在进行，需要考虑并发操作可能带来的影响。确保批量创建子表的操作不会对其他重要业务造成性能影响，必要时可以调整创建子表的时间或者采用适当的并发控制策略。
- **数据一致性和完整性**：在批量初始化子表时，要确保子表的结构和数据一致性。检查创建子表的SQL语句是否正确，以及标签和其他参数的设置是否符合业务需求，避免因初始化不当导致数据不一致或查询异常。
  子表时序存储高效查询的原理
- **列式存储**：TDengine 采用列式存储，同一列的数据连续存储。查询时只需读取所需列，减少了不必要的数据读取，提高了查询效率。
- **时间分区**：按照时间对数据进行分区存储，每个分区内的数据按时间顺序排列。查询时可根据时间范围快速定位到目标分区，缩小数据扫描范围。
- **索引机制**：为时间戳和标签建立索引，通过索引能快速定位符合条件的数据。

时间戳和标签使用的索引数据结构
TDengine 中时间戳和标签使用的索引数据结构是 B+ 树。B+ 树具有良好的平衡性和查找性能，能够在大规模数据下快速定位到目标数据，适合处理范围查询和排序操作。

单表容量不断增加对查询效率的影响
- **数据扫描范围增大**：单表数据量增加，查询时需扫描的数据增多，导致查询时间变长。
- **索引效率降低**：随着数据量增大，如果索引未及时更新优化，其效率会下降，影响查询速度。
- **内存压力增大**：大量数据可能导致内存不足，需频繁进行磁盘 I/O 操作，降低查询效率。

数据定期归档如何配置
TDengine 可通过设置数据保留策略实现数据定期归档，具体方法如下：
- **创建数据库时设置保留策略**：创建数据库时使用 `KEEP` 关键字指定数据保留时间。例如：
```sql
CREATE DATABASE db_name KEEP 365; -- 保留 365 天的数据
```
- **修改现有数据库的保留策略**：使用 `ALTER DATABASE` 语句修改保留策略。例如：
```sql
ALTER DATABASE db_name KEEP 730; -- 将保留时间修改为 730 天
```
TDengine 会自动根据保留策略对过期数据进行归档和清理，释放存储空间。
子表重复数据写入处理
在 TDengine 中，子表写入重复数据时的情况分以下两种：
- **无复合主键时**：
  TDengine 中首个时间戳字段起到主键的作用，不能有重复的时间戳。如果向子表中写入具有相同时间戳的数据，在 `UPDATE` 参数不同设置下，处理方式有所不同。当 `UPDATE` 为 0 时（默认值），后发送的相同时间戳的数据会被直接丢弃，但不会报错，而且仍然会被计入 `affected rows`；当 `UPDATE` 为 1 时，表示后写入的数据覆盖先写入的数据；当 `UPDATE` 为 2 时，如果更新一个数据行，其中某些列没有提供取值，那么这些列会保持原有数据行中的对应值。
- **有复合主键时**：
  从 TDengine 3.3.0.0 版本之后，新增了复合主键的功能。只有当时间戳相同且 `PRIMARY KEY` 列值都相同时，两行数据才会被认为是重复数据，此时会在合并更新后返回，不会报错。如果时间戳相同但 `Primary key` 较小数据后写入，则视为乱序数据处理。
  另外，如果写入的数据不符合表结构定义，如数据类型不匹配、违反非空约束等，TDengine 会返回错误信息。
  SpringBoot整合TDengine读写超级表和子表
### 1. 项目依赖添加
在 `pom.xml` 里添加 TDengine JDBC 驱动的依赖：
```xml
<dependencies>
    <!-- TDengine JDBC 驱动 -->
    <dependency>
        <groupId>com.taosdata.jdbc</groupId>
        <artifactId>taos-jdbcdriver</artifactId>
        <version>3.0.1</version>
    </dependency>
    <!-- Spring Boot JDBC 依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
</dependencies>
```

### 2. 配置文件设置
在 `application.properties` 或者 `application.yml` 里配置 TDengine 的连接信息：
```properties
spring.datasource.url=jdbc:TAOS://localhost:6030/testdb
spring.datasource.username=root
spring.datasource.password=taosdata
spring.datasource.driver-class-name=com.taosdata.jdbc.TSDBDriver
```

### 3. 创建数据访问对象（DAO）
创建一个 DAO 类，借助 Spring 的 `JdbcTemplate` 来操作 TDengine。
doubaocanvas:
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class TDengineDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建超级表
     */
    public void createSuperTable() {
        String sql = "CREATE STABLE IF NOT EXISTS meters (ts TIMESTAMP, current FLOAT, voltage INT, phase FLOAT) TAGS (location BINARY(64), groupId INT)";
        jdbcTemplate.execute(sql);
    }

    /**
     * 创建子表
     */
    public void createSubTable(String tableName, String location, int groupId) {
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s USING meters TAGS ('%s', %d)", tableName, location, groupId);
        jdbcTemplate.execute(sql);
    }

    /**
     * 向子表写入数据
     */
    public void insertData(String tableName, Timestamp ts, float current, int voltage, float phase) {
        String sql = String.format("INSERT INTO %s VALUES ('%s', %f, %d, %f)", tableName, ts, current, voltage, phase);
        jdbcTemplate.execute(sql);
    }

    /**
     * 从子表读取数据
     */
    public void readData(String tableName) {
        String sql = String.format("SELECT * FROM %s", tableName);
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("ts");
            float current = rs.getFloat("current");
            int voltage = rs.getInt("voltage");
            float phase = rs.getFloat("phase");
            System.out.printf("Time: %s, Current: %f, Voltage: %d, Phase: %f%n", ts, current, voltage, phase);
            return null;
        });
    }
}



### 4. 服务层创建
创建一个服务类，调用 DAO 类的方法：
doubaocanvas:

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class TDengineService {

    @Autowired
    private TDengineDao tdengineDao;

    public void operateTDengine() {
        // 创建超级表
        tdengineDao.createSuperTable();

        // 创建子表
        String tableName = "d1001";
        tdengineDao.createSubTable(tableName, "Beijing.Chaoyang", 2);

        // 向子表写入数据
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        tdengineDao.insertData(tableName, ts, 10.1f, 220, 0.5f);

        // 从子表读取数据
        tdengineDao.readData(tableName);
    }
}


### 5. 控制器创建（可选）
要是需要通过 RESTful 接口来操作，可创建一个控制器类：
doubaocanvas:
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TDengineController {

    @Autowired
    private TDengineService tdengineService;

    @GetMapping("/operate-tdengine")
    public String operateTDengine() {
        tdengineService.operateTDengine();
        return "TDengine operation completed.";
    }
}

### 6. 运行项目
启动 Spring Boot 项目，访问 `http://localhost:8080/operate-tdengine`（如果使用了控制器），就可以看到对超级表和子表的读写操作结果。

### 总结
通过上述步骤，你就能在 Spring Boot 项目里整合 TDengine，实现对超级表和子表的读写操作。核心在于借助 `JdbcTemplate` 来执行 SQL 语句，从而完成数据的增删改查。 