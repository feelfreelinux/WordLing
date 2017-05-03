package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.utils.PortraitActivity;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;

public class SessionSummaryActivity extends PortraitActivity {
    SortedSessionManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        manager = (SortedSessionManager) getIntent().getSerializableExtra("SortedSessionManager");

        // Set title - name of wordpack
        setTitle(manager.getWordpack().getTitle());

        TextView tv = (TextView) findViewById(R.id.percentText);
        int percentRate = (manager.getPassedCount() * 100) / manager.getWordCount();
        tv.setText(Integer.toString(percentRate)+"%");
    }

}
