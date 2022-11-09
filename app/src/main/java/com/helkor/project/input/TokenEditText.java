package com.helkor.project.input;

import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TokenEditText implements TextView.OnEditorActionListener{

    public interface Listener{

        void onTokenEntered(String text);
        void onNotTokenEntered(String text);
        void onEmptyEntered();
    }
    private Listener listener;
    EditText editText;
    public TokenEditText(@NonNull EditText editText,Object implementationContext){
        this.editText = editText;
        trySetListener(implementationContext);
        setAllCapsFilter();
        editText.setOnEditorActionListener(this);
    }
    private void trySetListener(Object implementationContext){
        try {
            listener = (Listener) implementationContext;
        } catch (ClassCastException e){
            throw new RuntimeException(implementationContext + " must implement Listener");
        }
    }
    private void setAllCapsFilter(){
        InputFilter[] inputFilters = getFiltersWithNewElement();
        inputFilters[editText.getFilters().length] = new InputFilter.AllCaps();
        editText.setFilters(inputFilters);
    }
    private InputFilter[] getFiltersWithNewElement(){
        InputFilter[] editFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        return newFilters;
    }
    public void clear() {
        editText.setText("");
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            CharSequence text = textView.getText();
            int textLength = text.length();
            if (textLength == 5){
                listener.onTokenEntered(text.toString());
            } else if (textLength != 0){
                listener.onNotTokenEntered(text.toString());
            } else {
                listener.onEmptyEntered();
            }
            return true;
        }
        return false;
    }
}
