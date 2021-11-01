package ru.tuanviet.javabox;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MegaStream<T> {

    private final Generator<T> generator;
    public boolean isParallel = false;
    private Iterable<T> iterable;
    private MegaStream<?> initialStream;
    private Spliterator<T> spliterator;

    private MegaStream(Generator<T> generator) {

        this.generator = generator;
    }

    public MegaStream(Iterable<T> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("null parameter");
        }


        this.generator = new Generator<T>() {
            @Override
            public void generate(Consumer<T> consumer) {
                int count = 0;
                if (isParallel) {
                    for (T elem : iterable) {
                        ++count;
                        consumer.accept(elem);
                    }
                } else {
                    iterable.forEach(consumer::accept);
                }
            }
        };
        this.iterable = iterable;
        spliterator = iterable.spliterator();
        initialStream = this;
    }

    public static <T> MegaStream<T> of(Iterable<T> iterable) {

        return new MegaStream<>(
                iterable::forEach
        );
    }

    @SafeVarargs
    public static <T> MegaStream<T> of(T... args) {
        return MegaStream.of(Arrays.asList(args));
    }

    public MegaStream<T> filter(Predicate<T> predicate) {
        MegaStream<T> filteredStream = new MegaStream<>(consumer -> generator.generate(
                value -> {
                    if (predicate.test(value)) {
                        consumer.accept(value);
                    }
                }));
        filteredStream.initialStream = this.initialStream;
        System.out.println(initialStream.isParallel);
        return filteredStream;
    }

    public <R> MegaStream<R> map(Function<T, R> function) {
        MegaStream<R> mappedStream = new MegaStream<>(consumer -> generator.generate(
                value -> {
                    consumer.accept(function.apply(value));
                }
        ));
        mappedStream.initialStream = this.initialStream;
        System.out.println(initialStream.isParallel);
        return mappedStream;
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

    public <R> List<T> toList() {
        Spliterator<?> newSpliterator = initialStream.spliterator.trySplit();

        Spliterator<T> tempSplitIterator = (Spliterator<T>) initialStream.spliterator.trySplit();

        List<T> resultList = new ArrayList<>();
        //         tempSplitIterator.tryAdvance( new Consumer<T>() {
//            @Override
//            public void accept(T t) {
//                resultList.add(t);
//            }
//        });
//        initialStream.generator = new Generator<T>() {
//            @Override
//            public void generate(Consumer<?> consumer) {
//                Spliterator::tryAdvance;
//            }
//        }
//        generator.generate(resultList::add);


        Thread thread = new Thread(() -> generator.generate(new Consumer<T>() {
            @Override
            public void accept(T t) {
                resultList.add(t);
            }
        }));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public Set<T> toSet() {
        Set<T> resultSet = new HashSet<>();

        generator.generate(new Consumer<T>() {
            @Override
            public void accept(T t) {
                resultSet.add(t);
            }
        });

        return resultSet;
    }

    public final MegaStream<T> parallel() {
        initialStream.isParallel = true;
        return this;
    }

}
