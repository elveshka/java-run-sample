package ru.tuanviet.javabox;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SuperCache<K, V> implements Map<K, V> {
    private final static long WATCHER_DELAY = 10;
    private final SuperReadWriteLock locker;
    private final Integer maxSize;
    private final long ttl;
    private final Queue<SuperEntry<K, V>> superCache = new ArrayDeque<>();
    private final Thread thread;

    public SuperCache(long ttl) {
        this(ttl, null);
    }

    public SuperCache(long ttl, Integer maxSize) {
        if (maxSize != null && maxSize < 1) {
            throw new IllegalArgumentException("maxSize less than 1");

        }
        if (ttl < 1) {
            throw new IllegalArgumentException("ttl less than 1");

        }
        locker = new SuperReadWriteLock();
        this.maxSize = maxSize;
        this.ttl = ttl;
        thread = new Thread(new Watcher());
        thread.start();
    }

    private void updatePairLastUse(SuperEntry<K, V> pair) {
        locker.acquireWriteLock();
        superCache.remove(pair);
        superCache.add(pair);
        pair.updateLastUsed();
        locker.releaseWriteLock();
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
            if (pair.key.equals(key)) {
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
            if (pair.value.equals(value)) {
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
            if (pair.key.equals(key)) {
                V result = pair.value;
                updatePairLastUse(pair);
                locker.releaseReadLock();
                return result;
            }
        }
        locker.releaseReadLock();
        return null;
    }

    @Override
    public V put(K key, V value) {
        locker.acquireWriteLock();
        Iterator<SuperEntry<K, V>> elementsIterator = superCache.iterator();
        while (elementsIterator.hasNext()) {
            SuperEntry<K, V> pair = elementsIterator.next();
            if (pair.key.equals(key)) {
                V oldValue = pair.value;
                pair.value = value;
                updatePairLastUse(pair);
                locker.releaseWriteLock();
                return oldValue;
            }
        }
//        for (SuperEntry<K, V> pair : superCache) {
//            if (pair.key.equals(key)) {
//                V oldValue = pair.value;
//                pair.value = value;
//                updatePairLastUse(pair);
//                locker.releaseWriteLock();
//                return oldValue;
//            }
//        }
        if (maxSize != null && maxSize == superCache.size()) {
            superCache.remove();
        }
        superCache.add(new SuperEntry<>(key, value));
        locker.releaseWriteLock();
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        locker.acquireWriteLock();

        for (Entry<? extends K, ? extends V> pair : map.entrySet()) {
            this.put(pair.getKey(), pair.getValue());
            if (maxSize != null && superCache.size() == maxSize) {
                break;
            }
        }

        locker.releaseWriteLock();
    }

    @Override
    public V remove(Object key) {
        locker.acquireWriteLock();
        for (SuperEntry<K, V> pair : superCache) {
            if (pair.key.equals(key)) {
                V value = pair.value;
                superCache.remove(pair);
                locker.releaseWriteLock();
                return value;
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
        Set<K> result = new HashSet<>();

        for (SuperEntry<K, V> pair : superCache) {
            result.add(pair.key);
        }
        locker.releaseReadLock();

        return result;
    }

    @Override
    protected void finalize() {
        this.thread.interrupt();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        locker.acquireReadLock();
        Collection<V> result = new ArrayList<>();
        for (SuperEntry<K, V> pair : superCache) {
            result.add(pair.value);
        }
        locker.releaseReadLock();
        return result;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        locker.acquireReadLock();
        Set<Entry<K, V>> result = new HashSet<>(superCache);
        locker.releaseReadLock();
        return result;
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
                k = pair.key;
                v = pair.value;
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException("Crash forEach", e);
            }
            action.accept(k, v);
            updatePairLastUse(pair);
        }
        locker.releaseWriteLock();
    }

    public V getOrCompute(K key, Supplier<V> valueSupplier) {
        locker.acquireWriteLock();
        if (this.containsKey(key)) {
            V result = this.get(key);
            locker.releaseWriteLock();
            return result;
        }
        this.put(key, valueSupplier.get());
        locker.releaseWriteLock();
        return null;
    }

    public static class SuperEntry<K, V> implements Entry<K, V> {

        final K key;
        V value;
        long lastUsed;

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

        public void updateLastUsed() {
            this.lastUsed = System.currentTimeMillis();

        }

        public K getKey() {
//            updateLastUsed();
            return key;
        }

        public V getValue() {
//            updateLastUsed();
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            updateLastUsed();
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        @Override
        public String toString() {
            updateLastUsed();
            return key + "=" + value;
        }
    }

    private class Watcher implements Runnable {
        @Override
        public void run() {
            while (true) {
                while (!superCache.isEmpty() &&
                        System.currentTimeMillis() - superCache.peek().lastUsed >= ttl) {
                    superCache.remove();
                }
                try {
                    Thread.sleep(WATCHER_DELAY);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Crash watcher", e);
                }
            }
        }
    }
}
