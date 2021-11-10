package ru.tuanviet.javabox;

import java.util.HashSet;
import java.util.Set;

public class SuperReadWriteLock {
    private final Set<Long> id = new HashSet<>();
    private boolean isRead;
    private boolean isWrite;
    private int numberOfReaders = 0;

    public synchronized void acquireReadLock() {
        long tmpId = Thread.currentThread().getId();
        id.add(tmpId);
        numberOfReaders++;
        isRead = true;
        while (isWrite) {
            if (numberOfReaders == 1 && id.contains(tmpId)) {
                break;
            }
            try {
                wait(500);
            } catch (InterruptedException e) {
                throw new RuntimeException("error on read lock", e);
            }
        }
    }

    public synchronized void releaseReadLock() {
        id.remove(Thread.currentThread().getId());
        numberOfReaders--;
        if (numberOfReaders <= 0) {
            isRead = false;
            numberOfReaders = 0;
            notify();
        }
    }

    public synchronized void acquireWriteLock() {
        long tmpId = Thread.currentThread().getId();
        while (isWrite || isRead) {
            if (id.size() == 1 && id.contains(tmpId)) {
                break;
            }
            try {
                wait(500);
            } catch (InterruptedException e) {
                throw new RuntimeException("error on write lock", e);
            }
        }
        isWrite = true;
    }

    public synchronized void releaseWriteLock() {
        isWrite = false;
        notify();
    }
}
