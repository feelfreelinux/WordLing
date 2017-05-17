package io.github.feelfreelinux.wordling.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.screens.WordpackEditorActivity;


public class LanguageSelectorDialog extends DialogFragment {
    ArrayList<Pair<String, String>> availableLanguages;
    String buttonID;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        buttonID = getArguments().getString("buttonID");

        availableLanguages = new ArrayList<>();

        // Get list of available languages
        for (Locale l : Locale.getAvailableLocales())
            if(!checkLangCodeDuplication(l.getLanguage())) // We only want to store Language list
                availableLanguages.add( new Pair<> (l.getLanguage(),
                        l.getDisplayLanguage().substring(0, 1).toUpperCase() + l.getDisplayLanguage().substring(1))); // First letter uppercase



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Collections.sort(availableLanguages, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> o1, Pair<String, String> o2) {
                return o1.second.compareTo(o2.second);
            }
        });

        // Create Array of display names
        CharSequence[] displayNameList = new CharSequence[availableLanguages.size()];
        for (Pair<String, String> language : availableLanguages)
            displayNameList[availableLanguages.indexOf(language)] = language.second;

        builder.setTitle(getString(R.string.select_language))
                .setItems(displayNameList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((WordpackEditorActivity) getActivity()).setLanguage(availableLanguages.get(which).first, availableLanguages.get(which).second, buttonID);
                    }
                });
        return builder.create();
    }

    private boolean checkLangCodeDuplication(String code) {
        for (Pair<String, String> language : availableLanguages) {
            if (language.first.equals(code)) return true;
        }
        return false;
    }
}