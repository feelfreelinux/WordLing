package io.github.feelfreelinux.wordling.screens;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.objects.Word;
import io.github.feelfreelinux.wordling.utils.PortraitActivity;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;

public class WordInputActivity extends PortraitActivity {
    SortedSessionManager manager;
    Word word;
    TextView wordView;
    EditText wordInput;
    InputMethodManager imm;
    ProgressBar progressBar;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_input);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // Get manager
        manager = (SortedSessionManager) getIntent().getSerializableExtra("SortedSessionManager");
        this.word = manager.getNextWord();

        // Set title - name of wordpack
        setTitle(manager.getWordpack().getTitle());

        Resources res = getResources();

        // Make it ask before exit
        setAskBeforeExit(res.getString(R.string.activityCloseConfirmationTitle), res.getString(R.string.activityCloseConfirmation));

        // Set progress status
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setMax(manager.getTotalWordCount());
        progressBar.setProgress(manager.getProgressCount());

        // Set text to text View
        wordView = (TextView) findViewById(R.id.wordView);
        wordView.setText(
                TextUtils.join(", ", word.getOriginLangQuestions())
        );

        wordInput = (EditText) findViewById(R.id.wordInput);
        wordInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkInput();
                    return true;
                }
                return false;
            }
        });

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });

        // Open software keyboard
        wordInput.requestFocus();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void checkInput() {
        // Open intent
        Intent intent = new Intent(this, WordSummaryActivity.class);
        intent.putExtra("SortedSessionManager", manager);
        intent.putExtra("CorrectAnswer", word.getTranslationLangQuestion());
        intent.putExtra("REPEATED", word.isRepeated());
        // Pass anwser result
        if (word.checkAnswer(wordInput.getText().toString()))
            intent.putExtra("PASSED", true);
        else {
            intent.putExtra("PASSED", false);
            manager.addWord(word);
        }
        // Start new activity.
        startActivity(intent);
        finish();
        // Close keyboard
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}
