package com.helkor.project.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.helkor.project.R;

public class AskForSettingsDialogFragment extends DialogFragment {

    public interface AskForSettingsDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    AskForSettingsDialogListener listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AskForSettingsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage(R.string.ask_for_settings_message)
                .setPositiveButton(R.string.ask_for_settings_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveClick(AskForSettingsDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.ask_for_settings_reject, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogNegativeClick(AskForSettingsDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
