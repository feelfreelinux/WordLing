package io.github.feelfreelinux.wordling.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Wordpack implements Serializable {
    public ArrayList<Word> pack;
    private int errorMargin;
    private String originLang, translationLang, description, title;

    public Wordpack(ArrayList<Word> pack, String originLang, String translationLang, String description, String title, int errorMargin) {
        this.pack = pack;
        this.originLang = originLang;
        this.translationLang = translationLang;
        this.description = description;
        this.title = title;
        this.errorMargin = errorMargin;
    }

    public String getWordLanguage() {
        return translationLang;
    }
    public String getTranslationLanguage() {
        return originLang;
    }
    public int getErrorMargin() {return errorMargin;}
    // Convert wordpack to json string
    public String toJSONString(boolean safeProgress) {
        JSONObject rawData = new JSONObject();
        try {
            rawData.put("description", this.description);
            rawData.put("title", this.title);
            rawData.put("from", this.originLang);
            rawData.put("to", this.translationLang);
            rawData.put("error_margin", this.errorMargin);

            JSONArray pack = new JSONArray();

            // Iterate through list of words
            for(Word word: this.pack) {
                if (!word.isRepeated()) {
                    JSONObject singleWord = new JSONObject();

                    JSONArray originLanguage = new JSONArray(),
                            translateLanguage = new JSONArray();

                    for (String sentence : word.getOriginLangQuestions())
                        originLanguage.put(sentence);

                    translateLanguage.put(word.getTranslationLangQuestion());

                    singleWord.put("from", originLanguage);
                    singleWord.put("to", translateLanguage);
                    if (safeProgress) {
                        singleWord.put("passed", word.getPassedAttempts());
                        singleWord.put("failed", word.getFailedAttempts());
                    }

                    pack.put(singleWord);
                }
            }
            rawData.put("pack", pack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rawData.toString();
    }
    public boolean compareTo(Wordpack wordpackCompare) {
        return wordpackCompare.toJSONString(true).equals(toJSONString(true));
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
}
