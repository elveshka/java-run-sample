package ru.tuanviet.javabox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringZipper<T> implements Iterable<String> {
    private final Iterator<T>[] iterators;
    private final List<String> zippedStrings = new ArrayList<>();
    private final int zippedStrCount;

    @SafeVarargs
    StringZipper(Iterable<T>... iterable) {
        if (iterable.length == 0) {
            throw new IllegalArgumentException();
        }
        iterators = new Iterator[iterable.length];
        for (int i = 0; i < iterators.length; ++i) {
            iterators[i] = iterable[i].iterator();
        }
        while (iteratorsHasNext()) {
            zipNextIterator();
        }
        zippedStrCount = zippedStrings.size();
    }

    private boolean iteratorsHasNext() {
        boolean result = true;
        for (Iterator<T> it : iterators) {
            result = result & it.hasNext();
        }
        return result;
    }

    private void zipNextIterator() {
        StringBuilder builder = new StringBuilder();
        for (Iterator<T> it : iterators) {
            builder.append(it.next()).append(" ");
        }
        zippedStrings.add(builder.toString());
    }

    public void printZippedStrings() {
        for (String str : zippedStrings) {
            System.out.println(str);
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new StringZipperIterator(zippedStrings, zippedStrCount);
    }
}

class StringZipperIterator implements Iterator<String> {

    private int iteratorCurrent = 0;
    private List<String> zippedStrings;
    private int zippedStrCount;

    public StringZipperIterator(List<String> zippedStrings, int zippedStrCount) {

        this.zippedStrings = zippedStrings;
        this.zippedStrCount = zippedStrCount;
    }

    @Override
    public boolean hasNext() {
        return zippedStrCount - 1 >= iteratorCurrent;
    }

    @Override
    public String next() {
        return zippedStrings.get(iteratorCurrent++);
    }
}
