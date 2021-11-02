package ru.tuanviet.javabox;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Set;
import java.util.function.Supplier;

public class SuperCache<K, V> extends AbstractMap<K, V> {
    private final static long ACCURACY_TTL = 100;
    private final static int DEFAULT_MAXSIZE = 10;
    private final SuperReadWriteLock locker;
    private final int maxSize;
    private final long ttl;

    public SuperCache(long ttl) {
        this(ttl, DEFAULT_MAXSIZE);
    }

    public SuperCache(long ttl, int maxSize) {
        locker = new SuperReadWriteLock();
        this.maxSize = maxSize;
        this.ttl = ttl;
    }

    public synchronized V getOrCompute(K key, Supplier<V> valueSupplier) {
        return null;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public int getMaxSize() { return maxSize; }
    public long getTtl() { return ttl; }
    public SuperReadWriteLock getLocker() {
        return locker;
    }
}
