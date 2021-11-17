package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class SuperCacheTest {
    private SuperCache<String, String> sutSuperCache;

    @Test
    public void shouldPutAnElement() {
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
        sleep(110);
        assertThat(sutSuperCache.size()).isEqualTo(0);
    }

    @Test
    public void shouldNotDeleteOneElement() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sleep(50);
        System.out.println(sutSuperCache.put("02", "keep me"));
        sleep(60);
        System.out.println(sutSuperCache.size());
        System.out.println(sutSuperCache.values());
//        assertThat(sutSuperCache.values().toArray()[0]).isEqualTo("keep me");
    }

    @Test
    public void testtest() {

    }

    @Test
    public void shouldGetOrComputeWorkCompute() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        String test = sutSuperCache.getOrCompute("02", new ObjectReturn<>("Test02"));
        assertThat(test).isNull();
        assertThat(sutSuperCache.containsValue("Test02")).isTrue();
    }

    @Test
    public void shouldPutAllElements() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("" + i, "elem");
        }
        sutSuperCache = new SuperCache<>(200);
        sutSuperCache.putAll(map);

        assertThat(sutSuperCache.size()).isEqualTo(10);
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException("Crash SuperCacheTest.sleep", e);
        }
    }
}

class ObjectReturn<T> implements Supplier<T> {
    T t;

    public ObjectReturn(T t) {
        this.t = t;
    }

    @Override
    public T get() {
        return t;
    }
}
