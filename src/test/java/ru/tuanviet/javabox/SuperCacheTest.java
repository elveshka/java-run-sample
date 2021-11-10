package ru.tuanviet.javabox;

import org.junit.Test;

import static java.lang.Thread.sleep;
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
    public void shouldNotCrashPut() {
        sutSuperCache = new SuperCache<>(1000);
        sutSuperCache.put("01","Test01");
        assertThat(sutSuperCache.size()).isEqualTo(1);
    }

    @Test
    public void shouldDeleteCache() {
        sutSuperCache = new SuperCache<>(100);
        sutSuperCache.put("01","Test01");
        sutSuperCache.put("02","Test02");
        sutSuperCache.put("03","Test03");
        sleepForTest(500);
        assertThat(sutSuperCache.size()).isEqualTo(0);
    }

    private void sleepForTest(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
