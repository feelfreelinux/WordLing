package io.github.feelfreelinux.wordling.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.Values;
import io.github.feelfreelinux.wordling.adapters.WordEditAdapter;
import io.github.feelfreelinux.wordling.dialogs.EditTextDialog;
import io.github.feelfreelinux.wordling.dialogs.WordDeleteMenuDialog;
import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.utils.DeleteWordListener;
import io.github.feelfreelinux.wordling.utils.EditTextDialogActionListener;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;

public class WordEditActivity extends WordlingActivity implements EditTextDialogActionListener, DeleteWordListener {
    private ArrayList<String> wordList;
    private WordEditAdapter adapter;
    private EditText editText;
    private FloatingActionButton fab;
    private Toast toast;
    private Word word;
    private LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word_list);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setTitle(getString(R.string.edit_word));

        // Show "back button" in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);;
        wordList = new ArrayList<>();

        // Get ListView
        ListView listView = (ListView) findViewById(R.id.list);
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_edit_word_header, null, false);
        editText = (EditText) header.findViewById(R.id.word);

        emptyView = (LinearLayout) findViewById(R.id.emptyView);

        // Set tooltip text
        ((TextView) header.findViewById(R.id.label)).setText(getString(R.string.word_in) + " " + getIntent().getStringExtra("termLanguage"));

        // Fill out data if intent contains data
        word = (Word) getIntent().getSerializableExtra("word");
        if (!(word == null)) {
            emptyView.setVisibility(View.GONE);
            wordList = (ArrayList<String>) word.getOriginLangQuestions().clone();
            editText.setText(word.getTranslationLangQuestion());
        } else emptyView.setVisibility(View.VISIBLE);

        // Set header view to list
        listView.addHeaderView(header, null, false);
        adapter = new WordEditAdapter(this, wordList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Open dialog editing this value
                openDefinitionDialog((String) parent.getItemAtPosition(position), position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String word = ((String) parent.getItemAtPosition(position));
                if (word != null) {
                    WordDeleteMenuDialog dialog = new WordDeleteMenuDialog();
                    // Construct dialog bundle with string data
                    Bundle args = new Bundle();

                    // Show word, get index
                    args.putString("word", word);

                    args.putInt("index", position - 1);
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "dialog");
                }
                return true;
            }
        });

        // Get fab
        fab = (FloatingActionButton) findViewById(R.id.addWordButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDefinitionDialog(null, -1);
            }
        });

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.isEmpty()) emptyView.setVisibility(View.VISIBLE);
                else emptyView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public void editTextAction(String text, Bundle args) {
        if(args.containsKey("position")) {
            wordList.set(args.getInt("position")-1, text);
        }
        else wordList.add(text);
        adapter.notifyDataSetChanged();
    }
    public void showAskAlert() {
        // Show ask dialog if data was changed
        if ( !(word == null)
                && wordList.equals(word.getOriginLangQuestions())
                && editText.getText().toString().equals(word.getTranslationLangQuestion())) {
            // Just exit
            setResult(Values.WordEditActivityBlank);
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.save_changes)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Validate, save, and exit
                            validateAndExit();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Values.WordEditActivityBlank);
                            finish();
                        }

                    })
                    .show();
        }
    }
    public void validateAndExit() {
        // validate fields
        if ((editText.getText().toString().isEmpty()) || (wordList.size() < 1)) {
            if (editText.getText().toString().isEmpty()) {
                editText.setError(getString(R.string.field_empty));
                editText.requestFocus();
            }
            if (wordList.size() < 1) {
                ((TextView) findViewById(R.id.labelDefinitions)).setError(getString(R.string.field_empty));

                // Show toast, only if prev toast is not showing
                if ((toast == null) || !toast.getView().isShown()) {
                    toast = null;
                    toast = Toast.makeText(this, getString(R.string.translation_error), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        } else {
            Intent intent = new Intent();
            if (word == null) {
                intent.putExtra("word", new Word(wordList, editText.getText().toString(), 0, 0));
                setResult(Values.WordEditActivityNew, intent);
            } else {
                word.editData(wordList, editText.getText().toString());
                intent.putExtra("word", word);
                intent.putExtra("position", getIntent().getIntExtra("position", -1));
                setResult(Values.WordEditActivityEdited, intent);
            }
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        showAskAlert();
    }

    private void openDefinitionDialog(@Nullable String pretypedText, int position) {
        EditTextDialog dialog = new EditTextDialog();
        // Construct dialog bundle with string data
        Bundle args = new Bundle();

        args.putString("hint", getString(R.string.translation_in) + " " + getIntent().getStringExtra("definitionLanguage"));
        args.putString("buttonLabel", getString(R.string.add));
        if(position > -1) {
            args.putString("title", getString(R.string.edit_translation));
            args.putInt("position", position);
        } else args.putString("title", getString(R.string.add_definition));
        if (!(pretypedText == null)) args.putString("pretyped", pretypedText);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showAskAlert();
            case R.id.editor_menu:
                showAskAlert();
        }
        return true;
    }

    @Override
    public void OnDeleteWord(int index) {
        // Remove the word, and update adapter
        wordList.remove(index);
        adapter.notifyDataSetChanged();
    }
}
