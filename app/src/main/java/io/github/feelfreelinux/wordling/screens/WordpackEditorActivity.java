package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Map;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.dialogs.LanguageSelectorDialog;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;

public class WordpackEditorActivity extends WordlingActivity {
    private ListView listView;
    private LayoutInflater inflater;
    private LinearLayout header;
    private FloatingActionButton fab;
    private Map<String, String> availableLanguages;
    private Button langOrigin, langTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordpack_editor_list);
        setTitle("Edit word pack");
        // Get inflater
        inflater = getLayoutInflater();
        listView = (ListView) findViewById(R.id.list);

        // Inflate Header View
        header = (LinearLayout) inflater.inflate(R.layout.activity_wordpack_editor_header, null, false);

        fab = (FloatingActionButton) findViewById(R.id.addWordButton);
        langOrigin = (Button) header.findViewById(R.id.langOrigin);
        langOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageDialog("origin");
            }
        });

        langTranslation = (Button) header.findViewById(R.id.langTranslation);
        langTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageDialog("translation");
            }
        });

    }

    public void setLanguage(String key, String language, String buttonID){
        if (buttonID == "translation") {
            langTranslation.setText(language);
        } else if (buttonID == "origin") {
            langOrigin.setText(language);
        }
    }

    public void openLanguageDialog(String buttonID) {
        // Create bundle, and pass it to fragment
        Bundle args = new Bundle();
        args.putString("buttonID", buttonID);
        LanguageSelectorDialog dialog = new LanguageSelectorDialog();
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "dialog");
    }
}
