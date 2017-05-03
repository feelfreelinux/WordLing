package io.github.feelfreelinux.wordling.objects;

import java.io.Serializable;
import java.util.ArrayList;

import io.github.feelfreelinux.wordling.Values;
import io.github.feelfreelinux.wordling.utils.JaroDistance;

public class Word implements Serializable {
    private String langTo;
    private ArrayList<String> langFrom;
    private int failedAttempts, passedAttempts;
    private boolean repeated = false;

    public Word(ArrayList<String> langFrom, String langTo, int passed, int failed) {
        this.langFrom = langFrom;
        this.langTo = langTo;

        this.passedAttempts = passed;
        this.failedAttempts = failed;
    }
    public ArrayList<String> getOriginLangQuestions(){
        return this.langFrom;
    }

    public String getTranslationLangQuestion(){
        return this.langTo;
    }

    public boolean checkAnswer(String answer){
        if(new JaroDistance().calculate(answer, this.langTo) > Values.jaroDistanceValue){
            if(!repeated) this.passedAttempts++;
            return true;
        }
        else {
            if (!repeated)
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
}