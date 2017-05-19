package io.github.feelfreelinux.wordling.dialogs;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.screens.WordpacksListActivity;
import io.github.feelfreelinux.wordling.utils.GithubGistUploader;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;

public class WordListMenuDialog extends DialogFragment {
    WordpackEntry entry;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get wordpack entry
        entry = (WordpackEntry) getArguments().getSerializable("entry");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(entry.title)
                .setItems(R.array.wordListMenuList, new DialogInterface.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ((WordpacksListActivity) getActivity()).editWordpack(entry);
                                break;
                            case 1:
                                if(((WordLing) getActivity().getApplication()).isOnline())
                                    ((WordpacksListActivity) getActivity()).uploadWordpackToWeb(entry);
                                else ((WordpacksListActivity) getActivity()).showNoInternetDialog();
                                break;
                            case 2:
                                ((WordpacksListActivity) getActivity()).safeDeleteWordpack(entry.key);
                                break;

                        }
                    }
                });
        return builder.create();
    }
}