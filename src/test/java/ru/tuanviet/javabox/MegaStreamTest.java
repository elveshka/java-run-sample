package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MegaStreamTest {
    ArrayList<Integer> sutList = new ArrayList<>(Arrays.asList(1, 2, 3));
    MegaStream<Integer> sutStream = new MegaStream<>(sutList);

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_exception() {
        MegaStream<String> testMegaStream = new MegaStream<>(null);
    }

    @Test
    public void should_not_crash_with_empty_collection() {
        Set<Objects> testSet = new HashSet<>();
        MegaStream<Objects> testMegaStream = new MegaStream<>(testSet);

        testMegaStream
                .filter(x -> x.hashCode() > 12345)
                .map(str -> "" + str)
                .toSet();
    }

    @Test
    public void should_concat_elements_with_no_delimiter() {

        assertThat(sutStream.join()).isEqualTo("123");
    }

    @Test
    public void should_concat_elements_with_delimiter() {

        assertThat(sutStream.join("!!")).isEqualTo("1!!2!!3");
    }

    @Test
    public void should_not_concat_with_one_element() {

        String actual = sutStream.filter(x -> x < 2).join("should_not_appear");

        assertThat(actual).isEqualTo("1");
    }

    @Test
    public void should_not_compute_before_terminate_method() {
        final String[] check = {"not computed"};

        sutStream.map(x -> {
            check[0] = "computing";
            return x * 2;
        });

        assertThat(check[0]).isEqualTo("not computed");
    }

    @Test
    public void should_mutate_to_list_of_strings() {
        List<String> actual = sutStream
                .filter(x -> x < 3)
                .map(str -> String.format("->%d", str))
                .toList();

        assertThat(actual).isEqualTo(Arrays.asList("->1", "->2"));
    }

}
