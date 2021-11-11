package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class SuperCacheTest {
    private SuperCache<String, String> sutSuperCache;

    @Test
    public void shouldCreateWithInputMaxsize() {
        sutSuperCache = new SuperCache<>(1000, 5);

        int testInt = sutSuperCache.getMaxSize();
        assertThat(testInt).isEqualTo(5);

        long testLong = sutSuperCache.getTtl();
        assertThat(testLong).isEqualTo(1000);
    }

    @Test
    public void shouldPutWork() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        assertThat(sutSuperCache.size()).isEqualTo(1);
    }

    @Test
    public void shouldDeleteCacheWhenTimeup() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sleep(200);
        assertThat(sutSuperCache.size()).isEqualTo(0);
    }

    @Test
    public void shouldGetOrComputeWorkGet() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        String test = sutSuperCache.getOrCompute("01", new StringReturn<>("Test02"));
        assertThat(test).isEqualTo("Test01");
    }

    @Test
    public void shouldGetOrComputeWorkCompute() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        String test = sutSuperCache.getOrCompute("02", new StringReturn<>("Test02"));
        assertThat(test).isNull();
        assertThat(sutSuperCache.containsValue("Test02")).isTrue();
    }

    @Test
    public void shouldUpdateLastUsedParameter() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("keep", "alive");

        for (int i = 0; i < 100; ++i) {
            sutSuperCache.get("keep");
            sleep(2);
        }

        assertThat(sutSuperCache.size()).isEqualTo(1);
    }

    @Test
    public void testTest() {
        sutSuperCache = new SuperCache<>(199);
        for (int i = 0; i < 100; ++i) {
            sutSuperCache.put("" + (i + 1), "");
            sleep(2);
        }
        sutSuperCache.getSuperCache().forEach(x -> System.out.println(x.getLastUsed()));
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException("Crash SuperCacheTest.sleep", e);
        }
    }
}

class StringReturn<T> implements Supplier<T> {
    T t;

    public StringReturn(T t) {
        this.t = t;
    }


    @Override
    public T get() {
        return t;
    }
}
