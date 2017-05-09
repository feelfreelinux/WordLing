package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.WordLing;

public class WordTTSInputActivity extends InputActivity {
    ImageView speaker;
    TextView skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_input);
        constructView();

        // We need to allow skipping it
        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add it to end of list
                manager.addWord(word, true);
                checkInput("", true);
            }
        });

        // Say it
        ((WordLing) getApplication()).say(getWord().getTranslationLangQuestion());

        // Set text to text View
        speaker = (ImageView) findViewById(R.id.icon);
        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((WordLing) getApplication()).isSpeaking())
                    ((WordLing) getApplication()).say(getWord().getTranslationLangQuestion());
            }
        });
    }

}
