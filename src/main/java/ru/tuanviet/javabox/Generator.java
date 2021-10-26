package ru.tuanviet.javabox;

import java.util.function.Consumer;

public interface Generator<T> {
    void generate(Consumer<T> consumer);
}
