# 比如商品，发布时间，销量，好评数


# 场景1-按销量排行

stringRedisTemplate.opsForZSet().add("A","1001","销量");
stringRedisTemplate.opsForZSet().reverseRange(rankKey, 0, 4);

# 场景2-销量一样情况下，按好评率高低排序
stringRedisTemplate.opsForZSet().add("A","1001","销量:好评数");
stringRedisTemplate.opsForZSet().add("A","1001","销量:发布时间");

# 场景3-销量一样的情况下，按上架时间最新靠前排列
stringRedisTemplate.opsForZSet().add("A","1001","销量:发布时间");
