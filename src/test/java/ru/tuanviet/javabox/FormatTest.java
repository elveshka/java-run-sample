package ru.tuanviet.javabox;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormatTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException() {
        new Format(null);
    }

    @Test
    public void shouldReturnFormaText() {
        News testNews = new News();
        testNews.setTitle("Testing string");
        testNews.setLink("http://localhost:0000/testing");
        testNews.setId(21);
        testNews.setScore(42);

        Format testFormat = new Format(testNews);
        String needReturn = "Testing string (42)\nhttp://localhost:0000/testing\n";

        assertThat(needReturn).isEqualTo(testFormat.toString());

    }

}