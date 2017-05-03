package io.github.feelfreelinux.wordling.utils;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public abstract class PortraitActivity extends AppCompatActivity {
    private boolean shouldAskBeforeExit = false;
    private String askTitle, askMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    public void setAskBeforeExit(String title, String message) {
        this.shouldAskBeforeExit = true;
        this.askMessage = message;
        this.askTitle = title;
    }

    @Override
    public void onBackPressed() {
        if(shouldAskBeforeExit) {
            Resources res = getResources();
            new AlertDialog.Builder(this)
                    .setTitle(askTitle)
                    .setMessage(askMessage)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            finish();
        }
    }

}