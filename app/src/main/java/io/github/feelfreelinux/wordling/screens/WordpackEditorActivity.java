package io.github.feelfreelinux.wordling.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.Values;
import io.github.feelfreelinux.wordling.adapters.WordEditAdapter;
import io.github.feelfreelinux.wordling.adapters.WordpackEditorAdapter;
import io.github.feelfreelinux.wordling.dialogs.EditTextDialog;
import io.github.feelfreelinux.wordling.dialogs.LanguageSelectorDialog;
import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.utils.EditTextDialogActivity;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;

public class WordpackEditorActivity extends WordlingActivity {
    private ListView listView;
    private LinearLayout header;
    private FloatingActionButton fab;
    private Map<String, String> availableLanguages;
    private Button langOrigin, langTranslation;
    private List<Word> wordList;
    private Pair<String, String> termLang, definitionLang;
    private WordpackEditorAdapter adapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordpack_editor_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.edit_wordpack));

        wordList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list);

        // Inflate Header View
        header = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_wordpack_editor_header, null, false);

        listView.addHeaderView(header, null, false);
        adapter = new WordpackEditorAdapter(this, wordList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open wordpack editor
                openWordEditor((Word) parent.getItemAtPosition(position), position);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.addWordButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open word editor
                openWordEditor(null, -1);
            }
        });
        langOrigin = (Button) header.findViewById(R.id.langOrigin);
        langOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageDialog("origin");
            }
        });

        langTranslation = (Button) header.findViewById(R.id.langTranslation);
        langTranslation.setError(null);
        langTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageDialog("translation");
            }
        });


    }

    public void openWordEditor(@Nullable Word word, int position) {
        if (!(termLang == null) && !(definitionLang == null)) {
            Intent intent = new Intent(getApplicationContext(), WordEditActivity.class);
            intent.putExtra("termLanguage", termLang.second);
            intent.putExtra("definitionLanguage", definitionLang.second);
            if (!(word == null)) {
                intent.putExtra("word", word);
                intent.putExtra("position", position);
            }
            startActivityForResult(intent, Values.WordEditActivityNew);
        } else {
            if ((toast == null) || !toast.getView().isShown()) {
                toast = null;
                toast = Toast.makeText(this, R.string.select_language_toast, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void setLanguage(String key, String language, String buttonID){
        if (buttonID == "translation") {
            termLang = new Pair(key, language);
            langTranslation.setText(language);
        } else if (buttonID == "origin") {
            definitionLang = new Pair(key, language);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Values.WordEditActivityBlank ) {
            Word word = (Word) data.getSerializableExtra("word");
            if ((!(word == null)) && word.getOriginLangQuestions().size() > 0) {
                if (resultCode == Values.WordEditActivityEdited)
                    wordList.set(data.getIntExtra("position", -1) - 1, word);
                else if (resultCode == Values.WordEditActivityNew) wordList.add(word);

                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }
}
