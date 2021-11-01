package ru.tuanviet.javabox;

public class App {
    public static void main(String[] args) {
        SuperReadWriteLock locker = new SuperReadWriteLock();
        Reader reader1 = new Reader(locker);
        Reader reader2 = new Reader(locker);
        Writer writer = new Writer(locker);

        Thread r1 = new Thread(reader1);
        Thread w1 = new Thread(writer);
        Thread r2 = new Thread(reader2);

        w1.start();
        r1.start();
        r2.start();

        try {
            r1.join();
            w1.join();
            r2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("App is over");
    }
}

class Reader implements Runnable {
    private SuperReadWriteLock locker;

    public Reader(SuperReadWriteLock locker) {
        this.locker = locker;
    }

    @Override
    public void run() {
        locker.acquireReadLock();
        System.out.println("Read is lock");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        locker.releaseReadLock();
        System.out.println(Thread.currentThread().getId());
    }
}

class Writer implements Runnable {
    private SuperReadWriteLock locker;

    public Writer(SuperReadWriteLock locker) {
        this.locker = locker;
    }

    @Override
    public void run() {
        locker.acquireWriteLock();
        System.out.println("Write is lock");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        locker.releaseWriteLock();
        System.out.println("Write is free");
    }
}