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

    @Override
    public int size() {
        return superCache.size();
    }

    @Override
    public boolean isEmpty() {
        return superCache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                V oldValue = pair.getValue();
                pair.setValue(value);
                return oldValue;
            }
        }
        superCache.add(new SuperEntry<K, V>(key, value));
        return null;
    }

    @Override
    public V remove(Object key) {
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                V oldValue = pair.getValue();
                superCache.remove(pair);
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        superCache.clear();
    }

    @Override
    public Set<K> keySet() {
        Set<K> tmpSet = new HashSet<>();
        for (SuperEntry<K, V> pair : superCache) {
            tmpSet.add(pair.key);
        }
        return tmpSet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> tmpColl = new ArrayList<>();
        for (SuperEntry<K, V> pair : superCache) {
            tmpColl.add(pair.value);
        }
        return tmpColl;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public synchronized V getOrCompute(K key, Supplier<V> valueSupplier) {
//        this.containsKey(key);
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                return pair.getValue();
            }
        }
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

    public static class SuperEntry<K, V> implements Entry<K, V> {

        private final K key;
        private V value;
        private long lastUsed;

        public SuperEntry(K key, V value) {
            this.key = key;
            this.value = value;
            this.lastUsed = System.currentTimeMillis();
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

        public long getLastUsed() {
            return lastUsed;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return key + "=" + value;
        }

        private static boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }

    }

    private class Watcher implements Runnable {

        @Override
        public void run() {
            while (true) {
                for (SuperEntry<K, V> elem : superCache) {
                    //System.out.println(elem.getKey());
                }
                try {
                    Thread.sleep(ACCURACY_TTL);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Crash watcher", e);
                }
            }
        }
    }
}
