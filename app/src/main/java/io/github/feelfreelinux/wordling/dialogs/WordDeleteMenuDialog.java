package io.github.feelfreelinux.wordling.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.utils.DeleteWordListener;

public class WordDeleteMenuDialog extends DialogFragment {
    private String word;
    private int index;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get wordpack entry
        word = getArguments().getString("word");
        index = getArguments().getInt("index");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(word)
                .setItems(R.array.wordDeleteMenuList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ((DeleteWordListener) getActivity()).OnDeleteWord(index);
                                break;
                        }
                    }
                });
        return builder.create();
    }
}