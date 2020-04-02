package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ProfileChatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    int size;
    Context context;
    List<Contact> list;
    int tab;
    itemClickListener listener;
    LevelDao levelDao;


    public UserAdapter(Context context, List<Contact> list, int tab) {
        this.context = context;
        this.list = list;
        this.tab = tab;
    }

    public UserAdapter(Context context, List<Contact> list, int tab, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.tab = tab;
        this.listener = listener;
        levelDao = AppDb.getInstance(context).levelDao();
    }

    public UserAdapter(Context context, int size, int tab) {
        this.context = context;
        this.size = size;
        this.tab = tab;
    }

    public void updateData(List<Contact> list){
        this.list.clear();
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pengguna, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.bindView(list.get(i), tab);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_nama)
        TextView tvNama;
        @BindView(R.id.tv_ket)
        TextView tvKet;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.iv_initial)
        ImageView ivInitial;
        @BindView(R.id.iv_detail)
        ImageView ivDetail;
        @BindView(R.id.switch_admin)
        SwitchCompat aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(final Contact contact, int tab){
            if (tab == 1){
                int level = levelDao.getLevel(contact.getId_roles())+1;
                tvKet.setText("Mengajukan diri untuk "+ levelDao.getNamaLevel(level));
                aSwitch.setChecked(false);
            } else if (tab == 3){
                tvKet.setText(levelDao.getNamaLevel(contact.getId_roles()));
                if (contact.getId_level() == Constant.USER_NON_LK) {
                    aSwitch.setChecked(false);
                    aSwitch.setEnabled(false);
                }
                else {
                    aSwitch.setChecked(true);
                }
            } else {
                String ket;
                if (contact.getId_level() == 5){
                    ket = "Komisariat";
                } else if (contact.getId_level() == 8){
                    ket = "BPL";
                } else if (contact.getId_level() == 11){
                    ket = "Alumni";
                } else if (contact.getId_level() == 13) {
                    ket = "Cabang";
                } else if (contact.getId_level() == 16){
                    ket = "PB HMI";
                } else if (contact.getId_level() == Constant.USER_SECOND_ADMIN) {
                    ket = "Nasional";
                } else {
                    ket = "Admin";
                }
//                tvKet.setText(CoreApplication.get().getAppDb().interfaceDao().getNamaLevel(contact.getId_roles())+".");
                tvKet.setText(ket);
                aSwitch.setChecked(true);
            }
            tvNama.setText(contact.getFullName());
            tvDate.setText(Tools.getDateLaporanFromMillis(System.currentTimeMillis()));
            if (contact.getImage() != null && !contact.getImage().trim().isEmpty()){
                ivPhoto.setVisibility(View.VISIBLE);
                ivInitial.setVisibility(View.GONE);
                Glide.with(itemView.getContext()).load(contact.getImage()).into(ivPhoto);
            } else {
                Tools.initial(ivInitial, contact.getFullName());
                ivPhoto.setVisibility(View.GONE);
                ivInitial.setVisibility(View.VISIBLE);
            }

            Intent profileChatIntent = new Intent(itemView.getContext(), ProfileChatActivity.class).putExtra("iduser", contact.get_id());
            if (contact.getId_level() != Constant.USER_NON_LK && Tools.isSuperAdmin()) {
                profileChatIntent.putExtra("MODE_ACCEPTED", true);
            }
            ivDetail.setOnClickListener(v -> itemView.getContext().startActivity(profileChatIntent));

            itemView.setOnClickListener(v -> itemView.getContext().startActivity(profileChatIntent));

            aSwitch.setOnClickListener(v -> {
                aSwitch.setChecked(true);
                if (tab != 3) {
                    Tools.showDialogCustom(itemView.getContext(), "Konfirmasi", context.getString(R.string.konfirm_ganti_admin_ket), Constant.LIHAT, ket -> {
//                    aSwitch.setChecked(false);
                        listener.onItemClick(contact);
                    });
                } else {
                    Tools.showDialogCustom(itemView.getContext(), "Konfirmasi", context.getString(R.string.konfirm_akses_hapus_ket), Constant.LIHAT, ket -> {
//                    aSwitch.setChecked(false);
                        listener.onItemClick(contact);
                    });
                }
            });
        }
    }

    public interface itemClickListener{
        void onItemClick(Contact contact);
    }
}
