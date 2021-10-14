package ru.tuanviet.javabox;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static final int DEFAULT_NEWS_COUNT = 10;

    public static void main(String[] args) {
        List<News> newsList = new ArrayList<>();
        Collector collector = new Collector(new FetchTopStories().getIds(), DEFAULT_NEWS_COUNT);

        for (Integer id : collector.getIds()) {
            News tmp = new News(id, News.DEFAULT_TIMEOUT);
            tmp.execute();
            newsList.add(tmp);
        }

        for (News news : newsList) {
            System.out.println(new Format(news));
        }

    }
}
