package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.DataKader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaporanDataPelatihanAdapter extends RecyclerView.Adapter<LaporanDataPelatihanAdapter.ViewHolder> {
    Context context;
    boolean isSlider;
    List<DataKader> list;
    itemClickListener listener;

    public LaporanDataPelatihanAdapter(Context context,boolean isSlider, List<DataKader> list, itemClickListener listener) {
        this.context = context;
        this.isSlider = isSlider;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<DataKader> list1){
        this.list = list1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (isSlider) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pelatihan, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pelatihan_glider, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        DataKader dataKader = list.get(i);
        viewHolder.tvJudul.setText(dataKader.getNama());
        viewHolder.tvTotal.setText(""+dataKader.getTotal());

        viewHolder.itemView.setOnClickListener(v -> {
            listener.onItemClick(dataKader);
        });
    }

    @Override
    public int getItemCount() {
        return list.size()>0 ? list.size() : 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_judul)
        TextView tvJudul;
        @BindView(R.id.tv_total)
        TextView tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(DataKader dataKader);
    }
}
