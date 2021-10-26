package ru.tuanviet.javabox;

import java.util.function.Consumer;

interface Generator<T> {
    void generate(Consumer<T> content);
}
