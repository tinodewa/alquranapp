package com.example.root.sihmi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.root.sihmi.R;
import com.example.root.sihmi.activity.ChatActivity;
import com.example.root.sihmi.activity.ProfilActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    int size;
    Context context;
    int tab;

    public UserAdapter(Context context, int size, int tab) {
        this.context = context;
        this.size = size;
        this.tab = tab;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.bindView(tab);

    }

    @Override
    public int getItemCount() {
        return size;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.btnDetail)
        Button btnDetail;
        @BindView(R.id.btnUpdate)
        Button btnUpdate;
        @BindView(R.id.btnSetuju)
        Button btnSetuju;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(int tab){
            if (tab == 1){
                btnUpdate.setVisibility(View.GONE);
            } else {
                btnSetuju.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.btnUpdate)
        public void goUpdate(){
            itemView.getContext().startActivity(new Intent(itemView.getContext().getApplicationContext(), ProfilActivity.class));
        }

        @OnClick(R.id.btnDetail)
        public void goDetail(){
            itemView.getContext().startActivity(new Intent(itemView.getContext().getApplicationContext(), ProfilActivity.class));
        }
    }
}
