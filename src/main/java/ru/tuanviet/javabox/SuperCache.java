package ru.tuanviet.javabox;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class SuperCache<K, V> extends AbstractMap<K, V> {
    private final static long ACCURACY_TTL = 100;
    private final static int DEFAULT_MAXSIZE = 10;
    private final SuperReadWriteLock locker;
    private final int maxSize;
    private final long ttl;
    private final List<SuperEntry<K, V>> superCache = new ArrayList<>();

    public SuperCache(long ttl) {
        this(ttl, DEFAULT_MAXSIZE);
    }

    public SuperCache(long ttl, int maxSize) {
        locker = new SuperReadWriteLock();
        this.maxSize = maxSize;
        this.ttl = ttl;
        Thread watcher = new Thread(new Watcher());
        watcher.start();
    }

    public synchronized V getOrCompute(K key, Supplier<V> valueSupplier) {
        return null;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public long getTtl() {
        return ttl;
    }

    public SuperReadWriteLock getLocker() {
        return locker;
    }

    private class Watcher implements Runnable {
        @Override
        public void run() {
            while (true) {
                for (SuperEntry<K, V> elem : superCache) {
                    System.out.println(elem.getKey());
                }
                try {
                    Thread.sleep(ACCURACY_TTL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class SuperEntry<K, V> implements Entry<K, V> {

        private final K key;
        private V value;

        public SuperEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SuperEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

//        public boolean equals(Object o) {
//            if (!(o instanceof Map.Entry))
//                return false;
//            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
//            return eq(key, e.getKey()) && eq(value, e.getValue());
//        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return key + "=" + value;
        }

    }
}
