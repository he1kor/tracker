package com.helkor.project.global;


import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.helkor.project.buttons.MainButton;
import com.helkor.project.buttons.SwitchInputButton;
import com.helkor.project.buttons.Utils.HideToColor;
import com.helkor.project.dialogs.LeaveWalkingConfirmDialogFragment;
import com.helkor.project.graphics.ColorVariable;

public class ModeState {

    Controller controller;
    private Mode current_mode;
    private DrawMode current_draw_mode;
    enum Mode{
        MAIN,
        DRAW,
        VIEW,
        WALK,
        PAUSE,
        FINISH,
        NULLMODE
    }
    protected enum DrawMode{
        FINGER,
        LOCATION;
    }
    public ModeState(Controller controller,Mode starting_mode){
        current_mode = Mode.NULLMODE;
        current_draw_mode = DrawMode.FINGER;
        this.controller = controller;
    }
    private void throwUnknownModeError(Mode goto_mode){
        throw new RuntimeException("Not available to go from " + current_mode + " to " + goto_mode);
    }
    protected void changeMode(Mode goto_mode){
        switch (current_mode){
            case MAIN:
                leaveMain(goto_mode);
                break;

            case DRAW:
                leaveDraw(goto_mode);
                break;

            case VIEW:
                leaveView(goto_mode);
                break;

            case WALK:
                leaveWalk(goto_mode);
                break;

            case PAUSE:
                leavePause(goto_mode);
                break;

            case FINISH:
                leaveFinish(goto_mode);
                break;
            case NULLMODE:
                leaveNull(goto_mode);
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leaveMain(Mode goto_mode){
        switch (goto_mode){
            case DRAW:
                goFromMainToDraw();
                break;
            case WALK:
                goFromMainToWalk();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leaveDraw(Mode goto_mode){
        switch (goto_mode){
            case MAIN:
                goFromDrawToMain();
                break;
            case VIEW:
                goFromDrawToView();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leaveView(Mode goto_mode){
        switch (goto_mode){
            case MAIN:
                goFromViewToMain();
                break;
            case DRAW:
                goFromViewToDraw();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leaveWalk(Mode goto_mode){
        switch (goto_mode){
            case MAIN:
                safeGoFromWalkToMain();
                break;
            case PAUSE:
                goFromWalkToPause();
                break;
            case FINISH:
                goFromWalkToFinish();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leavePause(Mode goto_mode){
        switch (goto_mode){
            case MAIN:
                safeGoFromPauseToMain();
                break;
            case WALK:
                goFromPauseToWalk();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leaveFinish(Mode goto_mode){
        switch (goto_mode){
            case MAIN:
                goFromFinishToMain();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }
    private void leaveNull(Mode goto_mode){
        switch (goto_mode) {
            case MAIN:
                goFromNullToMain();
                break;
            default:
                throwUnknownModeError(goto_mode);
        }
    }

    private void offWalkable(){
        controller.location_sensor.setWalkable(false);
        controller.location_sensor.unSubscribeToLocationUpdate();
    }
    private void onWalkable(){
        controller.location_sensor.setWalkable(true);
        controller.location_sensor.subscribeToLocationUpdate();
    }
    private void offWalkSection() {
        controller.counter_monitor.switchModes();
        controller.timer.stop();
    }
    private void onWalkSection() {
        controller.counter_monitor.switchModes();
        controller.timer.start();
    }

    private void updateUI(ColorVariable.Variant variant){
        controller.counter_monitor.setVariant(variant);
        controller.main_button.setVariant(variant);
    }

    private void goFromNullToMain() {
        current_mode = Mode.MAIN;

        updateUI(ColorVariable.Variant.MAIN);
        controller.main_button.show();

        controller.location_sensor.setDrawable(false);
        offWalkable();

        controller.location_sensor.moveCamera(controller.location_sensor.getMyLocation(), controller.COMFORTABLE_ZOOM_LEVEL+1,3);
    }

    private void goFromMainToDraw(){
        current_mode = Mode.DRAW;
        updateUI(ColorVariable.Variant.DRAW);

        //controller.button_connection.show();
        controller.button_switch_input.show();
        controller.button_clear.show();
        if (controller.button_switch_input.getVariant() == SwitchInputButton.Variant.GPS){
            setNavigatorDrawMode();
        } else if (controller.button_switch_input.getVariant() == SwitchInputButton.Variant.DRAW){
            setFingerDrawMode();
        }
    }

    private void goFromMainToWalk(){
        current_mode = Mode.WALK;

        controller.path_string.setTravelledPathEnabled(true);
        updateUI(ColorVariable.Variant.WALK);
        onWalkSection();
        onWalkable();
    }

    private void goFromDrawToMain(){
        current_mode = Mode.MAIN;
        checkoutLeaveDrawMode();

        updateUI(ColorVariable.Variant.MAIN);

        //controller.button_connection.hideWithColor(HideToColor.MAIN);
        controller.button_switch_input.hideWithColor(HideToColor.MAIN);
        controller.button_clear.hideWithColor(HideToColor.MAIN);

        controller.location_sensor.moveCamera(controller.location_sensor.getMyLocation(), controller.COMFORTABLE_ZOOM_LEVEL+1,1);
    }

    private void goFromDrawToView(){
        current_mode = Mode.VIEW;
        checkoutLeaveDrawMode();

        //controller.button_connection.hideWithColor(HideToColor.VIEW);
        controller.button_switch_input.hideWithColor(HideToColor.VIEW);
        controller.button_clear.hideWithColor(HideToColor.VIEW);

        updateUI(ColorVariable.Variant.VIEW);
    }


    private void goFromViewToMain(){
        current_mode = Mode.MAIN;

        updateUI(ColorVariable.Variant.MAIN);

        controller.location_sensor.moveCamera(controller.location_sensor.getMyLocation(), controller.COMFORTABLE_ZOOM_LEVEL+1,1);
    }

    private void goFromViewToDraw(){
        current_mode = Mode.DRAW;

        updateUI(ColorVariable.Variant.DRAW);

        //controller.button_connection.show();
        controller.button_switch_input.show();
        controller.button_clear.show();

        checkoutDrawMode();
    }



    protected void goToMainConfirmation(String tag){
        switch (tag){
            case "LeaveWalkingConfirmation":
                goFromWalkToMain();
                break;
            case "LeavePauseConfirmation":
                goFromPauseToMain();
                break;
        }
    }
    private void safeGoFromWalkToMain(){
        LeaveWalkingConfirmDialogFragment leave_walking_confirm_dialog_fragment = new LeaveWalkingConfirmDialogFragment(controller);
        leave_walking_confirm_dialog_fragment.show(controller.main_activity.getSupportFragmentManager(),"LeaveWalkingConfirmation");
    }

    private void goFromWalkToMain(){
        current_mode = Mode.MAIN;
        offWalkSection();
        controller.location_sensor.moveCamera(controller.location_sensor.getMyLocation(), controller.COMFORTABLE_ZOOM_LEVEL + 1, 1);

        updateUI(ColorVariable.Variant.MAIN);

        controller.path_string.setTravelledPathEnabled(false);

        offWalkable();

        controller.line_drawer.resetTravelledPath();
    }

    private void goFromWalkToPause(){
        current_mode = Mode.PAUSE;
        updateUI(ColorVariable.Variant.PAUSE);

        offWalkable();
        controller.timer.pause();
    }
    private void goFromWalkToFinish(){
        current_mode = Mode.FINISH;
        Vibrator vibrator = (Vibrator) controller.main_activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,200,100,200,100,50,100,50,100,300},-1));
        }

        updateUI(ColorVariable.Variant.FINISH);

        offWalkable();
        controller.timer.pause();
    }


    private void safeGoFromPauseToMain(){
        LeaveWalkingConfirmDialogFragment leave_walking_confirm_dialog_fragment = new LeaveWalkingConfirmDialogFragment(controller);
        leave_walking_confirm_dialog_fragment.show(controller.main_activity.getSupportFragmentManager(),"LeavePauseConfirmation");
    }
    private void goFromPauseToMain(){
        current_mode = Mode.MAIN;

        controller.path_string.setTravelledPathEnabled(false);
        offWalkSection();
        controller.location_sensor.moveCamera(controller.location_sensor.getMyLocation(), controller.COMFORTABLE_ZOOM_LEVEL + 1, 1);

        updateUI(ColorVariable.Variant.MAIN);

        controller.line_drawer.resetTravelledPath();
    }
    private void goFromPauseToWalk(){
        current_mode = Mode.WALK;

        controller.timer.resume();
        updateUI(ColorVariable.Variant.WALK);

        controller.path_string.setTravelledPathEnabled(true);
        onWalkable();
    }

    private void goFromFinishToMain(){
        current_mode = Mode.MAIN;

        updateUI(ColorVariable.Variant.MAIN);
        offWalkSection();
        controller.location_sensor.moveCamera(controller.location_sensor.getMyLocation(), controller.COMFORTABLE_ZOOM_LEVEL + 1, 1);

        controller.path_string.setTravelledPathEnabled(false);

        controller.line_drawer.resetTravelledPath();
    }









    protected void checkoutLeaveDrawMode() {
        if (controller.button_switch_input.getVariant() == SwitchInputButton.Variant.GPS){
            leaveNavigatorDrawMode();
        } else if (controller.button_switch_input.getVariant() == SwitchInputButton.Variant.DRAW){
            leaveFingerDrawMode();
        }
    }

    protected void checkoutDrawMode(){
        if (controller.button_switch_input.getVariant() == SwitchInputButton.Variant.GPS){
            setNavigatorDrawMode();
        } else if (controller.button_switch_input.getVariant() == SwitchInputButton.Variant.DRAW){
            setFingerDrawMode();
        }
    }
    protected void leaveNavigatorDrawMode(){
        controller.location_sensor.setDrawable(false);
        controller.location_sensor.unSubscribeToLocationUpdate();
    }
    protected void leaveFingerDrawMode(){
        controller.map_sensor.turnOff();
    }
    private void setNavigatorDrawMode(){
        current_draw_mode = DrawMode.LOCATION;
        controller.line_drawer.setDivisionStep(4);
        controller.location_sensor.setDrawable(true);
        controller.location_sensor.subscribeToLocationUpdate();
    }
    private void setFingerDrawMode(){
        current_draw_mode = DrawMode.FINGER;
        controller.line_drawer.setDivisionStep(4);
        controller.map_sensor.turnOn();
    }
}
