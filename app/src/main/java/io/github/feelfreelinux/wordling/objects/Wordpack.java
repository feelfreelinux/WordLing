package io.github.feelfreelinux.wordling.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Wordpack implements Serializable {
    public ArrayList<Word> pack;
    private Float versionNumber;
    private String originLang, translationLang, name, title;

    public Wordpack(ArrayList<Word> pack, Float version, String originLang, String translationLang, String name, String title) {
        this.pack = pack;
        this.versionNumber = version;
        this.originLang = originLang;
        this.translationLang = translationLang;
        this.name = name;
        this.title = title;
    }

    // Convert wordpack to json string
    public String toJSONString() {
        JSONObject rawData = new JSONObject();
        try {
            rawData.put("version", this.versionNumber.toString());
            rawData.put("name", this.name);
            rawData.put("title", this.title);
            rawData.put("from", this.originLang);
            rawData.put("to", this.translationLang);

            JSONArray pack = new JSONArray();
            // Iterate through list of words
            for(Word word: this.pack) {
                JSONObject singleWord = new JSONObject();

                JSONArray originLanguage = new JSONArray(),
                        translateLanguage = new JSONArray();

                for (String sentence : word.getOriginLangQuestions())
                    originLanguage.put(sentence);

                translateLanguage.put(word.getTranslationLangQuestion());

                singleWord.put("pl", originLanguage);
                singleWord.put("en", translateLanguage);
                singleWord.put("passed", word.getPassedAttempts());
                singleWord.put("failed", word.getFailedAttempts());

                pack.put(singleWord);
            }
            rawData.put("pack", pack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rawData.toString();
    }

    public Float getVersionNumber(){
        return versionNumber;
    }
    public String getTitle(){
        return title;
    }
    public String getName(){
        return name;
    }
}
