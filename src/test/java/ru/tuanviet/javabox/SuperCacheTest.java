package ru.tuanviet.javabox;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SuperCacheTest {
    private SuperCache<String, String> sutSuperCache;

    @Test
    public void shouldCreateWithInputMaxsize() {
        sutSuperCache = new SuperCache<>(1000, 5);

        System.out.println(sutSuperCache.getMaxSize());
        System.out.println(sutSuperCache.getTtl());
        System.out.println(sutSuperCache.getLocker());

        assertThat(sutSuperCache.getMaxSize()).isEqualTo(5);
    }

    @Test
    public void shouldOtherTest() {
        sutSuperCache = new SuperCache<>(5000);
    }

}