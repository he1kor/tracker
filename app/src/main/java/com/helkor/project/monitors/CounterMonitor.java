package com.helkor.project.monitors;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helkor.project.R;


public class CounterMonitor {
    private final Activity activity;
    private final RelativeLayout relative_layout;
    private final TextView text1;
    private final TextView text2;
    private final int start_height;
    private final int ANIMATION_DURATION = 300;
    private int height;
    private boolean doubledLine;

    public CounterMonitor(Activity activity, int relative_id, int text1_id, int text2_id){
        doubledLine = true;
        this.activity = activity;
        relative_layout = activity.findViewById(relative_id);
        text1 = activity.findViewById(text1_id);
        text2 = activity.findViewById(text2_id);
        start_height = (int) activity.getResources().getDimension(R.dimen.counter_monitor_height);
        height = start_height;
    }
    @SuppressLint("SetTextI18n")
    public void setFirstLineText(String text){
        text1.setText(text + (doubledLine ? "\n" : ""));
    }
    @SuppressLint("SetTextI18n")
    public void setSecondLineText(String text){
        if (!doubledLine) throw new RuntimeException("Second line is not available");
        text2.setText("\n" + text);
    }
    public void setHeight(int height){
        System.out.println(height);
        System.out.println(start_height);
        this.height = height;
        relative_layout.getLayoutParams().height = height;
        relative_layout.requestLayout();
    }
    public static int spToPx(float sp, Context context) {
        System.out.println("sp: " + sp);
        System.out.println("px:  " + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()));
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public void setMarginTop(View view,float sp){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.topMargin = spToPx(sp,activity);
    }
    public void switchModes(){
        if (doubledLine) {
            animateDecreasing();
        } else {
            animateExtension();
        }
        doubledLine = !doubledLine;
    }
    private void animateDecreasing(){
        animateTextDecreasing();
        animateLayoutDecreasing();
    }
    private void animateExtension(){
        animateTextExtension();
        animateLayoutExtension();
    }
    private void animateTextExtension(){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,28.0f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            text1.setAlpha((float) valueAnimator.getAnimatedValue() / 28);
            setMarginTop(text1, -28 + (Float) valueAnimator.getAnimatedValue());
            setMarginTop(text2,(Float) valueAnimator.getAnimatedValue());
        });
        animator.start();
    }
    private void animateTextDecreasing(){
        ValueAnimator animator = ValueAnimator.ofFloat(28.0f,0.0f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            text1.setAlpha((float) valueAnimator.getAnimatedValue() / 28);
            setMarginTop(text1, -28 + (Float) valueAnimator.getAnimatedValue());
            setMarginTop(text2,(Float) valueAnimator.getAnimatedValue());
        });
        animator.start();
    }
    private void animateLayoutExtension(){
        ValueAnimator animator = ValueAnimator.ofFloat(28.0f,0.0f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {

            setHeight((int) (start_height - spToPx((float) valueAnimator.getAnimatedValue(),activity)));
        });
        animator.start();
    }
    private void animateLayoutDecreasing(){
        ValueAnimator animator = ValueAnimator.ofFloat(0f,28.0f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {

            setHeight((int) (start_height - spToPx((float) valueAnimator.getAnimatedValue(),activity)));
        });
        animator.start();
    }
    public int getHeight(){
        return height;
    }
}
