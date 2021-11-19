package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SuperCacheTest {
    private SuperCache<String, String> sutSuperCache;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenWrongTtl() {
        new SuperCache<>(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenWrongMaxSize() {
        new SuperCache<>(100, -1);
    }

    @Test
    public void shouldPutAnElement() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        assertThat(sutSuperCache.size()).isEqualTo(1);
    }

    @Test
    public void shouldGetAnElement() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        assertThat(sutSuperCache.get("01")).isEqualTo("Test01");
    }

    @Test
    public void shouldDeleteCacheWhenTimeUp() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sleep(110);
        assertThat(sutSuperCache.size()).isEqualTo(0);
    }

    @Test
    public void shouldNotDeleteElementWhenGetElement() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sleep(50);
        sutSuperCache.get("02");
        sleep(60);
        assertThat(sutSuperCache.size()).isEqualTo(1);
    }

    @Test
    public void shouldUpdateValueWhenPutSameKey() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("01", "keep me");
        String after = sutSuperCache.get("01");
        assertThat(after).isEqualTo("keep me");
    }

    @Test
    public void shouldGetNullWhenGetOrCompute() {
        sutSuperCache = new SuperCache<>(1000);
        String test = sutSuperCache.getOrCompute("02", () -> "Test02");
        assertThat(test).isNull();
    }

    @Test
    public void shouldGetOrComputeWorkCompute() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.getOrCompute("02", () -> "Test02");
        assertThat(sutSuperCache.containsValue("Test02")).isTrue();
    }

    @Test
    public void shouldPutAllElements() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("0" + i, "Test0" + i);
        }
        sutSuperCache = new SuperCache<>(200);
        sutSuperCache.putAll(map);
        assertThat(sutSuperCache.entrySet()).containsAll(map.entrySet());
    }

    @Test
    public void shouldPutAllElementsWithMaxSize() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("0" + i, "Test0" + i);
        }
        sutSuperCache = new SuperCache<>(200, 3);
        sutSuperCache.putAll(map);
        assertThat(sutSuperCache.size()).isEqualTo(3);
    }

    @Test
    public void shouldUpdateTimeByGet() {
        sutSuperCache = new SuperCache<>(100, 3);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sleep(50);
        sutSuperCache.get("02");
        sutSuperCache.put("04", "Test04");
        sutSuperCache.put("05", "Test05");
        assertThat(sutSuperCache.containsKey("02")).isTrue();
    }

    @Test
    public void shouldBeReentrant() {
        sutSuperCache = new SuperCache<>(100, 3);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sleep(50);
        sutSuperCache.get("02");
        sutSuperCache.put("04", "Test04");
        sutSuperCache.put("05", "Test05");
        sleep(50);
        sutSuperCache.put("02", "remove me");
        sleep(60);
        sutSuperCache.remove("02");
        assertThat(sutSuperCache.isEmpty()).isTrue();
    }

    @Test
    public void shouldEmptyAfterClear() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        sutSuperCache.clear();
        assertThat(sutSuperCache.size()).isEqualTo(0);
    }

    @Test
    public void shouldCompareByHashCodeTrue() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        SuperCache<String, String> test = new SuperCache<>(1000, null);
        test.put("01", "Test01");
        test.put("02", "Test02");
        test.put("03", "Test03");
        assertThat(sutSuperCache.hashCode()).isEqualTo(test.hashCode());
    }

    @Test
    public void shouldCompareByEqualsTrue() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        SuperCache<String, String> test = new SuperCache<>(1000, null);
        test.put("01", "Test01");
        test.put("02", "Test02");
        test.put("03", "Test03");
        assertThat(sutSuperCache.equals(test)).isTrue();
    }

    @Test
    public void shouldCreateSuperEntryAndChangeValue() {
        Map.Entry<String, String> entry = new AbstractMap.SimpleImmutableEntry<>("00", "test00");
        SuperCache.SuperEntry<String, String> test = new SuperCache.SuperEntry<>(entry);
        test.setValue("keep me");
        assertThat(test.toString()).isEqualTo("00=keep me");
    }

    @Test
    public void shouldKeySet() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        Set<String> testKeySet = sutSuperCache.keySet();
        assertThat(testKeySet.size()).isEqualTo(3);
    }

    @Test
    public void shouldValues() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01", "Test01");
        sutSuperCache.put("02", "Test02");
        sutSuperCache.put("03", "Test03");
        Collection<String> testValues = sutSuperCache.values();
        // ---
//        sutSuperCache.containsValue("somethings");
//        sutSuperCache.get("00");
//        sutSuperCache.remove("00");
//        sutSuperCache.getOrCompute("01", () -> "test");

//        sutSuperCache.forEach();
//        for (SuperCache.SuperEntry<String, String> pair : test) {
//            System.out.println(pair);
//        }
        // ---
        assertThat(testValues.size()).isEqualTo(3);
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException("Crash SuperCacheTest.sleep", e);
        }
    }
}

