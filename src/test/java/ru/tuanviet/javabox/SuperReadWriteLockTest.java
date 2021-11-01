package ru.tuanviet.javabox;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SuperReadWriteLockTest {
    private SuperReadWriteLock sutLocker;
    private TestReader sutReader;
    private TestWriter sutWriter;
    private TestReadWrite sutTestReadWrite;

    @Before
    public void setUp() {
        sutLocker = new SuperReadWriteLock();
        sutReader = new TestReader(sutLocker);
        sutWriter = new TestWriter(sutLocker);
        sutTestReadWrite = new TestReadWrite(sutLocker);
    }

    @Test
    public void shouldReentrantWithSeveralThreads() {
        Thread t1 = new Thread(sutReader);
        Thread t2 = new Thread(sutWriter);
        Thread t3 = new Thread(sutTestReadWrite);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class TestReader implements Runnable {
    private final SuperReadWriteLock locker;

    public TestReader(SuperReadWriteLock locker) {
        this.locker = locker;
    }

    @Override
    public void run() {
        locker.acquireReadLock();
        SuperReadWriteLockTest.sleep(500);
        locker.releaseReadLock();
    }
}

class TestWriter implements Runnable {
    private final SuperReadWriteLock locker;

    public TestWriter(SuperReadWriteLock locker) {
        this.locker = locker;
    }

    @Override
    public void run() {
        locker.acquireWriteLock();
        SuperReadWriteLockTest.sleep(500);
        locker.releaseWriteLock();
    }
}

class TestReadWrite implements Runnable {
    private final SuperReadWriteLock locker;

    public TestReadWrite(SuperReadWriteLock locker) {
        this.locker = locker;
    }

    @Override
    public void run() {
        locker.acquireReadLock();
        locker.acquireWriteLock();
        SuperReadWriteLockTest.sleep(500);
        locker.releaseWriteLock();
        locker.releaseReadLock();
    }
}
