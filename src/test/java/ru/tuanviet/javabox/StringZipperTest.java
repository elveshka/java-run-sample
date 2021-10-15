package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.*;

public class StringZipperTest {

    @Test
    public void someTest() {
        List<String> a = new ArrayList<>();
        Vector<Long> b = new Vector<>();
        Set<Character> c = new HashSet<>();
        a.add("a");
        a.add("b");
        a.add("c");
        a.add("c");
        a.add("c");
        a.add("c");
        b.add(1L);
        b.add(2L);
        b.add(3L);
        c.add('A');
        c.add('B');
        c.add('C');
        c.add('D');
        StringZipper<Integer> test = new StringZipper(a, b, c);

        for (String str : test) {
            System.out.println(str);
        }
    }

}
