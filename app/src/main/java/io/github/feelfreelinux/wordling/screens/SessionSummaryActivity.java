package io.github.feelfreelinux.wordling.screens;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.objects.SummaryListAdapter;
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

        // Get ListView
        ListView listView = (ListView) findViewById(R.id.list);
        // .subList gets list without repeated words
        List editedList = manager.getWordpack().pack.subList(0, manager.getWordCount());
        // Add empty space at beggining, used for headers
        editedList.add(0, null);
        SummaryListAdapter adapter = new SummaryListAdapter(this, editedList);
        listView.setAdapter(adapter);
    }

}
