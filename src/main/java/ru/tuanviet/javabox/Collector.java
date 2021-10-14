package ru.tuanviet.javabox;

import java.util.ArrayList;
import java.util.List;

public class Collector {

    private final List<Integer> idsList = new ArrayList<>();
    private String[] idsArray;

    public Collector(String str) {
        parseString(str);
        for (String id : idsArray) {
            idsList.add(Integer.parseInt(id));
        }
    }

    public Collector(String str, int count) {
        parseString(str);
        addIdsToList(count);
    }

    private void parseString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (str.isEmpty()) {
            throw new IllegalArgumentException("argument is empty");
        }
        try {
            idsArray = str.substring(2, str.length() - 2).split(", ");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void addIdsToList(int count) {
        if (count < 0 || count > idsArray.length) {
            throw new IllegalArgumentException("wrong count");
        }
        for (int i = 0; i < count; ++i) {
            idsList.add(Integer.parseInt(idsArray[i]));
        }
    }


    public List<Integer> getIds() {
        return idsList;
    }
}
