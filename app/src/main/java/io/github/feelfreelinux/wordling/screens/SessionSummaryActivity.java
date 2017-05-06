package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;

public class SessionSummaryActivity extends WordlingActivity {
    SortedSessionManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        manager = (SortedSessionManager) getIntent().getSerializableExtra("SortedSessionManager");

        // Set title - name of wordpack
        setTitle(manager.getWordpack().getTitle());
        ((TextView) findViewById(R.id.result)).setText(getResources().getString(R.string.result));

        TextView tv = (TextView) findViewById(R.id.percentText);
        int percentRate = (manager.getPassedCount() * 100) / manager.getWordCount();
        tv.setText(Integer.toString(percentRate)+"%");
        Log.v("asd", Integer.toString(manager.getPassedCount())+" "+Integer.toString(manager.getWordCount()));
    }

}
