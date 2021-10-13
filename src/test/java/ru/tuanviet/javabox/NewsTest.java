package ru.tuanviet.javabox;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;


public class NewsTest {
    News sutNews;
    int timeOut = 3_000;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            options().dynamicPort());

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
        System.out.println(sutNews.getResponseBody());
        System.out.println(sutNews.getUrl());
        System.out.println(sutNews.getRequest());
//        sutNews.execute();
//        assertThat(123).isEqualTo(sutNews.getScore());
    }
}
