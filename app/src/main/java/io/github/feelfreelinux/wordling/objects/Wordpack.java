package io.github.feelfreelinux.wordling.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Wordpack implements Serializable {
    public ArrayList<Word> pack;
    private String originLang, translationLang, description, title;

    public Wordpack(ArrayList<Word> pack, String originLang, String translationLang, String description, String title) {
        this.pack = pack;
        this.originLang = originLang;
        this.translationLang = translationLang;
        this.description = description;
        this.title = title;
    }

    public String getWordLanguage() {
        return translationLang;
    }
    public String getTranslationLanguage() {
        return originLang;
    }
    // Convert wordpack to json string
    public String toJSONString() {
        JSONObject rawData = new JSONObject();
        try {
            rawData.put("description", this.description);
            rawData.put("title", this.title);
            rawData.put("from", this.originLang);
            rawData.put("to", this.translationLang);

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
                    singleWord.put("passed", word.getPassedAttempts());
                    singleWord.put("failed", word.getFailedAttempts());

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
        Log.v("DOGGO", wordpackCompare.toJSONString());
        Log.v("DOGGER", toJSONString());
        if (wordpackCompare.toJSONString().equals(toJSONString())) Log.v("COMPARE", "OKOK");
        return wordpackCompare.toJSONString().equals(toJSONString());
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
}
