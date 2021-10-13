package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectorTest {
    String testArr = "[ 28851484, 28850036, 28850861, 28850366, 28844101, 28845844, 28849101 ]";
    Integer[] testList = new Integer[]{28851484, 28850036, 28850861, 28850366, 28844101, 28845844, 28849101};

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNull() {
        new Collector(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmpty() {
        new Collector("");
    }

    @Test
    public void shouldReturnListOfIndex() {
        Collector coll = new Collector(testArr);
        for (Integer id : coll.getIds()) {
            assertThat(Arrays.asList(testList).contains(id)).isTrue();
        }
    }

    @Test
    public void shouldReturnCurrentCount() {
        Collector coll = new Collector(testArr, 4);
        assertThat(4).isEqualTo(coll.getIds().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionNegativeCount() {
        new Collector(testArr, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOverCount() {
        new Collector(testArr, 42);
    }
}
