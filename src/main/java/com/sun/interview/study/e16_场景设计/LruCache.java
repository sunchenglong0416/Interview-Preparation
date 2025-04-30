package com.sun.interview.study.e16_场景设计;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    public LruCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    public static void main(String[] args) {
        LruCache<String, String>  cache = new LruCache<>(3);
        // 使用 Collections.synchronizedMap 包装成线程安全的 Map
        Map<String, String> threadSafeCache = Collections.synchronizedMap(cache);
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
