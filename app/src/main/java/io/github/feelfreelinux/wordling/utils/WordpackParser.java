package io.github.feelfreelinux.wordling.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.objects.WordpackList;

public class WordpackParser {
    // Parse text string into wordpack
    public Wordpack getWordpackFromText(String text) {
        if (validateJSONWordpack(text)) {
            try {
                JSONObject jsonData = new JSONObject(text);
                JSONArray pack = jsonData.getJSONArray("pack");
                ArrayList<Word> words = new ArrayList<>();
                String originLang = jsonData.getString("from");
                String translationLang = jsonData.getString("to");
                String name = jsonData.getString("name");
                String title = jsonData.getString("title");

                Float versionNumber = Float.parseFloat(jsonData.getString("version"));

                // Enumerate JSON array containing words
                for (int i = 0; i < pack.length(); i++) {

                    JSONObject word = pack.getJSONObject(i);
                    String translation = word.getJSONArray(translationLang).getString(0);
                    ArrayList<String> origin = new ArrayList<>();
                    word.getJSONArray(originLang).length();

                    for (int x = 0; x < word.getJSONArray(originLang).length(); x++)
                        origin.add(word.getJSONArray(originLang).getString(x));

                    // Bare wordpacks from web do not have these variables. Its used to storage user's progress in memory
                    int failed = 0, passed = 0;
                    if (word.has("passed")) passed = word.getInt("passed");
                    if (word.has("failed")) failed = word.getInt("failed");

                    words.add(new Word(origin, translation, passed, failed));
                }

                return new Wordpack(words, versionNumber, originLang, translationLang, name, title);
            } catch (JSONException e) {
                Log.v("Wordling", "caught json exception!");
            }
        } else {
            Log.v("WordLing", "Invalid wordpack format");
        }
        return null;
    }

    public boolean validateJSONWordpack(String text) {
        try {
            JSONObject jsonData = new JSONObject(text);
            if (jsonData.has("version") &&
                    jsonData.has("from") &&
                    jsonData.has("to") &&
                    jsonData.has("pack") &&
                    jsonData.has("name") &&
                    jsonData.has("title")) {
                return true;
            } else {
                Log.v("WordLing", "Invalid wordpack format");
                return false;
            }

        } catch (JSONException e) {
            Log.v("WordLing", "Error occured when parsing wordpack");
            return false;
        }
    }

    // Parse text string into wordpack list
    public WordpackList getWordpackListFromText(String text) {

        try {
            ArrayList<WordpackEntry> entriesList = new ArrayList<>();
            JSONArray wordpackList = new JSONArray(text);

            for (int i = 0; i < wordpackList.length(); i++) {
                JSONObject jsonEntry = wordpackList.getJSONObject(i);

                if (jsonEntry.has("title") && jsonEntry.has("key") && jsonEntry.has("description")) {
                    WordpackEntry singleEntry = new WordpackEntry(
                            jsonEntry.getString("key"),
                            jsonEntry.getString("title"),
                            jsonEntry.getString("description")
                    );
                    entriesList.add(singleEntry);
                }
            }
            return new WordpackList(entriesList);
        } catch (JSONException e) {
            Log.v("WordLing", "Error occured when parsing wordpack list");
            e.printStackTrace();
        }

        return null;
    }
}