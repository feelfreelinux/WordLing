package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;

public class WordInputActivity extends InputActivity {
    TextView wordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_input);
        constructView();

        // Set text to text View
        wordView = (TextView) findViewById(R.id.wordView);
        wordView.setText(
                TextUtils.join(", ", getWord().getOriginLangQuestions())
        );
    }

}
