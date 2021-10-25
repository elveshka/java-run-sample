package ru.tuanviet.javabox;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class MegaStreamTest {

    @Test
    public void join() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1,2,3));

        MegaStream<Integer> testStream = new MegaStream<>(list);

        System.out.println(testStream.join("!!"));

//        assertThat(actualString).isEqualTo("1!!2!!3");
    }
}
