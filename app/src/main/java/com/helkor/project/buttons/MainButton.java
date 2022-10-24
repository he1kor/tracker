package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.core.content.ContextCompat;

import com.helkor.project.R;
import com.helkor.project.buttons.Utils.BigButton;
import com.helkor.project.graphics.Bar;
import com.helkor.project.graphics.ColorVariable;

public class MainButton extends BigButton implements ColorVariable{

    private Variant variant;

    public MainButton(Activity activity,Object implementation_context, int button_view_id, int button_animation_id) {
        super(button_view_id,activity,implementation_context,button_animation_id);
        variant = Variant.MAIN;
    }
    @Override
    @SuppressLint("SetTextI18n")
    public void updateView(){
        switch (variant) {
            case MAIN:
                button_view.setBackground(ContextCompat.getDrawable(button_view.getContext(), R.drawable.circle_variant_1));
                button_view.setTextSize(38);
                button_view.setText("Start");
                Bar.setColor(R.color.light_red);
                break;
            case DRAW:
                button_view.setBackground(ContextCompat.getDrawable(button_view.getContext(), R.drawable.circle_variant_2));
                button_view.setTextSize(38);
                button_view.setText("View");
                Bar.setColor(R.color.lilac);
                break;
            case VIEW:
                button_view.setBackground(ContextCompat.getDrawable(button_view.getContext(), R.drawable.circle_variant_3));
                button_view.setTextSize(38);
                button_view.setText("Draw");
                Bar.setColor(R.color.yellow);
                break;
            case WALK:
                button_view.setBackground(ContextCompat.getDrawable(button_view.getContext(), R.drawable.circle_variant_4));
                button_view.setTextSize(37);
                button_view.setText("Pause");
                Bar.setColor(R.color.green);
                break;
            case PAUSE:
                button_view.setBackground(ContextCompat.getDrawable(button_view.getContext(), R.drawable.circle_variant_3));
                button_view.setTextSize(28);
                button_view.setText("Resume");
                Bar.setColor(R.color.yellow);
                break;
            case FINISH:
                button_view.setBackground(ContextCompat.getDrawable(button_view.getContext(), R.drawable.circle_variant_4));
                button_view.setTextSize(34);
                button_view.setText("Finish!");
                Bar.setColor(R.color.green);
                break;
        }
    }
    @Override
    public void setVariant(Variant variant) {
        this.variant = variant;
        updateView();
    }
    @Override
    public Variant getVariant() {
        return variant;
    }
}
