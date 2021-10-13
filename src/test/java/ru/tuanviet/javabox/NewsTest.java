package ru.tuanviet.javabox;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class NewsTest {

    News sutNews;
    int timeOut = 3_000;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNull() {
        new News(null, timeOut);
    }

    @Test
    public void shouldReturnUrl() {
        Integer testId = 1234;
        sutNews = new News(testId, timeOut);
        assertThat("https://hacker-news.firebaseio.com/v0/item/1234.json?print=pretty").isEqualTo(sutNews.getUrl());
    }

    @Test
    public void shouldReturnJsonString() {
        Integer testId = 28850366;
        sutNews = new News(testId, timeOut);
        assertThat(121).isEqualTo(sutNews.getScore());
    }
}
