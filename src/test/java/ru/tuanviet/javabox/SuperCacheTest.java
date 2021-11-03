package ru.tuanviet.javabox;

import org.junit.Test;

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
    public void shouldTest() {
        sutSuperCache = new SuperCache<>(5000);
        sutSuperCache.put("01","Test01");
        sutSuperCache.put("02","Test02");
        sutSuperCache.put("03","Test03");
        assertThat(sutSuperCache.size()).isEqualTo(3);
    }

}
