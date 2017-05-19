package io.github.feelfreelinux.wordling.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.github.feelfreelinux.wordling.R;

public class GithubUploadedDialog extends DialogFragment {
    TextView urlView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Construct custom dialog with editText

        // Get string data
        String url = getArguments().getString("url");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_github_uploaded, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        urlView = (TextView) view.findViewById(R.id.urlView);

        urlView.setText(url);
        urlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(urlView.getText(), urlView.getText());
                Toast.makeText(getActivity(), R.string.copied, Toast.LENGTH_SHORT).show();
                clipboard.setPrimaryClip(clip);
            }
        });
        builder.setTitle(R.string.success);
        builder.setMessage(R.string.wp_uploaded);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }
}

