package io.github.feelfreelinux.wordling.objects;

public class WordpackEntry {
    public String key, title, description;
    public WordpackEntry(String key, String title, String description) {
        this.title = title;
        this.key = key;
        this.description = description;
    }
}