设计一个短URL系统是一个经典的软件工程问题，涉及到URL编码、数据库设计、并发处理等多个方面。下
面是一个简单的设计方案，使用Java实现。

### 1. 系统需求
- **生成短URL**：用户输入长URL，系统生成一个唯一的短URL。
- **重定向**：用户访问短URL时，系统重定向到原始的长URL。
- **统计访问量**：记录每个短URL的访问次数。
- **高可用性**：系统需要能够处理大量的请求。

### 2. 系统架构
- **前端**：简单的Web界面，用户可以输入长URL并获取短URL。
- **后端**：处理URL生成、重定向和统计。
- **数据库**：存储长URL和短URL的映射关系，以及访问统计信息。

### 3. 技术选型
- **编程语言**：Java
- **Web框架**：Spring Boot
- **数据库**：MySQL
- **缓存**：Redis（可选，用于提高性能）

### 4. 数据库设计
- **url_mapping**：存储长URL和短URL的映射关系。
    - `id`：主键，自增。
    - `long_url`：原始的长URL。
    - `short_url`：生成的短URL。
    - `created_at`：创建时间。
- **url_stats**：存储短URL的访问统计信息。
    - `short_url`：短URL。
    - `visit_count`：访问次数。
    - `last_visited`：最后访问时间。

### 5. 短URL生成算法
可以使用Base62编码来生成短URL。Base62编码使用字符集`[0-9a-zA-Z]`，可以生成较短的字符串。

```java
public class ShortUrlGenerator {
    private static final String CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHAR_SET.length();

    public static String generateShortUrl(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(CHAR_SET.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }
}
```


### 6. 后端实现
#### 6.1 创建Spring Boot项目
使用Spring Initializr创建一个新的Spring Boot项目，添加Web、JPA、MySQL依赖。

#### 6.2 实体类
```java
@Entity
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String longUrl;

    @Column(unique = true)
    private String shortUrl;

    private LocalDateTime createdAt;

    // getters and setters
}

@Entity
public class UrlStats {
    @Id
    private String shortUrl;

    private int visitCount;

    private LocalDateTime lastVisited;

    // getters and setters
}
```


#### 6.3 仓库接口
```java
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortUrl(String shortUrl);
    Optional<UrlMapping> findByLongUrl(String longUrl);
}

public interface UrlStatsRepository extends JpaRepository<UrlStats, String> {
}
```


#### 6.4 服务层
```java
@Service
public class UrlService {
    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private UrlStatsRepository urlStatsRepository;

    public String generateShortUrl(String longUrl) {
        Optional<UrlMapping> existingMapping = urlMappingRepository.findByLongUrl(longUrl);
        if (existingMapping.isPresent()) {
            return existingMapping.get().getShortUrl();
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortUrl(ShortUrlGenerator.generateShortUrl(mapping.getId()));
        mapping.setCreatedAt(LocalDateTime.now());
        urlMappingRepository.save(mapping);

        UrlStats stats = new UrlStats();
        stats.setShortUrl(mapping.getShortUrl());
        stats.setVisitCount(0);
        stats.setLastVisited(LocalDateTime.now());
        urlStatsRepository.save(stats);

        return mapping.getShortUrl();
    }

    public String getLongUrl(String shortUrl) {
        Optional<UrlMapping> mapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (mapping.isPresent()) {
            UrlStats stats = urlStatsRepository.findById(shortUrl).orElseThrow(() -> new RuntimeException("URL not found"));
            stats.setVisitCount(stats.getVisitCount() + 1);
            stats.setLastVisited(LocalDateTime.now());
            urlStatsRepository.save(stats);
            return mapping.get().getLongUrl();
        }
        throw new RuntimeException("URL not found");
    }
}
```


#### 6.5 控制器
```java
@RestController
@RequestMapping("/api")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl) {
        String shortUrl = urlService.generateShortUrl(longUrl);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrl) {
        String longUrl = urlService.getLongUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
}
```

