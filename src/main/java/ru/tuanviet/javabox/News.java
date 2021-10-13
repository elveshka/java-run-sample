package ru.tuanviet.javabox;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class News {
    private final String url;
    private final int timeOut;
    private String title;
    private Integer score;

    public News(Integer id, int timeOut) {
        this.timeOut = timeOut;
        if (id == null || id < 0) {
            throw new IllegalArgumentException();
        }
        url = "https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty";

        parseJsonToFields(sendRequest(id));
    }

    private Response sendRequest(Integer id) {
        Request request = new Request.Builder()
                .url(url).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseJsonToFields(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        String body;
        NewsAttr attr;
        try {
            body = Objects.requireNonNull(response.body()).string();
            attr = mapper.readValue(body, NewsAttr.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.score = Integer.parseInt(attr.getScore());
    }

    public String getUrl() {
        return url;
    }

    public int getScore() {
        return score;
    }
}

class NewsAttr {
    private String by;
    private String descendants;
    private String id;
    private String[] kids;
    private String score;
    private String time;
    private String title;
    private String type;
    private String url;

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getDescendants() {
        return descendants;
    }

    public void setDescendants(String descendants) {
        this.descendants = descendants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getKids() {
        return kids;
    }

    public void setKids(String[] kids) {
        this.kids = kids;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

//    Request request = new Request.Builder().url(url).build();
//    OkHttpClient client = new OkHttpClient.Builder()
//            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
//            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
//            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
//            .build();
//        response = client.newCall(request).execute();