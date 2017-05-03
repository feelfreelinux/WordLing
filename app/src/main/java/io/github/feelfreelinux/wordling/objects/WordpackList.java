package io.github.feelfreelinux.wordling.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WordpackList {
    private ArrayList<WordpackEntry> wordpacks;

    public WordpackList(ArrayList<WordpackEntry> wordpacks) {
        this.wordpacks = wordpacks;
    }

    public ArrayList<WordpackEntry> getWordpackEntries(){
        return this.wordpacks;
    }

    public void addWordpackEntry(WordpackEntry entry){
        this.wordpacks.add(entry);
    }

    public String toString() {
        JSONArray jsonList = new JSONArray();
        try {
            Log.v("asd", Integer.toString(wordpacks.size()));
            for (WordpackEntry wordpackEntry : this.wordpacks ) {
                JSONObject jsonEntry = new JSONObject();

                jsonEntry.put("key", wordpackEntry.key );
                jsonEntry.put("title", wordpackEntry.title);
                jsonEntry.put("description", wordpackEntry.description);

                jsonList.put(jsonEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonList.toString();
    }
}
