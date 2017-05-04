package io.github.feelfreelinux.wordling.screens;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.utils.PortraitActivity;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;

public class WordSummaryActivity extends PortraitActivity {
    ImageView iconView;
    SortedSessionManager manager;
    ProgressBar progressBar;
    Button nextButton;
    TextView result, messageBox;
    Vibrator vibrator;
    boolean passed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_results);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        // Get session manager
        manager = (SortedSessionManager) getIntent().getSerializableExtra("SortedSessionManager");

        // Set title - name of wordpack
        setTitle(manager.getWordpack().getTitle());

        Resources res = getResources();
        // Make it ask before exit
        setAskBeforeExit(res.getString(R.string.activityCloseConfirmationTitle), res.getString(R.string.activityCloseConfirmation));

        // Get vibrator. Used to indicate fail
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Set progress status
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setMax(manager.getTotalWordCount());
        progressBar.setProgress(manager.getProgressCount()-1);

        passed = getIntent().getBooleanExtra("PASSED", false);
        iconView = (ImageView) findViewById(R.id.icon);
        messageBox = (TextView) findViewById(R.id.messageBox);

        // Icon to show.
        Drawable icon;
        if (passed) {
            icon = ContextCompat.getDrawable(this, R.drawable.icon_correct);
            messageBox.setText(res.getString(R.string.correctAnswer));
            // Only count word pas passed if word is not "repeated"
            if(!getIntent().getBooleanExtra("REPEATED", true)) manager.passed();

        }
        else {
            vibrator.vibrate(250);
            messageBox.setText(res.getString(R.string.incorrectAnswer));
            icon = ContextCompat.getDrawable(this, R.drawable.icon_incorrect);
        }
        iconView.setImageDrawable(icon);

        // Set correct result text
        result = (TextView) findViewById(R.id.wordView);
        result.setText(getIntent().getStringExtra("CorrectAnswer"));

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check are there any words
                if (manager.getProgressCount() != manager.getTotalWordCount()) {
                    // Start another input activity
                    Intent inputScreen = new Intent(getApplicationContext(), WordInputActivity.class);
                    inputScreen.putExtra("SortedSessionManager", manager);
                    startActivity(inputScreen);
                    finish();
                } else {
                    // Show session summary
                    Intent sessionSummary = new Intent(getApplicationContext(), SessionSummaryActivity.class);
                    sessionSummary.putExtra("SortedSessionManager", manager);
                    startActivity(sessionSummary);
                    finish();
                }
            }
        });
        if(!getIntent().getBooleanExtra("REPEATED", true)) {
            // Save progress
            StorageWordpackManager strMgr = new StorageWordpackManager(this);
            strMgr.saveJSONtoMemory(manager.getKey(), manager.getWordpack().toJSONString());
        }
    }
}
