package com.helkor.project.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.helkor.project.R;
import com.helkor.project.buttons.SwitchArrowButton;
import com.helkor.project.buttons.Utils.LittleButton;
import com.helkor.project.dialogs.util.TokenListAdapter;

public class RouteSendingDialogFragment extends DialogFragment implements LittleButton.Listener{


    public interface Listener{
        void onCancel();
    }
    RecyclerView recyclerView;
    TokenListAdapter adapter;
    SwitchArrowButton switchArrowButton;
    Object implementationContext;
    Listener listener;
    public RouteSendingDialogFragment(Object implementationContext){
        this.implementationContext = implementationContext;
        tryAddListener(implementationContext);
    }
    private void tryAddListener(Object implementationContext){
        try {
            listener = (Listener) implementationContext;
        } catch (ClassCastException e){
            throw new RuntimeException(implementationContext + " must implement Listener");
        }
    }
    public void onCancel (DialogInterface dialog){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.route_connection_dialog_fragment,null,false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        recyclerView = view.findViewById(R.id.scroll_list);
        adapter = new TokenListAdapter(getActivity(), implementationContext);
        recyclerView.setAdapter(adapter);

        switchArrowButton = new SwitchArrowButton(getActivity(),view,this,R.id.button_switch_arrow);
    }
    public void notifyItemRemoved(int index){
        adapter.notifyItemRemoved(index);
    }
    public void notifyItemInserted(int index){
        adapter.notifyItemInserted(index);
    }
    @Override
    public void onClickLittleButton(View view) {
        switchArrowButton.nextVariant();
        checkMode();
    }
    private void checkMode(){
        switch (switchArrowButton.getVariant()){
            case UPLOAD:
                setUploadMode();
                break;
            case DOWNLOAD:
                setDownloadMode();
                break;
        }
    }
    private void setDownloadMode(){
        ((TextView) getView().findViewById(R.id.foreign_token_text)).setText("Input foreign token:");
        ((TextView) getView().findViewById(R.id.mode_text)).setText("Get route");

        getView().findViewById(R.id.scroll_list).setVisibility(View.GONE);
        getView().findViewById(R.id.token_input_layout_outside).setVisibility(View.VISIBLE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) getView().findViewById(R.id.constraintlayout));
        constraintSet.connect(R.id.middle_divider,ConstraintSet.TOP,R.id.token_input_layout_outside,ConstraintSet.BOTTOM,0);
        constraintSet.applyTo(getView().findViewById(R.id.constraintlayout));
    }
    private void setUploadMode(){
        ((TextView) getView().findViewById(R.id.foreign_token_text)).setText("Foreign tokens' requests:");
        ((TextView) getView().findViewById(R.id.mode_text)).setText("Send route");

        getView().findViewById(R.id.scroll_list).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.token_input_layout_outside).setVisibility(View.GONE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) getView().findViewById(R.id.constraintlayout));
        constraintSet.connect(R.id.middle_divider,ConstraintSet.TOP,R.id.scroll_list,ConstraintSet.BOTTOM,0);
        constraintSet.applyTo(getView().findViewById(R.id.constraintlayout));
    }
}
