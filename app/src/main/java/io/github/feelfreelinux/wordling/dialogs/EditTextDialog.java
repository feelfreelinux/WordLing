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
import io.github.feelfreelinux.wordling.utils.EditTextDialogActivity;

public class EditTextDialog extends DialogFragment {
    EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Construct custom dialog with editText

        // Get string data
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String buttonLabel = getArguments().getString("buttonLabel");
        String editTextHint = getArguments().getString("hint");;
        Resources res = getResources();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_edit_text, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        editText = (EditText) view.findViewById(R.id.editText);
        editText.setHint(editTextHint);
        builder.setTitle(title);
        if (!(message == null)) builder.setMessage(message);
        builder.setPositiveButton(buttonLabel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call callback
                        ((EditTextDialogActivity) getActivity()).editTextAction(editText.getText().toString());
                    }
                });
        return builder.create();
    }
}

