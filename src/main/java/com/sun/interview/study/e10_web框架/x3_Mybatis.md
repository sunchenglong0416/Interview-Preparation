MyBatis 一级缓存和二级缓存详解及默认开启情况
在 MyBatis 中，缓存机制可以显著提高数据库操作的性能。MyBatis 提供了一级缓存（Local Cache）和二级缓存（Second Level Cache）两种缓存机制。以下是详细的介绍以及默认开启的情况。
1. **一级缓存（Local Cache）**
   1.1 **定义**
- **一级缓存**：也称为本地缓存，是 SqlSession 级别的缓存。
- **作用**：在同一个 SqlSession 内，如果执行相同的 SQL 语句，MyBatis 会从缓存中直接返回结果，避免重复查询数据库。
  1.2 **缓存范围**
- **范围**：仅限于同一个 SqlSession 内。
- **生命周期**：SqlSession 关闭或提交后，一级缓存会被清空。
  1.3 **默认开启情况**
- **默认开启**：一级缓存默认是开启的，无需额外配置。
- **配置项**：可以通过 settings 中的 localCacheScope 进行配置。

- SESSION（默认）：一级缓存作用于整个 SqlSession。
- STATEMENT：一级缓存仅作用于当前的 Statement，即每次查询都会清空缓存。
  1.4 **优点**
- **提高性能**：减少对数据库的重复查询。
- **简单易用**：默认开启，无需额外配置。
  1.5 **示例**
  try (SqlSession session = sqlSessionFactory.openSession()) {
  UserMapper mapper = session.getMapper(UserMapper.class);
  User user1 = mapper.selectUserById(1); // 查询数据库
  User user2 = mapper.selectUserById(1); // 从缓存中获取
  }

2. **二级缓存（Second Level Cache）**
   2.1 **定义**
- **二级缓存**：是 Mapper 级别的缓存，多个 SqlSession 可以共享同一个 Mapper 的缓存。
- **作用**：在同一个 Mapper 内，如果执行相同的 SQL 语句，MyBatis 会从缓存中直接返回结果，避免重复查询数据库。
  2.2 **缓存范围**
- **范围**：多个 SqlSession 可以共享同一个 Mapper 的缓存。
- **生命周期**：Mapper 的缓存可以在多个 SqlSession 之间共享，直到缓存被清空或过期。
  2.3 **默认开启情况**
- **默认关闭**：二级缓存默认是关闭的，需要手动开启。
- **配置项**：

- **全局配置**：在
  settings 中设置 cacheEnabled 为 true。
  <settings>
  <setting name="cacheEnabled" value="true"/>
  </settings>

- **Mapper 配置**：在 Mapper XML 文件中配置
  <cache> 标签。
  <mapper namespace="com.example.mapper.UserMapper">
  <cache/>
  <select id="selectUserById" resultType="User">
  SELECT * FROM users WHERE id = #{id}
  </select>
  </mapper>

2.4 **优点**
- **提高性能**：减少对数据库的重复查询。
- **缓存范围广**：多个 SqlSession 可以共享缓存。
  2.5 **缺点**
- **复杂性**：需要额外配置和管理。
- **数据一致性问题**：如果数据被修改，需要手动或自动刷新缓存，否则可能导致数据不一致。
- **适用场景**：适用于读多写少的场景，且数据一致性要求不高。
  2.6 **示例**
  1.**启用二级缓存**：
  <settings>
  <setting name="cacheEnabled" value="true"/>
  </settings>

2.**配置 Mapper 缓存**：
<mapper namespace="com.example.mapper.UserMapper">
<cache/>
<select id="selectUserById" resultType="User">
SELECT * FROM users WHERE id = #{id}
</select>
</mapper>

3.**确保对象可序列化**：
- 二级缓存中的对象需要实现 Serializable 接口。
  public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  private int id;
  private String name;
  // 其他字段和方法
  }

3. **总结**
   3.1 **一级缓存**
- **默认开启**：一级缓存默认是开启的，无需额外配置。
- **缓存范围**：仅限于同一个 SqlSession 内。
- **优点**：提高性能，简单易用。
- **适用场景**：适用于读多写少的场景。
  3.2 **二级缓存**
- **默认关闭**：二级缓存默认是关闭的，需要手动开启。
- **缓存范围**：多个 SqlSession 可以共享同一个 Mapper 的缓存。
- **优点**：提高性能，缓存范围广。
- **缺点**：复杂性增加，数据一致性问题。
- **适用场景**：适用于读多写少的场景，且数据一致性要求不高。
  通过理解 MyBatis 的一级缓存和二级缓存机制及其默认开启情况，可以更好地利用缓存提高系统性能，同时避免潜在的问题。