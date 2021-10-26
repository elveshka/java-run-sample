package ru.tuanviet.javabox;


import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SuperStreamTest {


    @Test
    public void should_collect_to_list() {
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        SuperStream<Integer> testStream = new SuperStream<>(set);

        List<Integer> expectedList = new ArrayList<>(set);
        List<Integer> actualList = testStream.toList();

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    public void should_collect_to_set() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));

        SuperStream<Integer> testStream = new SuperStream<>(list);

        Set<Integer> expectedSet = new LinkedHashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> actualSet = testStream.toSet();

        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test
    public void should_join_with_delimiter_to_string() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));

        SuperStream<Integer> testStream = new SuperStream<>(list);

        String actualString = testStream.join("!!");

        assertThat(actualString).isEqualTo("1!!2!!3");
    }

    @Test
    public void should_join_without_delimiter_to_string() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));

        SuperStream<Integer> testStream = new SuperStream<>(list);

        String actualString = testStream.join();

        assertThat(actualString).isEqualTo("123");
    }

    @Test
    public void should_map() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));

        SuperStream<Integer> superStream = new SuperStream<>(list);
        SuperStream<Integer> testStream = superStream.map(x -> x * 2);

        assertThat(testStream.join()).isEqualTo("246");
    }

    @Test
    public void should_filter() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

        SuperStream<Integer> superStream = new SuperStream<>(list);
        SuperStream<Integer> testStream = superStream.filter(x -> x > 2);

        assertThat(testStream.join()).isEqualTo("34");
    }

}