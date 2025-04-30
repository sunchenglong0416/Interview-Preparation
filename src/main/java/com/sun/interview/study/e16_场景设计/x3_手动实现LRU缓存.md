~~~java
package com.sun.interview.study.e16_场景设计;

import java.util.LinkedHashMap;

//1:基于linkedHashMap实现缓存
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    //2: 定义容量
    private final int capacity;

    public LruCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    //3: 重写removeEldestEntry方法，当超过容量时，删除最老的元素
    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > capacity;
    }


    public static void main(String[] args) {
        LruCache<String, String>  cache = new LruCache<>(3);
        // 使用 Collections.synchronizedMap 包装成线程安全的 Map
        Map<Integer, String> threadSafeCache = Collections.synchronizedMap(lruCache);
        threadSafeCache.put("1","1");
        threadSafeCache.put("2","2");
        threadSafeCache.put("3","3");
        System.out.println(cache);
        threadSafeCache.get("1");
        System.out.println(cache);
        threadSafeCache.put("4","4");
        System.out.println(cache);
    }
}
~~~