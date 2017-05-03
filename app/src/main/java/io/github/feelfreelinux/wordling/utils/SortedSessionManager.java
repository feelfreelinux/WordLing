package io.github.feelfreelinux.wordling.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.objects.Wordpack;

public class SortedSessionManager implements Serializable {
    private Wordpack wordpack;
    private ArrayList<Word> sortedList;
    private int iterator = 0;
    private int passed = 0;
    private String key;

    public SortedSessionManager(Wordpack wordpack, String key) {
        this.wordpack = wordpack;
        this.sortedList = wordpack.pack;
        this.key = key;

        // Sort word pack, by the correct anwsers rate
        Collections.sort(this.sortedList, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                // +1 works around zero divide problem.
                float rateo1 = (o1.getPassedAttempts()+1) / (o1.getFailedAttempts()+1);
                float rateo2 = (o2.getPassedAttempts()+1) / (o2.getFailedAttempts()+1);
                return Math.round((rateo1 - rateo2)*1000);
            }
        });
    }

    public Wordpack getWordpack() {
        return this.wordpack;
    }
    public int getWordCount(){
        return wordpack.pack.size();
    }

    public int getTotalWordCount(){
        return sortedList.size();
    }

    public int getProgressCount() {
        return iterator;
    }

    public void passed() { passed++; }

    public String getKey() { return this.key; }

    public int getPassedCount() { return passed; }

    public Word getNextWord() {
        if (getWordCount() != getProgressCount()) {
            Word word = sortedList.get(iterator);
            iterator++;
            return word;
        } else return null;
    }

    public void addWord(Word word){
        word.setRepeated();
        sortedList.add(word);
    }
}
