package ru.tuanviet.javabox;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class News {
    public static final int DEFAULT_TIMEOUT = 3_000;
    private String url;
    private Integer id;
    private int timeOut;
    private Integer score;
    private String link;
    private String title;
    private String responseBody;
    private Response response;

    public News() {
    }

    public News(Integer id, int timeOut) {
        this.timeOut = timeOut;
        this.id = id;

        if (id == null || id < 0) {
            throw new IllegalArgumentException();
        }
        url = "https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty";
        sendRequest();
    }

    public News(Integer id, int timeOut, String url) {
        this.timeOut = timeOut;
        this.id = id;

        if (id == null || id < 0) {
            throw new IllegalArgumentException();
        }
        this.url = url + "/test";
        sendRequest();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer newId) {
        id = newId;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Response getRequest() {
        return response;
    }

    public void execute() {
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
        ObjectMapper mapper = new ObjectMapper();
        NewsAttr attr;
        try {
            responseBody = Objects.requireNonNull(response.body()).string();
            attr = mapper.readValue(responseBody, NewsAttr.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.title = attr.getTitle();
        this.score = Integer.parseInt(attr.getScore());
        this.link = attr.getUrl();
    }

    public String getUrl() {
        return url;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        score = newScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String newLink) {
        link = newLink;
    }
}

class NewsAttr {
    private String by;
    private String descendants;
    private String id;
    private String[] kids;
    private String score;
    private String text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
