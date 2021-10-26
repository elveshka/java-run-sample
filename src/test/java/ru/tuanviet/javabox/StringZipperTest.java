package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class StringZipperTest {


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyConstructor() {
        new StringZipper<>();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNull() {
        new StringZipper<>(null);
    }

    @Test
    public void shouldIterateOverOneCollection() {
        final List<Long> testList = new ArrayList<>();

        testList.add(1L);
        testList.add(2L);
        testList.add(3L);
        testList.add(4L);

        StringZipper<Long> test = new StringZipper<>(testList);

        String actual = "";
        String expected = "1 2 3 4 ";

        for (String s : test) {
            actual = String.join("", actual, s);
        }

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCheckStringsCount() {
        final List<Integer> testList = new ArrayList<>();
        final Set<Integer> testSet = new HashSet<>();

        testList.add(11);
        testList.add(22);
        testList.add(33);
        testList.add(44);
        testSet.add(1);
        testSet.add(2);
        testSet.add(3);

        StringZipper<Integer> test = new StringZipper<>(testList, testSet);
        int actual = 0;
        int expected = 3;

        Iterator<String> it = test.iterator();

        while (it.hasNext()) {
            it.next();
            actual++;
        }

        assertThat(actual).isEqualTo(expected);
    }
}
