package io.github.feelfreelinux.wordling;

import android.app.Application;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * This is application class. Used to keep TTS object here
 */

public class WordLing extends Application {
    private TextToSpeech tts;
    private boolean ttsInit = false;
    private Locale ttsLocale;

    public void say(String word) {
        // Say word only if tts is initialised
        if (ttsInit) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
            else //noinspection deprecation
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void initTTS(Locale locale) {
        ttsInit = false;
        ttsLocale = locale;
        // Init text to speech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(ttsLocale);
                    // Make it say it slowly
                    tts.setSpeechRate(0.75f);
                    ttsInit = true;
                } else ttsInit = false;
            }
        });
    }

    public boolean ttsReady(){
        return ttsInit;
    }

    public boolean isSpeaking(){
        return tts.isSpeaking();
    }
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
