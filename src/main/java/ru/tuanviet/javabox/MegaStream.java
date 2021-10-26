package ru.tuanviet.javabox;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MegaStream<T> {

    private final Generator<T> generator;

    private MegaStream(Generator<T> generator) {
        this.generator = generator;
    }

    public MegaStream(Iterable<T> iterable) {
        this(consumer -> {
            iterable.forEach(consumer::accept);
        });
        if (iterable == null) {
            throw new IllegalArgumentException("null parameter");
        }
    }

    public static <T> MegaStream<T> of(Iterable<T> iterable) {
        return new MegaStream<T>(consumer -> iterable.forEach(item -> consumer.accept(item)));
    }

    public static <T> MegaStream<T> of(T... args) {
        return MegaStream.of(Arrays.asList(args));
    }

    public MegaStream<T> filter(Predicate<T> predicate) {
        return new MegaStream<T>(consumer -> generator.generate(
                value -> {
                    if (predicate.test(value)) {
                        consumer.accept(value);

                    }
                }));
    }

    public <R> MegaStream<R> map(Function<T, R> function) {

        return new MegaStream<>(consumer -> generator.generate(
                value -> {
                    consumer.accept(function.apply(value));
                    // System.out.println("mapping" + value);  // to remove. This is only for checking "not compute"
                }
        ));
    }

    public Optional<T> reduce(BiFunction<T, T, T> operator) {
        final Object[] result = new Object[]{null};
        final int[] generatedCount = {0};
        generator.generate(value1 -> {
            if (generatedCount[0] == 0) {
                result[0] = value1;
                ++generatedCount[0];
            } else {
                result[0] = operator.apply((T) result[0], value1);
            }
        });

        return Optional.of((T) result[0]);
    }

    public void forEach(Consumer<T> consumer) {
        generator.generate(value -> consumer.accept(value));
    }


    public String join(String delimiter) {
        StringBuilder result = new StringBuilder();

        generator.generate(value -> {
            result.append(value.toString()).append(delimiter);
        });
        return result.substring(0, Math.max(0, result.length() - delimiter.length()));
    }

    public String join() {
        return join("");
    }

    public List<T> toList() {
        List<T> resultList = new ArrayList<>();
        generator.generate(value -> {
            resultList.add(value);
        });
        return resultList;
    }

    public Set<T> toSet() {
        Set<T> resultSet = new HashSet<>();
        generator.generate(value -> {
            resultSet.add(value);
        });
        return resultSet;
    }

}