package ru.tuanviet.javabox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    }

    public MegaStream<T> filter(Predicate<T> predicate) {
        return new MegaStream<T>(consumer -> generator.generate(value -> {
            if (predicate.test(value)) {
                consumer.accept(value);

            }
        }));
    }

    public <R> MegaStream<R> map(Function<T, R> function) {

        return new MegaStream<R>(consumer -> generator.generate(
                value -> {
                    consumer.accept(function.apply(value));
                    System.out.println("mapping" + value);  // to remove. This is only for checking "not compute"
                }
        ));
    }

    public void forEach(Consumer<T> consumer) {
        generator.generate(value -> consumer.accept(value));
    }

    public String join(String delimiter) {
        StringBuilder result = new StringBuilder();
        generator.generate(value -> {
            result.append(value.toString()).append(delimiter);
        });

        return result.substring(0, result.length() - delimiter.length());
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

@FunctionalInterface
interface Generator<T> {
    void generate(Consumer<T> content);
}
