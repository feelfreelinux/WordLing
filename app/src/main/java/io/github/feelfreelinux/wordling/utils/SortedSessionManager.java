package io.github.feelfreelinux.wordling.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.objects.Wordpack;

public class SortedSessionManager implements Serializable {
    private Wordpack wordpack;
    private int iterator = 0;
    private int passed = 0;
    private String key;
    private int wordCount;

    public SortedSessionManager(Wordpack wordpack, String key) {
        this.wordpack = wordpack;
        this.wordCount = wordpack.pack.size();
        this.key = key;

        // Sort word pack, by the correct anwsers rate
        Collections.sort(this.wordpack.pack, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                try {
                    // +1 works around zero divide problem.
                    float rateo1 = (o1.getPassedAttempts() + 1) / (o1.getFailedAttempts() + 1);
                    float rateo2 = (o2.getPassedAttempts() + 1) / (o2.getFailedAttempts() + 1);
                    return Math.round((rateo1 - rateo2) * 1000);
                } catch (ArithmeticException e) {
                    return 0;
                }
            }
        });
    }

    public Wordpack getWordpack() {
        return this.wordpack;
    }

    public int getWordCount(){
        return wordCount;
    }

    public int getTotalWordCount(){
        return wordpack.pack.size();
    }

    public int getProgressCount() {
        return iterator;
    }

    public void passed() { passed++; }

    public String getKey() { return this.key; }

    public int getPassedCount() { return passed; }

    public Word getNextWord() {
        if (getTotalWordCount() != getProgressCount()) {
            Word word = this.wordpack.pack.get(iterator);
            iterator++;
            return word;
        } else return null;
    }

    public void addWord(Word word){
        Word clonedWord = word.clone();
        clonedWord.setRepeated();
        this.wordpack.pack.add(clonedWord);
    }
}
