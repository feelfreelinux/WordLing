package io.github.feelfreelinux.wordling.objects;

import java.io.Serializable;
import java.util.ArrayList;

import io.github.feelfreelinux.wordling.utils.JaroDistance;

public class Word implements Serializable, Cloneable {
    private String langTo;
    private ArrayList<String> langFrom;
    private int failedAttempts, passedAttempts;
    private boolean repeated = false, skipped = false;

    public Word(ArrayList<String> langFrom, String langTo, int passed, int failed) {
        this.langFrom = langFrom;
        this.langTo = langTo;
        this.passedAttempts = passed;
        this.failedAttempts = failed;
    }

    public void editData (ArrayList<String> langFrom, String langTo) {
        this.langFrom = langFrom;
        this.langTo = langTo;
    }
    public Word clone() {
        return new Word(this.langFrom, this.langTo, this.passedAttempts, this.failedAttempts);
    }

    public ArrayList<String> getOriginLangQuestions(){
        return this.langFrom;
    }

    public String getTranslationLangQuestion(){
        return this.langTo;
    }

    public boolean checkAnswer(String answer, int errorMargin){
        if(new JaroDistance().calculate(answer.toLowerCase(), this.langTo.toLowerCase()) >= ((double) errorMargin)/100) {
            if(!repeated && !skipped) this.passedAttempts++;
            return true;
        }
        else {
            if (!repeated && !skipped)
            this.failedAttempts++;
            return false;
        }
    }
    public int getFailedAttempts(){
        return this.failedAttempts;
    }

    public int getPassedAttempts(){
        return this.passedAttempts;
    }

    public void setRepeated(){ this.repeated = true; }

    public void setSkipped(){ this.skipped = true; }

    public boolean isRepeated(){ return this.repeated; }

    public boolean isSkipped(){ return this.skipped; }

}