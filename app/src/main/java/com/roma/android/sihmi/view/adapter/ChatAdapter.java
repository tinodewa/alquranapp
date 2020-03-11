package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chating;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    int size;
    Context context;
    List<Chating> list;
    itemClickListener listener;
//    String theLastMsg, theLastTimeMsg;
    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;

    public ChatAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
    }

    public ChatAdapter(Context context, List<Chating> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        appDb = AppDb.getInstance(context);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
    }

    public ChatAdapter(Context context, int size, itemClickListener listener) {
        this.size = size;
        this.context = context;
        this.listener = listener;
    }

    public void updateData(List<Chating> contacts){
        this.list = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Chating chating = list.get(i);
        String[] msg = chating.getLast_message().split("split100x");
        String receiver = msg[0];
        String lastMessage = msg[1];

        Contact user = contactDao.getContactById(chating.get_id());
        viewHolder.tvNama.setText(user.getFullName());
        viewHolder.tvDesc.setText(lastMessage);

        if (chating.getUnread() > 0 && receiver.equals(userDao.getUser().get_id())){
            viewHolder.tvUnread.setVisibility(View.VISIBLE);
            viewHolder.tvUnread.setText(String.valueOf(chating.getUnread()));
        } else {
            viewHolder.tvUnread.setVisibility(View.GONE);
        }

        if (Tools.getDateFromMillis(chating.getTime_message()).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
//            viewHolder.tvTime.setText(Tools.getTimeAMPMFromMillis(chating.getTime_message()));
//            viewHolder.tvTime.setText(context.getString(R.string.hari_ini));
            if (Constant.getLanguage().equals("id")){
                viewHolder.tvTime.setText("Hari Ini");
            } else {
                viewHolder.tvTime.setText("Today");
            }
        } else if (Tools.getDateFromMillis(chating.getTime_message()+ TimeUnit.DAYS.toMillis(1)).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
//            viewHolder.tvTime.setText(context.getString(R.string.kemarin));
            if (Constant.getLanguage().equals("id")){
                viewHolder.tvTime.setText("Kemarin");
            } else {
                viewHolder.tvTime.setText("Yesterday");
            }
        } else {
            viewHolder.tvTime.setText(Tools.getDateFromMillis(chating.getTime_message()));
        }

        String firstLetter = String.valueOf(user.getFullName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(chating);
        //int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        try {
            if (user.getImage() != null && !user.getImage().trim().isEmpty()) {
                Glide.with(context).load(user.getImage()).into(viewHolder.ivPhoto);
                viewHolder.ivPhoto.setVisibility(View.VISIBLE);
                viewHolder.ivIntial.setVisibility(View.GONE);
            } else {
                viewHolder.ivIntial.setImageDrawable(drawable);
                viewHolder.ivIntial.setVisibility(View.VISIBLE);
                viewHolder.ivPhoto.setVisibility(View.GONE);
            }
        } catch (NullPointerException e){
            viewHolder.ivIntial.setImageDrawable(drawable);
            viewHolder.ivIntial.setVisibility(View.VISIBLE);
            viewHolder.ivPhoto.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(chating, false);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClick(chating, true);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvDesc)
        TextView tvDesc;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.ivInitial)
        ImageView ivIntial;
        @BindView(R.id.tvUnread)
        TextView tvUnread;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Chating chating, boolean isLongCLick);
    }
}
