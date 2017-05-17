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
import io.github.feelfreelinux.wordling.utils.EditTextDialogActionListener;

public class EditTextDialog extends DialogFragment {
    EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Construct custom dialog with editText

        // Get string data
        String title = getArguments().getString("title");
        String message = getArguments().getString("message"),
                pretypedText = getArguments().getString("pretyped"),
                buttonLabel = getArguments().getString("buttonLabel"),
                editTextHint = getArguments().getString("hint");;
        Resources res = getResources();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_edit_text, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        editText = (EditText) view.findViewById(R.id.editText);
        editText.setHint(editTextHint);
        if (!(pretypedText == null)) editText.setText(pretypedText);
        builder.setTitle(title);
        if (!(message == null)) builder.setMessage(message);
        builder.setPositiveButton(buttonLabel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().isEmpty())
                        // Call callback
                        ((EditTextDialogActionListener) getActivity()).editTextAction(editText.getText().toString(), getArguments());
                    }
                });
        return builder.create();
    }
}

