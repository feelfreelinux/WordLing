package io.github.feelfreelinux.wordling.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.screens.WordpacksListActivity;

public class ImportWordpackDialog extends DialogFragment {
    EditText urlEdit;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Resources res = getResources();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_import_wordpack, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        urlEdit = (EditText) view.findViewById(R.id.url);
        builder.setTitle(res.getString(R.string.wordpackImportTitle));
        builder.setPositiveButton(
                res.getString(R.string.wordpackImportButton),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((WordpacksListActivity) getActivity()).importWordpackFromUrl(urlEdit.getText().toString());
                    }
                });
        return builder.create();
    }
}

