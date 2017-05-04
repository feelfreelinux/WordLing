package io.github.feelfreelinux.wordling.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.objects.WordpackList;

public class StorageWordpackManager {
    SharedPreferences storage;
    WordpackParser parser;
    public StorageWordpackManager(Context context){
        this.parser = new WordpackParser();
        this.storage = context.getSharedPreferences("io.feelfree.wordling", Context.MODE_PRIVATE);
    }
    public boolean contains(String key) {
        return storage.contains(key);
    }

    public Wordpack getWordpackFromStorage(String key) {
        return parser.getWordpackFromText(storage.getString(key, null));
    }

    public WordpackList addWordpackToMemory(Wordpack wordpack){
        String uniqueKey = generateUniqueKey(4);
        saveJSONtoMemory(uniqueKey, wordpack.toJSONString());
        WordpackList wordpackList = getWordpackListFromStorage();

        wordpackList.addWordpackEntry(new WordpackEntry(uniqueKey, wordpack.getTitle(), wordpack.getDescription()));
        saveJSONtoMemory("wordpacks", wordpackList.toString());
        return wordpackList;
    }

    public WordpackList getWordpackListFromStorage() {
        return parser.getWordpackListFromText(storage.getString("wordpacks", null));
    }

    public void saveJSONtoMemory(String key, String jsonWordpack) {
        storage.edit().putString(key, jsonWordpack).apply();
    }

    public String generateUniqueKey(int lenght){
        Random generator = new Random();
        String result;
        // Generate unique memory key
        do {
            StringBuilder randomStringBuilder = new StringBuilder();
            int randomLength = generator.nextInt(lenght);
            char tempChar;
            for (int i = 0; i < randomLength; i++) {
                tempChar = (char) (generator.nextInt(96) + 32);
                randomStringBuilder.append(tempChar);
            }
            result = randomStringBuilder.toString();
        } while (contains(result));
        return result;
    }
}
