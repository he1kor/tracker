package com.helkor.project.monitors;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helkor.project.R;
import com.helkor.project.graphics.ColorVariable;
import com.helkor.project.monitors.util.TwoLinesText;

import java.util.concurrent.atomic.AtomicBoolean;


public class CounterMonitor extends TwoLinesText implements ColorVariable {
    private final Activity activity;
    private final RelativeLayout relative_layout_outside;
    private final RelativeLayout relative_layout;
    private final TextView top_text_view;
    private final TextView bottom_text_view;
    private final int start_height;
    private int height;
    private RowState row_state;
    private Variant variant;

    private final int ANIMATION_DURATION = 300;
    private final int LONG_ANIMATION_DURATION = 1500;
    private final float TEXT_HEIGHT = 28f;

    private enum RowState {
        DOUBLED,
        ONE,
        ZERO
    }

    public CounterMonitor(Activity activity, int relative_id_outside, int relative_id, int text1_id, int text2_id){
        this.activity = activity;
        relative_layout_outside = activity.findViewById(relative_id_outside);
        relative_layout = activity.findViewById(relative_id);
        top_text_view = activity.findViewById(text1_id);
        bottom_text_view = activity.findViewById(text2_id);
        start_height = (int) activity.getResources().getDimension(R.dimen.counter_monitor_height);
        height = start_height;
        hide();
    }
    @Override
    public void setTopText(String text) {
        if (row_state != RowState.DOUBLED) throw new RuntimeException("Second line is not available");
        super.setTopText(text);
        top_text_view.setText(text);
    }

    @Override
    public void setBottomText(String text) {
        super.setTopText(text);
        bottom_text_view.setText(text);
    }
    public void setHeight(int px){
        this.height = px;
        relative_layout_outside.getLayoutParams().height = px;
        relative_layout_outside.requestLayout();
    }
    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public void setMarginTop(View view,float sp){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.topMargin = spToPx(sp,activity);
    }
    public void switchModes(){
        switch (row_state) {
            case DOUBLED:
                animateDecreasing();
                break;
            case ONE:
                animateExtension();
                break;
            case ZERO:
                animateAppearing();
                break;
        }
    }
    public void animateAppearing(){
        hide();
        row_state = RowState.ONE;
        animateTextAppearing();
        animateLayoutAppearing();
    }
    public void animateDecreasing(){
        extend();
        row_state = RowState.ONE;
        animateTextDecreasing();
        animateLayoutDecreasing();
    }
    public void animateExtension(){
        decrease();
        row_state = RowState.DOUBLED;
        animateTextExtension();
        animateLayoutExtension();
    }
    private void hide(){
        top_text_view.setAlpha(0f);
        bottom_text_view.setAlpha(0f);
        setMarginTop(top_text_view,-TEXT_HEIGHT);
        setMarginTop(bottom_text_view,-TEXT_HEIGHT);
        setHeight(0);
    }
    private void extend(){
        top_text_view.setAlpha(1f);
        setMarginTop(top_text_view, 0);
        setMarginTop(bottom_text_view,TEXT_HEIGHT);
        setHeight(start_height);
    }
    private void decrease(){
        top_text_view.setAlpha(0f);
        setMarginTop(top_text_view, -TEXT_HEIGHT);
        setMarginTop(bottom_text_view,0f);
        setHeight(start_height - spToPx(TEXT_HEIGHT,activity));
    }
    private void animateTextAppearing(){
        AtomicBoolean is_stopping = new AtomicBoolean(false);

        ValueAnimator animator = ValueAnimator.ofFloat(0f,TEXT_HEIGHT);
        animator.setDuration(LONG_ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            if (row_state != RowState.ONE && !is_stopping.get()) {
                is_stopping.set(true);
                animator.end();
            }
            bottom_text_view.setAlpha((float) valueAnimator.getAnimatedValue() / TEXT_HEIGHT);
            setMarginTop(bottom_text_view,-TEXT_HEIGHT + (float) valueAnimator.getAnimatedValue());
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }
    private void animateLayoutAppearing(){
        AtomicBoolean is_stopping = new AtomicBoolean(false);

        ValueAnimator animator = ValueAnimator.ofFloat(0f,start_height - spToPx(TEXT_HEIGHT,activity));
        animator.setDuration(LONG_ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            if (row_state != RowState.ONE && !is_stopping.get()) {
                is_stopping.set(true);
                animator.end();
            }
            setHeight((int) (float) valueAnimator.getAnimatedValue());
        });
        animator.start();
    }
    private void animateTextExtension(){
        AtomicBoolean is_stopping = new AtomicBoolean(false);

        ValueAnimator animator = ValueAnimator.ofFloat(0f,TEXT_HEIGHT);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            if (row_state != RowState.DOUBLED && !is_stopping.get()) {
                is_stopping.set(true);
                animator.end();
            }
            top_text_view.setAlpha((float) valueAnimator.getAnimatedValue() / TEXT_HEIGHT);
            setMarginTop(top_text_view, -TEXT_HEIGHT + (float) valueAnimator.getAnimatedValue());
            setMarginTop(bottom_text_view,(float) valueAnimator.getAnimatedValue());
        });
        animator.start();
    }
    private void animateLayoutExtension(){
        AtomicBoolean is_stopping = new AtomicBoolean(false);

        ValueAnimator animator = ValueAnimator.ofFloat(TEXT_HEIGHT,0.0f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            if (row_state != RowState.DOUBLED && !is_stopping.get()) {
                is_stopping.set(true);
                animator.end();
            }
            setHeight(start_height - spToPx((float) valueAnimator.getAnimatedValue(),activity));
        });
        animator.start();
    }
    private void animateTextDecreasing(){
        AtomicBoolean is_stopping = new AtomicBoolean(false);

        ValueAnimator animator = ValueAnimator.ofFloat(TEXT_HEIGHT,0.0f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            if (row_state != RowState.ONE && !is_stopping.get()) {
                is_stopping.set(true);
                animator.end();
            }
            top_text_view.setAlpha((float) valueAnimator.getAnimatedValue() / TEXT_HEIGHT);
            setMarginTop(top_text_view, -TEXT_HEIGHT + (float) valueAnimator.getAnimatedValue());
            setMarginTop(bottom_text_view,(float) valueAnimator.getAnimatedValue());
        });
        animator.start();
    }
    private void animateLayoutDecreasing(){
        AtomicBoolean is_stopping = new AtomicBoolean(false);

        ValueAnimator animator = ValueAnimator.ofFloat(0f,TEXT_HEIGHT);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            if (row_state != RowState.ONE && !is_stopping.get()) {
                is_stopping.set(true);
                animator.end();
            }
            setHeight(start_height - spToPx((float) valueAnimator.getAnimatedValue(),activity));
        });
        animator.start();
    }
    public int getHeight(){
        return height;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void updateView() {
        switch (variant){
            case MAIN:
                relative_layout.setBackground(activity.getDrawable(R.drawable.counter_background_1));
                break;
            case DRAW:
                relative_layout.setBackground(activity.getDrawable(R.drawable.counter_background_2));
                break;
            case PAUSE:
                relative_layout.setBackground(activity.getDrawable(R.drawable.counter_background_3));
                break;
            case WALK:
            case FINISH:
                relative_layout.setBackground(activity.getDrawable(R.drawable.counter_background_4));
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
