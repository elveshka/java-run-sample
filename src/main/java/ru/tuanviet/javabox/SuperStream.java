package ru.tuanviet.javabox;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


public class SuperStream <T>  {

    private final Iterable<T> content;

    public SuperStream(Iterable<T> content) {
        if (content == null){
            throw new IllegalArgumentException();
        }
        this.content = content;
    }

    public List<T> toList() {
        ArrayList<T> result = new ArrayList<>();

        for (T elem : content) {
            result.add(elem);
        }
        return result;
    }

    public Set<T> toSet() {
        Set<T> result = new LinkedHashSet<>();
        for (T elem : content) {
            result.add(elem);
        }
        return result;
    }

    public String join() {
        return join("");
    }

    public String join(String delim) {
        StringBuilder str = new StringBuilder();
        for (T elem : content) {
            str.append(elem).append(delim);
        }
        return str.substring(0, str.length() - delim.length());
    }

    public <R> SuperStream<R> map (Function<T, R> function){
        List<R> resultContent = new ArrayList<>();
        for (T element : content) {
            resultContent.add(function.apply(element));
        }
        return new SuperStream<>(resultContent);
    }

    public SuperStream<T> filter (Predicate<T> predicate) {
        List<T> resultContent = new ArrayList<>();
        for (T element : content) {
            if (predicate.test(element)){
                resultContent.add(element);
            }
        }
        return new SuperStream<>(resultContent);
    }
}
