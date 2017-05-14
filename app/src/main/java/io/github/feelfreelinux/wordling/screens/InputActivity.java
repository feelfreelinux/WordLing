package io.github.feelfreelinux.wordling.screens;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;

public class InputActivity extends WordlingActivity {
    protected SortedSessionManager manager;
    protected Word word;
    protected EditText wordInput;
    protected InputMethodManager imm;
    protected ProgressBar progressBar;
    protected Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public Word getWord(){ return this.word; }
    protected void constructView() {
        // Get manager
        manager = (SortedSessionManager) getIntent().getSerializableExtra("SortedSessionManager");

        word = manager.getNextWord();

        // Set title - name of wordpack
        setTitle(manager.getWordpack().getTitle());

        Resources res = getResources();

        // Make it ask before exit
        setAskBeforeExit(res.getString(R.string.activityCloseConfirmationTitle), res.getString(R.string.activityCloseConfirmation));

        // Set progress status
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setMax(manager.getTotalWordCount());
        progressBar.setProgress(manager.getProgressCount()-1);

        wordInput = (EditText) findViewById(R.id.wordInput);
        wordInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkInput(wordInput.getText().toString(), false);
                    return true;
                }
                return false;
            }
        });

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput(wordInput.getText().toString(), false);
            }
        });

        // Open software keyboard
        wordInput.requestFocus();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void checkInput(String anwser, boolean skipped) {
        // Only show summary if we do not skip it
        if (!skipped) {
            // Open intent
            Intent intent = new Intent(this, WordSummaryActivity.class);
            intent.putExtra("CorrectAnswer", word.getTranslationLangQuestion());
            intent.putExtra("SortedSessionManager", manager);
            intent.putExtra("REPEATED", word.isRepeated());
            // Pass anwser result
            if (word.checkAnswer(anwser))
                intent.putExtra("PASSED", true);
                if(!word.isRepeated() || word.isSkipped()) manager.passed();
            else {
                intent.putExtra("PASSED", false);
                manager.addWord(word, false);
            }
            // Start new activity.
            startActivity(intent);
        } else {
            // Add this word to the end of list
            manager.addWord(word, true);
            manager.procced(this, ((WordLing) getApplication()));
        }

        // Close keyboard
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        finish();
    }

}
