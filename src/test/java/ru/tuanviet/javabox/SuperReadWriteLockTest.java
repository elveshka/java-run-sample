package ru.tuanviet.javabox;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.tuanviet.javabox.SuperReadWriteLockTest.sleep;

public class SuperReadWriteLockTest {
    private SuperReadWriteLock sutLocker;
    private TestReader sutReader;
    private TestWriter sutWriter;
    private TestReadWrite sutReadWrite;

    @Before
    public void setUp() {
        sutLocker = new SuperReadWriteLock();
        sutReader = new TestReader(sutLocker);
        sutWriter = new TestWriter(sutLocker);
        sutReadWrite = new TestReadWrite(sutLocker);
    }

    @Test(timeout = 500)
    public void shouldBeTrueAfterRead() {
        //given
        Thread reader = new Thread(sutReader);

        //when
        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //then
        assertThat(sutReader.check).isTrue();
    }

    @Test(timeout = 500)
    public void shouldIncrementWhenWrite() {
        //given
        Thread reader = new Thread(sutReader);
        Thread writer = new Thread(sutWriter);

        //when
        reader.start();
        writer.start();
        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //then
        assertThat(sutWriter.getNumber()).isEqualTo(1);
    }

    @Test(timeout = 1000)
    public void shouldReentrantWithSeveralThreads() {
        //given
        Thread reader = new Thread(sutReader);
        Thread readWrite = new Thread(sutReadWrite);
        Thread writer = new Thread(sutWriter);

        //when
        reader.start();
        sleep(50);
        readWrite.start();
        writer.start();
        try {
            reader.join();
            readWrite.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //then
        assertThat(sutReadWrite.getNumber()).isEqualTo(2);
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
    public boolean check;

    public TestReader(SuperReadWriteLock locker) {
        this.locker = locker;
        check = false;
    }

    @Override
    public void run() {
        locker.acquireReadLock();
        sleep(100);
        locker.releaseReadLock();
        check = true;
    }
}

class TestWriter implements Runnable {
    private final SuperReadWriteLock locker;
    private int number;

    public TestWriter(SuperReadWriteLock locker) {
        this.locker = locker;
        number = 0;
    }

    public void inc() {number++; }

    public int getNumber() { return number; }

    @Override
    public void run() {
        locker.acquireWriteLock();
        inc();
        locker.releaseWriteLock();
    }
}

class TestReadWrite implements Runnable {
    private final SuperReadWriteLock locker;
    private int number;

    public TestReadWrite(SuperReadWriteLock locker) {
        this.locker = locker;
        number = 0;
    }

    public void inc() {number++; }

    public int getNumber() { return number; }

    @Override
    public void run() {
        locker.acquireReadLock();
        locker.acquireWriteLock();
        inc();
        locker.releaseWriteLock();
        locker.releaseReadLock();

        locker.acquireWriteLock();
        locker.acquireReadLock();
        locker.releaseReadLock();
        inc();
        locker.releaseWriteLock();
    }
}
