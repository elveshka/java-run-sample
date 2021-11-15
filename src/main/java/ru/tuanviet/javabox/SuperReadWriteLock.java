package ru.tuanviet.javabox;

import java.util.HashSet;
import java.util.Set;

public class SuperReadWriteLock {
    private final Set<Long> id_read = new HashSet<>();
    private final Set<Long> id_write = new HashSet<>();
    private boolean isRead;
    private boolean isWrite;
    private int numberOfReaders = 0;

    public synchronized void acquireReadLock() {
        long tmpId = Thread.currentThread().getId();
        id_read.add(tmpId);
        while (isWrite) {
            if (id_write.contains(tmpId)) {
                break;
            }
            try {
                wait(500);
            } catch (InterruptedException e) {
                throw new RuntimeException("error on read lock", e);
            }
        }
        isRead = true;
        numberOfReaders++;
    }

    public synchronized void releaseReadLock() {
        id_read.remove(Thread.currentThread().getId());
        numberOfReaders--;
        if (numberOfReaders <= 0) {
            isRead = false;
            numberOfReaders = 0;
            notify();
        }
    }

    public synchronized void acquireWriteLock() {
        long tmpId = Thread.currentThread().getId();
        id_write.add(tmpId);
        while (isWrite || isRead) {
            if ((numberOfReaders == 1 && id_read.contains(tmpId)) || id_write.contains(tmpId)) {
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
        id_write.remove(Thread.currentThread().getId());
        isWrite = false;
        notify();
    }
}
