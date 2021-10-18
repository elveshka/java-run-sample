package ru.tuanviet.javabox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringZipper<T> implements Iterable<String> {
    private final Iterator<?>[] iterators;
    private final List<String> zippedStrings = new ArrayList<>();

    @SafeVarargs
    StringZipper(Iterable<T>... iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("Null argument passed");
        }
        if (iterable.length == 0) {
            throw new IllegalArgumentException("No arguments passed");
        }
        iterators = new Iterator[iterable.length];
        for (int i = 0; i < iterators.length; ++i) {
            iterators[i] = iterable[i].iterator();
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new StringZipperIterator(iterators);
    }
}

class StringZipperIterator implements Iterator<String> {

    private final Iterator<?>[] iterators;

    public StringZipperIterator(Iterator<?>[] iterators) {
        this.iterators = iterators;
    }

    @Override
    public boolean hasNext() {
        boolean result = true;
        for (Iterator<?> it : iterators) {
            result &= it.hasNext();
        }
        return result;
    }

    @Override
    public String next() {
        StringBuilder builder = new StringBuilder();
        for (Iterator<?> it : iterators) {
            builder.append(it.next()).append(" ");
        }
        return builder.toString();
    }
}
