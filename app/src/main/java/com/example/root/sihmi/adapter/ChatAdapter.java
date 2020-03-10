package com.example.root.sihmi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.sihmi.R;
import com.example.root.sihmi.activity.ChatActivity;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    int size;
    Context context;
    itemClickListener listener;

    public ChatAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
    }

    public ChatAdapter(Context context, int size, itemClickListener listener) {
        this.size = size;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return size;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick();
    }
}
