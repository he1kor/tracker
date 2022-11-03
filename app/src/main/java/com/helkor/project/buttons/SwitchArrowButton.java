package com.helkor.project.buttons;


import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.helkor.project.R;
import com.helkor.project.buttons.Utils.ButtonVariant;
import com.helkor.project.buttons.Utils.LittleButton;


public class SwitchArrowButton extends LittleButton {

    ButtonVariant<Variant> button_variant;
    private final ImageButton button_view;


    public enum Variant{
        UPLOAD,DOWNLOAD
    }
    public SwitchArrowButton(Activity activity, View root, Object implementation_context, int button_view_id){
        super(activity,implementation_context);
        button_view = root.findViewById(button_view_id);
        setOnClickListener(button_view);
        button_variant = new ButtonVariant<>(Variant.class);
        updateView();
    }
    private void updateView(){
        switch (getVariant()) {
            case UPLOAD:
                button_view.setImageResource(R.drawable.arrow_up);
                break;
            case DOWNLOAD:
                button_view.setImageResource(R.drawable.arrow_down);
                break;
        }
    }
    public Variant nextVariant(){
        Variant variant = button_variant.next();
        updateView();
        return variant;
    }
    public Variant getVariant() {
        return button_variant.face();
    }
}
