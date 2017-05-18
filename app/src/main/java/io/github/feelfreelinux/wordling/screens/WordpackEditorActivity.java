package io.github.feelfreelinux.wordling.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.Values;
import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.adapters.WordpackEditorAdapter;
import io.github.feelfreelinux.wordling.dialogs.LanguageSelectorDialog;
import io.github.feelfreelinux.wordling.dialogs.WordDeleteMenuDialog;
import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.utils.DeleteWordListener;
import io.github.feelfreelinux.wordling.utils.InputFilterMinMax;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;

public class WordpackEditorActivity extends WordlingActivity implements DeleteWordListener {
    private ListView listView;
    private LinearLayout header;
    private FloatingActionButton fab;
    private EditText description, title;
    private Button langOrigin, langTranslation;
    private List<Word> wordList;
    private Pair<String, String> termLang, definitionLang;
    private WordpackEditorAdapter adapter;
    private EditText percentChooser;
    private Toast toast;
    private StorageWordpackManager strMgr;
    private Wordpack wordpack;
    private WordpackEntry entry;
    private LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordpack_editor_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.edit_wordpack));

        // Get storageWordpackManager
        strMgr = new StorageWordpackManager(this);
        wordList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list);
        emptyView = (LinearLayout) findViewById(R.id.emptyView);

        // Set longClick listener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = ((Word) parent.getItemAtPosition(position));
                WordDeleteMenuDialog dialog = new WordDeleteMenuDialog();

                // Construct dialog bundle with string data
                Bundle args = new Bundle();

                // Show word, get index
                args.putString("word", word.getTranslationLangQuestion());
                args.putInt("index", position-1);

                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "dialog");
                return true;
            }
        });



        // Inflate Header View
        header = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_wordpack_editor_header, null, false);

        // Get edit texts
        description = (EditText) header.findViewById(R.id.description);
        title = (EditText) header.findViewById(R.id.name);
        percentChooser = (EditText) header.findViewById(R.id.percentChooser);
        // Set filter for percent
        percentChooser.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});

        // Get buttons
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

        // Fill data with edited wordpack
        if ((getIntent().getSerializableExtra("wordpack")) != null) {
            emptyView.setVisibility(View.GONE);
            entry = (WordpackEntry) getIntent().getSerializableExtra("wordpack");
            wordpack = strMgr.getWordpackFromStorage(entry.key);
            if(wordpack != null) {
                // Fill out fields
                description.setText(wordpack.getDescription());
                title.setText(wordpack.getTitle());
                percentChooser.setText(Integer.toString(wordpack.getErrorMargin()));
                wordList.addAll(wordpack.pack);
                Locale wordLocale = ((WordLing) getApplication()).getLocaleFromString(wordpack.getWordLanguage()),
                        translationLocale = ((WordLing) getApplication()).getLocaleFromString(wordpack.getTranslationLanguage());
                // Set locales
                setLanguage(wordLocale.getLanguage(),
                        wordLocale.getDisplayLanguage().substring(0, 1).toUpperCase() + wordLocale.getDisplayLanguage().substring(1),
                        "translation");
                setLanguage(translationLocale.getLanguage(),
                        translationLocale.getDisplayLanguage().substring(0, 1).toUpperCase() + translationLocale.getDisplayLanguage().substring(1),
                        "origin");
            }
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

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

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else emptyView.setVisibility(View.GONE);
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
        // Handle word editor exit
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

    public void validateAndExit(){
        if (!filledData()) {
            if (!(wordList.size() > 0)) {
                // Only show toast if its not already shown
                if (toast == null || !toast.getView().isShown()) {
                    toast = null;
                    toast = Toast.makeText(getApplicationContext(), R.string.no_word_error, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            if (description.getText().toString().isEmpty()) description.setError(getString(R.string.field_empty));
            if (percentChooser.getText().toString().isEmpty()) percentChooser.setError(getString(R.string.field_empty));
            if (title.getText().toString().isEmpty()) title.setError(getString(R.string.field_empty));

        } else {
            // Validate, save, and exit
            if (entry != null) {
                strMgr.editWordpack(entry.key,
                        getWordpack().toJSONString(),
                        getWordpack().getTitle(),
                        getWordpack().getDescription());
            } else strMgr.addWordpackToMemory(getWordpack());

            setResult(Values.WordpackEdited);
            finish();
        }
    }

    public boolean filledData(){
        if (definitionLang != null
                && termLang != null
                && !description.getText().toString().isEmpty()
                && !percentChooser.getText().toString().isEmpty()
                && !title.getText().toString().isEmpty()
                && wordList.size() > 0) return true;
        else return false;
    }

    public void showAskAlert() {
        if (filledData() && wordpack != null && wordpack.compareTo(getWordpack())) {
            setResult(Values.WordEditActivityBlank);
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.save_changes)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            validateAndExit();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Values.WordEditActivityBlank);
                            finish();
                        }

                    }).show();
        }

    }

    public void onBackPressed() {
        showAskAlert();
    }

    public Wordpack getWordpack() {
        return new Wordpack((ArrayList<Word>) this.wordList, definitionLang.first,
                termLang.first,
                description.getText().toString(),
                title.getText().toString(),
                Integer.parseInt(percentChooser.getText().toString()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    @Override
    public void OnDeleteWord(int index) {
        // Remove word from list
        wordList.remove(index);
        adapter.notifyDataSetChanged();
    }
}
