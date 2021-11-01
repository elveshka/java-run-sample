package ru.tuanviet.javabox;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiStreamTest {
    ArrayList<Integer> sutList = new ArrayList<>(Arrays.asList(1, 2, 3));
    MultiStream<Integer> sutStream = new MultiStream<>(sutList);

    @Test
    public void should_collect_to_list() {
        List<Integer> set = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        MultiStream<Integer> testStream = new MultiStream<>(set);

        List<Integer> expectedList = new ArrayList<>(set);
        List<Integer> actualList = testStream.toList();

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    public void should_collect_to_set() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        MultiStream<Integer> testStream = new MultiStream<>(list);

        Set<Integer> expectedSet = new LinkedHashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> actualSet = testStream.toSet();

        assertThat(actualSet).isEqualTo(expectedSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_exception() {
        new MultiStream<>(null);
    }

    @Test
    public void should_not_crash_with_empty_collection() {
        List<String> testSet = new ArrayList<>();

        new MultiStream<>(testSet)
                .filter(x -> x.hashCode() > 0)
                .map(str -> "" + str)
                .join("delimiter");
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
    public void should_mapping_to_same_type() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));

        MultiStream<Integer> superStream = new MultiStream<>(list);
        MultiStream<Integer> testStream = superStream.map(x -> x * 2);

        assertThat(testStream.join()).isEqualTo("246");
    }

    @Test
    public void should_mapping_integer_to_strings() {
        List<String> actual = sutStream
                .filter(x -> x < 3)
                .map(str -> String.format("->%d", str))
                .toList();

        assertThat(actual).isEqualTo(Arrays.asList("->1", "->2"));
    }

    @Test
    public void should_filter_elements() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

        MultiStream<Integer> superStream = new MultiStream<>(list);
        MultiStream<Integer> testStream = superStream.filter(x -> x > 2);

        assertThat(testStream.join()).isEqualTo("34");
    }

    @Test
    public void should_not_compute_before_terminate_method() {
        final String[] check = {"not computed"};

        sutStream.map(x -> {
            check[0] = "computing";
            return x * 2;
        }).map(x -> {
            check[0] = "computing";
            return x * 2;
        });

        assertThat(check[0]).isEqualTo("not computed");
    }

    @Test
    public void should_not_mutate_intermediate_streams() {
        // given
        final MultiStream<Integer> ss = new MultiStream<>(Arrays.asList(1, 2, 3)).map(i -> i + 1);

        final MultiStream<Integer> intermediateStream1 = ss.map(i -> i * 2);
        intermediateStream1.toList();

        final MultiStream<Integer> intermediateStream2 = ss.map(i -> i * 3);

        // when
        List<Integer> result = intermediateStream2.toList();

        // then
        assertThat(result).isEqualTo(Arrays.asList(6, 9, 12));

    }

    @Test
    public void should_reduce_given_sequence() {
        MultiStream<Integer> numbersStream = MultiStream.of(1, 2, 3, 4, 5, 6);
        Optional<Integer> result = numbersStream.reduce((x, y) -> x * y);
        assertThat(result.get()).isEqualTo(720);
    }

    @Test
    public void should_calculate_100k_elements_in_parallel() throws FileNotFoundException {
        List<Integer> test = new ArrayList<>();
        Scanner scanner = new Scanner(
                new File("/Users/out-goryaev3-ev/projects/java-run-sample/src/test/resources/__files/test10kElements")
        );
        while (scanner.hasNextInt()) {
            test.add(scanner.nextInt());
        }
        sutStream = new MultiStream<>(test);

        String actual = sutStream
                .filter(n -> n % 2 == 0)
                .map(n -> n / 1000)
                .parallel()
                .filter(n -> n < 400 && n > 100)
                .map(str -> String.format("%d", str))
                .reduce((x, y) -> x + y).toString();

//        assertThat(actual.get(0)).isEqualTo("124");
        System.out.println(actual);
    }

    @Test
    public void should_collect_to_valid_list() {

        List<String> set = new ArrayList<>(Arrays.asList("1", "11", "111", "1111", "11111"));
        MultiStream<String> testStream = new MultiStream<>(set);

        List<Integer> expectedList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> actualList = testStream.map(x -> x.length()).toList();

        assertThat(actualList).isEqualTo(expectedList);


    }
}
