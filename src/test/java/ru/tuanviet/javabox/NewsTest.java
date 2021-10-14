package ru.tuanviet.javabox;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;


public class NewsTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            options().dynamicPort());
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
        stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withBodyFile("testUrl.json")));

        sutNews = new News(28850366, timeOut, wireMockRule.baseUrl());
        sutNews.execute();
        assertThat(123).isEqualTo(sutNews.getScore());
        assertThat("AI predicts accident hot-spots from satellite imagery and GPS data").isEqualTo(sutNews.getTitle());
    }
}
