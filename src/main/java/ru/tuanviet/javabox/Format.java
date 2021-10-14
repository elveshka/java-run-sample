package ru.tuanviet.javabox;

public class Format {
    News n;

    public Format(News newNews) {
        if (newNews == null) {
            throw new IllegalArgumentException();
        }
        n = newNews;
        if (n.getLink() == null) {
            n.setLink("https://news.ycombinator.com/item?id=" + n.getId());
        }
    }

    @Override
    public String toString() {
        return n.getTitle() + " (" + n.getScore() + ")\n" + n.getLink() + "\n";
    }
}
