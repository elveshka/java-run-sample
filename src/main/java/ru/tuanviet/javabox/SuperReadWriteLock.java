package ru.tuanviet.javabox;

import java.util.HashSet;
import java.util.Set;

public class SuperReadWriteLock {
    private boolean isRead;
    private boolean isWrite;
    private int numberOfThreads = 0;
    private Set<Long> id = new HashSet<>();

    public synchronized void acquireReadLock() {
        id.add(Thread.currentThread().getId());
        while (isWrite) {
            if (id.size() == 1) {
                break;
            }
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isRead = true;
        numberOfThreads++;
    }

    public synchronized void releaseReadLock() {
        id.remove(Thread.currentThread().getId());
        numberOfThreads--;
        if (numberOfThreads <= 0) {
            isRead = false;
            numberOfThreads = 0;
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
                e.printStackTrace();
            }
        }
        isWrite = true;
    }

    public synchronized void releaseWriteLock() {
        isWrite = false;
        notify();
    }
}
