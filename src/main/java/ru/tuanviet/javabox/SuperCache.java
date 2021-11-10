package ru.tuanviet.javabox;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SuperCache<K, V> implements Map<K, V> {
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
        locker.acquireReadLock();
        int tmp = superCache.size();
        locker.releaseReadLock();
        return tmp;
    }

    @Override
    public boolean isEmpty() {
        locker.acquireReadLock();
        boolean tmp = superCache.isEmpty();
        locker.releaseReadLock();
        return tmp;
    }

    @Override
    public boolean containsKey(Object key) {
        locker.acquireReadLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.getKey().equals(key)) {
                locker.releaseReadLock();
                return true;
            }
        }
        locker.releaseReadLock();
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        locker.acquireReadLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.getValue().equals(value)) {
                locker.releaseReadLock();
                return true;
            }
        }
        locker.releaseReadLock();
        return false;
    }

    @Override
    public V get(Object key) {
        locker.acquireReadLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.getKey().equals(key)) {
                V tmp = pair.getValue();
                locker.releaseReadLock();
                return tmp;
            }
        }
        locker.releaseReadLock();
        return null;
    }

    @Override
    public V put(K key, V value) {
        locker.acquireWriteLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.getKey().equals(key)) {
                V oldValue = pair.getValue();
                pair.setValue(value);
                locker.releaseWriteLock();
                return oldValue;
            }
        }
        superCache.add(new SuperEntry<K, V>(key, value));
        locker.releaseWriteLock();
        return null;
    }

    // ---- loker / addall // todo
    @Override
    public void putAll(Map<? extends K,? extends V> map) {
        for (Entry<? extends K, ? extends V> pair : map.entrySet()) {
            this.put(pair.getKey(), pair.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        locker.acquireWriteLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.getKey().equals(key)) {
                V oldValue = pair.getValue();
                superCache.remove(pair);
                locker.releaseWriteLock();
                return oldValue;
            }
        }
        locker.releaseWriteLock();
        return null;
    }

    @Override
    public void clear() {
        locker.acquireWriteLock();
        superCache.clear();
        locker.releaseWriteLock();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        locker.acquireReadLock();
        Set<K> tmpSet = new HashSet<>();
        for (SuperEntry<K, V> pair : superCache) {
            tmpSet.add(pair.getKey());
        }
        locker.releaseReadLock();
        return tmpSet;
    }

    @NotNull
    @Override
    public Collection<V> values() {
        locker.acquireReadLock();
        Collection<V> tmpColl = new ArrayList<>();
        for (SuperEntry<K, V> pair : superCache) {
            tmpColl.add(pair.getValue());
        }
        locker.releaseReadLock();
        return tmpColl;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        locker.acquireReadLock();
        Set<Entry<K, V>> tmpSet = new HashSet<>();
        for (SuperEntry<K, V> pair : superCache) {
            tmpSet.add(pair);
        }
        locker.releaseReadLock();
        return tmpSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuperCache)) {
            return false;
        }
        SuperCache<K, V> input = (SuperCache<K, V>) o;
        locker.acquireReadLock();
        for (SuperEntry<K, V> pair : input.superCache) {
            if (!this.superCache.contains(pair)) {
                locker.releaseReadLock();
                return false;
            }
        }
        locker.releaseReadLock();
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(locker, maxSize, ttl, superCache);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        locker.acquireWriteLock();
        for (SuperEntry<K, V> pair : superCache) {
            K k;
            V v;
            try {
                k = pair.getKey();
                v = pair.getValue();
            } catch (IllegalStateException ise) {
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
        locker.releaseWriteLock();
    }

    public synchronized V getOrCompute(K key, Supplier<V> valueSupplier) {
        locker.acquireReadLock();
        if (this.containsKey(key)) {
            V tmp = this.get(key);
            locker.releaseReadLock();
            return tmp;
        }
        locker.releaseReadLock();
        this.put(key, valueSupplier.get());
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
            updateLastUsed();
        }

        public SuperEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        private void updateLastUsed() {
            this.lastUsed = System.currentTimeMillis();
        }

        public K getKey() {
            updateLastUsed();
            return key;
        }

        public V getValue() {
            updateLastUsed();
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            updateLastUsed();
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
            updateLastUsed();
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
                SuperEntry<K, V> elem;
                int size = superCache.size();

                for (int i = 0; i < size; ++i) {
                    elem = superCache.get(i);
                    long lastUsedTime = elem.getLastUsed();
                    long currentTime = System.currentTimeMillis();
                    long actualTime = currentTime - lastUsedTime;
                    if (actualTime >  ttl) {
//                        System.out.println(elem.getKey() + " must be delete");
                        locker.acquireWriteLock();
                        superCache.remove(elem);
                        i--;
                        size--;
                        locker.releaseWriteLock();
                    }
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
