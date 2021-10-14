package ru.tuanviet.javabox;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FetchTopStories {
    public static final int DEFAULT_TIMEOUT = 3_000;

    private static final String DEFAULT_URL = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    private final String url;
    private final int timeOut;
    private String ids;
    private Response response;

    public FetchTopStories() {
        this.timeOut = DEFAULT_TIMEOUT;
        this.url = DEFAULT_URL;

        sendRequest();
        parseJsonToFields(response);
    }

    public FetchTopStories(String url, int timeOut) {
        this.timeOut = timeOut;
        this.url = url;

        sendRequest();
        parseJsonToFields(response);
    }

    private void sendRequest() {
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseJsonToFields(Response response) {
        try {
            ids = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIds() {
        return ids;
    }

    public Response getResponse() {
        return response;
    }
}
