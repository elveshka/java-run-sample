package ru.tuanviet.javabox;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SuperCache<K, V> implements Map<K, V> {
    private final static long TTL_GAP = 100;
    private final SuperReadWriteLock locker;
    private final Integer maxSize;
    private final long ttl;
    private final List<SuperEntry<K, V>> superCache = new ArrayList<>();
    private final Queue<SuperEntry<K,V>> queue = new ArrayDeque<>();
    private final Watcher runner;

    public SuperCache(long ttl) {
        this(ttl, null);
    }

    public SuperCache(long ttl, Integer maxSize) {
        locker = new SuperReadWriteLock();
        this.maxSize = maxSize;
        this.ttl = ttl;
        runner = new Watcher();
        Thread watcher = new Thread(runner);
        watcher.start();
    }

    public List<SuperEntry<K, V>> getSuperCache() {
        return superCache;
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

    // todo
    @Override
    public V put(K key, V value) {
        if (maxSize == null) {
            locker.acquireWriteLock();
            for (SuperEntry<K, V> pair : superCache) {
                if (pair.key.equals(key)) {     // change from getKey
                    V oldValue = pair.value;    // change from getValue
                    pair.setValue(value);
                    locker.releaseWriteLock();
                    return oldValue;
                }
            }
            SuperEntry<K, V> newElement = new SuperEntry<>(key, value);
            superCache.add(newElement);
            queue.add(newElement);
            locker.releaseWriteLock();
        }
        return null;
    }

    // ---- locker / addAll // todo
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> pair : map.entrySet()) {
            this.put(pair.getKey(), pair.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        locker.acquireWriteLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                V oldValue = pair.value;
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

    @Override
    protected void finalize() {
        this.runner.breakLoop();
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
        Set<Entry<K, V>> tmpSet = new HashSet<>(superCache);
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
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException("Crash forEach", e);
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

        private static boolean eq(Object o1, Object o2) {
            return Objects.equals(o1, o2);
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

    }

    private class Watcher implements Runnable {

        private void breakLoop() {
            this.running = false;
        }

        private boolean running = true;

        @Override
        public void run() {
            while (running) {
                locker.acquireReadLock();   // up from 306
                Iterator<SuperEntry<K, V>> elementsIterator
                        = superCache.iterator();

                while (elementsIterator.hasNext()) {
                    SuperEntry<K, V> elem = elementsIterator.next();
                    long lastUsedTime = elem.getLastUsed();
                    long currentTime = System.currentTimeMillis();
                    long actualTime = currentTime - lastUsedTime;

                    if (actualTime > ttl) {
                        locker.acquireWriteLock();
                        elementsIterator.remove();
                        locker.releaseWriteLock();
                    }
                }
                locker.releaseReadLock();
                try {
                    Thread.sleep(TTL_GAP);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Crash watcher", e);
                }
            }
            System.out.println("Runner closed");
        }
    }
}
