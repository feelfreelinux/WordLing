package io.github.feelfreelinux.wordling.utils;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.screens.SessionSummaryActivity;
import io.github.feelfreelinux.wordling.screens.WordInputActivity;
import io.github.feelfreelinux.wordling.screens.WordTTSInputActivity;

public class SortedSessionManager implements Serializable {
    private Wordpack wordpack;
    private int iterator = 0;
    private int passed = 0;
    private String key;
    private Random generator;
    private int wordCount;
    private boolean skipListenings = false;

    public SortedSessionManager( Wordpack wordpack, String key) {
        this.wordpack = wordpack;
        this.wordCount = wordpack.pack.size();
        this.key = key;
        generator = new Random();

        // Sort word pack, by the correct answers rate
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
            Word word = wordpack.pack.get(iterator);
            iterator++;
            return word;
        } else return null;
    }

    public void addWord(Word word, boolean skipped) {
        Word clonedWord = word.clone();
        clonedWord.setRepeated();

        // Add skipped flag if nessesary
        if (skipped) {
            skipListenings = skipped;
            clonedWord.setSkipped();
        }
        wordpack.pack.add(clonedWord);
    }

    public void procced(Context context, WordLing app) {
        Intent intent;

        // Start another input activity
        if (getProgressCount() != getTotalWordCount())
            if (app.ttsReady() && (generator.nextFloat() <= 0.25f) && !wordpack.pack.get(iterator).isRepeated() && !skipListenings)
                intent = new Intent(context, WordTTSInputActivity.class);
            else intent = new Intent(context, WordInputActivity.class);
        else // Show session summary
            intent = new Intent(context, SessionSummaryActivity.class);

        intent.putExtra("SortedSessionManager", this);
        context.startActivity(intent);
    }
}
