package io.github.feelfreelinux.wordling.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.feelfreelinux.wordling.screens.WordpackEditorActivity;


public class LanguageSelectorDialog extends DialogFragment {
    Map<String, String> availableLanguages;
    String buttonID;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        buttonID = getArguments().getString("buttonID");

        availableLanguages = new HashMap<>();
        // Get list of available languages
        for (Locale l : Locale.getAvailableLocales())
            if(!availableLanguages.containsKey(l.getLanguage())) // We only want to store Language list
                availableLanguages.put(l.getLanguage(),
                        l.getDisplayLanguage().substring(0, 1).toUpperCase() + l.getDisplayLanguage().substring(1)); // First letter uppercase
        // Sort Map
        availableLanguages = sortByValue(availableLanguages);
        final List<String> printableList = new ArrayList<String>(availableLanguages.values());
        final List<String> keyList = new ArrayList<String>(availableLanguages.keySet());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select language")
                .setItems(printableList.toArray(new CharSequence[printableList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((WordpackEditorActivity) getActivity()).setLanguage(keyList.get(which), printableList.get(which), buttonID);
                    }
                });
        return builder.create();
    }

    // Sorts Map. from http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
    private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}