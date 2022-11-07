package com.helkor.project.dialogs.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helkor.project.R;
import com.helkor.project.connection.websocket.TokenUsable;

public class TokenListAdapter extends RecyclerView.Adapter<TokenListAdapter.ViewHolder> {
    public interface Listener{
        void onAcceptClick(String token);
        void onRejectClick(String token);
    }
    private final LayoutInflater inflater;
    private Listener listener;
    private TokenUsable tokenUsable;
    public TokenListAdapter(Context context,Object implementationContext){
        trySetListener(implementationContext);
        trySetTokenUsable(implementationContext);
        inflater = LayoutInflater.from(context);
    }
    private void trySetListener(Object implementationContext){
        try{
            listener = (Listener) implementationContext;
        } catch (ClassCastException e){
            throw new RuntimeException(implementationContext + " must implement Listener");
        }
    }
    private void trySetTokenUsable(Object implementationContext){
        try{
            tokenUsable = (TokenUsable) implementationContext;
        } catch (ClassCastException e){
            throw new RuntimeException(implementationContext + " must implement TokenUsable");
        }
    }

    @NonNull
    @Override
    public TokenListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.foreign_tocken_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tokenValue.setText(tokenUsable.getToken(position));
        holder.token = tokenUsable.getToken(position);
    }

    @Override
    public int getItemCount() {
        return tokenUsable.getTokenAmount();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton rejectButton;
        public ImageButton acceptButton;
        public TextView tokenValue;
        public String token;
        ViewHolder(View view){
            super(view);
            rejectButton = view.findViewById(R.id.clear_token_button);
            acceptButton = view.findViewById(R.id.accept_token_button);
            tokenValue = view.findViewById(R.id.token_value);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRejectClick(token);
                }
            });
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAcceptClick(token);
                }
            });
        }
    }
}
