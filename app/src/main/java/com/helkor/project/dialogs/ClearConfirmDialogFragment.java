package com.helkor.project.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.helkor.project.R;

public class ClearConfirmDialogFragment extends DialogFragment {

    public interface Listener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
    Listener listener;
    public ClearConfirmDialogFragment(Object context) {
        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage(R.string.clear_confirm_message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveClick(ClearConfirmDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogNegativeClick(ClearConfirmDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
