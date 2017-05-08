package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.objects.Word;

public class WordTTSInputActivity extends InputActivity {
    Word word;
    ImageView speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_input);
        constructView();

        // Set text to text View
        speaker = (ImageView) findViewById(R.id.icon);
        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WordLing) getApplication()).say(getWord().getTranslationLangQuestion());
            }
        });
    }

}
