package ru.tuanviet.javabox;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MultiStream<T> {

    private final Generator<T> generator;
    private MultiStream<?> initialStream;
    private boolean isParallel = false;

    private MultiStream(Generator<T> generator) {
        this.generator = new Generator<T>() {
            @Override
            public void generate(Consumer<T> consumer) {
                generator.generate(consumer);
            }
        };
    }

    public MultiStream(Iterable<T> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("null iterable");
        }
        this.generator = new Generator<T>() {
            @Override
            public void generate(Consumer<T> consumer) {
                if (!isParallel) {
                    iterable.forEach(consumer::accept);
                } else {
                    List<Spliterator<T>> spliteratorList = generateSpliterators(iterable.spliterator());
                    List<Thread> threads = generateThreads(spliteratorList, consumer);
                    runAndFinishThreads(threads);
                }
            }
        };
        initialStream = this;
    }

    public static <T> MultiStream<T> of(Iterable<T> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("null iterable");
        }
        return new MultiStream<>(iterable);
    }

    @SafeVarargs
    public static <T> MultiStream<T> of(T... args) {
        if (args == null) {
            throw new IllegalArgumentException("no elements");
        }
        return MultiStream.of(Arrays.asList(args));
    }

    private List<Spliterator<T>> generateSpliterators(Spliterator<T> spliterator) {
        long initialSize = spliterator.estimateSize();
        int threadCount = 2;
        long elementsInThread = 30000;

        while (threadCount * elementsInThread < initialSize) {
            threadCount += 2;
            elementsInThread *= 2;
        }

        List<Spliterator<T>> resultList = new ArrayList<>();
        resultList.add(spliterator);

        while (resultList.size() < threadCount) {
            List<Spliterator<T>> tempList = new ArrayList<>();
            for (Spliterator<T> s : resultList) {
                Spliterator<T> second = s.trySplit();
                tempList.add(s);
                tempList.add(second);
            }
            resultList = tempList;
        }

        return resultList;
    }

    private List<Thread> generateThreads(List<Spliterator<T>> spliterators, Consumer<T> action) {
        List<Thread> resultThreads = new ArrayList<>();
        for (Spliterator<T> spliterator : spliterators) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    spliterator.forEachRemaining(action::accept);
                }
            });
            resultThreads.add(thread);
        }
        System.out.println("Computing in " + resultThreads.size() + " Threads");
        return resultThreads;
    }

    public MultiStream<T> filter(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("bad filter expression");
        }

        MultiStream<T> filteredStream = new MultiStream<>(consumer -> generator.generate(
                value -> {
                    if (predicate.test(value)) {
                        consumer.accept(value);
                    }
                }));
        filteredStream.initialStream = this.initialStream;
        return filteredStream;
    }

    public <R> MultiStream<R> map(Function<T, R> function) {
        if (function == null) {
            throw new IllegalArgumentException("bad map expression");
        }

        MultiStream<R> mappedStream = new MultiStream<>(consumer -> {
            generator.generate(
                    value -> {
                        consumer.accept(function.apply(value));
                    });
        }
        );
        mappedStream.initialStream = this.initialStream;

        return mappedStream;
    }

    public Optional<T> reduce(BiFunction<T, T, T> operator) {
        if (operator == null) {
            throw new IllegalArgumentException("bad reduce function expression");
        }

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
        if (consumer == null) {
            throw new IllegalArgumentException("bad function expression");
        }

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
        List<T> resultList = new CopyOnWriteArrayList<>();

        generator.generate(new Consumer<T>() {
            @Override
            public void accept(T t) {
                resultList.add(t);
            }
        });

        return resultList;
    }

    public Set<T> toSet() {
        Set<T> resultSet = new CopyOnWriteArraySet<>();

        generator.generate(new Consumer<T>() {
            @Override
            public void accept(T t) {
                resultSet.add(t);
            }
        });

        return resultSet;
    }

    private void runAndFinishThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public final MultiStream<T> parallel() {
        initialStream.isParallel = true;
        return this;
    }
}
