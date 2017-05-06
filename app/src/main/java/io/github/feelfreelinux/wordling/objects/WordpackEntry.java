package io.github.feelfreelinux.wordling.objects;

import java.io.Serializable;

public class WordpackEntry implements Serializable {
    public String key, title, description;
    public WordpackEntry(String key, String title, String description) {
        this.title = title;
        this.key = key;
        this.description = description;
    }
}