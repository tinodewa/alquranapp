package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.ViewHolder> {
    Context context;
    List<Chat> list;
    int type;
    itemClickListener listener;
    Typeface britanic;
    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;
    String user;

    public RoomChatAdapter(Context context, List<Chat> list, int type, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.listener = listener;
        britanic  = Typeface.createFromAsset(context.getAssets(),"fonts/Britanic.ttf");
        appDb = AppDb.getInstance(context);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
        user = userDao.getUser().get_id();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_chat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        Chat chat = list.get(i);
        if (type == 1){
            viewHolder.tvName.setVisibility(View.GONE);
        } else {
            viewHolder.tvName.setVisibility(View.VISIBLE);
            viewHolder.tvName.setText(contactDao.getContactById(chat.getSender()).getNama_depan());
        }

        if (chat.getSender().equals(user)){
            Log.d("haiRoma", "onBindViewHolder: send "+chat.getMessage());
            viewHolder.llReceive.setVisibility(View.GONE);

            if (chat.getType().equalsIgnoreCase(Constant.TEXT)){
                viewHolder.tvSend.setVisibility(View.VISIBLE);
                viewHolder.tvTimeSend.setVisibility(View.VISIBLE);
                viewHolder.imgSend.setVisibility(View.GONE);
                viewHolder.tvMessageSendSticker.setVisibility(View.GONE);
            } else if (chat.getType().equalsIgnoreCase(Constant.STICKER)){
                viewHolder.tvSend.setVisibility(View.GONE);
                viewHolder.tvTimeSend.setVisibility(View.VISIBLE);
                viewHolder.imgSend.setVisibility(View.GONE);
                viewHolder.tvMessageSendSticker.setVisibility(View.VISIBLE);
                viewHolder.llSend.setBackgroundColor(Color.TRANSPARENT);
//                viewHolder.tvMessageSendSticker.setTextSize(50);
                if (Constant.loadNightModeState()) {
                    viewHolder.tvMessageSendSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.WHITE, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeSend.setTextColor(viewHolder.itemView.getResources().getColor(R.color.colorTextLight));
                }
                else {
                    viewHolder.tvMessageSendSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.BLACK, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeSend.setTextColor(Color.BLACK);
                }
                viewHolder.tvMessageSendSticker.setTypeface(britanic);

            } else {
                viewHolder.tvSend.setVisibility(View.GONE);
                viewHolder.tvTimeSend.setVisibility(View.GONE);
                viewHolder.imgSend.setVisibility(View.VISIBLE);
                viewHolder.tvMessageSendSticker.setVisibility(View.GONE);
            }

            viewHolder.tvSend.setText(chat.getMessage());
            viewHolder.tvMessageSendSticker.setText(chat.getMessage().toUpperCase());
            viewHolder.tvTimeSend.setText(Tools.getTimeFromMillis(chat.getTime()));
            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)){
                Glide.with(context).load(chat.getMessage()).into(viewHolder.imgSend);
            }

        } else {
            Log.d("haiRoma", "onBindViewHolder: receive "+chat.getMessage());
            viewHolder.llSend.setVisibility(View.GONE);

            if (chat.getType().equalsIgnoreCase(Constant.TEXT)){
                viewHolder.tvReceive.setVisibility(View.VISIBLE);
                viewHolder.tvTimeReceive.setVisibility(View.VISIBLE);
                viewHolder.imgReceive.setVisibility(View.GONE);
                viewHolder.tvMessageReceiveSticker.setVisibility(View.GONE);
            } else if (chat.getType().equalsIgnoreCase(Constant.STICKER)){
                viewHolder.tvReceive.setVisibility(View.GONE);
                viewHolder.tvTimeReceive.setVisibility(View.VISIBLE);
                viewHolder.imgReceive.setVisibility(View.GONE);
                viewHolder.tvMessageReceiveSticker.setVisibility(View.VISIBLE);
                viewHolder.llReceive.setBackgroundColor(Color.TRANSPARENT);
//                viewHolder.tvReceive.setTextSize(50);
                if (Constant.loadNightModeState()) {
                    viewHolder.tvMessageReceiveSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.WHITE, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeReceive.setTextColor(viewHolder.itemView.getResources().getColor(R.color.colorTextLight));
                }
                else {
                    viewHolder.tvMessageReceiveSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.BLACK, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeReceive.setTextColor(Color.BLACK);
                }
                viewHolder.tvMessageReceiveSticker.setTypeface(britanic);
            } else {
                viewHolder.tvReceive.setVisibility(View.GONE);
                viewHolder.tvTimeReceive.setVisibility(View.GONE);
                viewHolder.imgReceive.setVisibility(View.VISIBLE);
                viewHolder.tvMessageReceiveSticker.setVisibility(View.GONE);
            }

            viewHolder.tvReceive.setText(chat.getMessage());
            viewHolder.tvMessageReceiveSticker.setText(chat.getMessage().toUpperCase());
            viewHolder.tvTimeReceive.setText(Tools.getTimeFromMillis(chat.getTime()));
            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)){
                Glide.with(context).load(chat.getMessage()).into(viewHolder.imgReceive);
            }
        }

        if (i>0){
            if (Tools.getDateFromMillis(list.get(i).getTime()).equalsIgnoreCase(Tools.getDateFromMillis(list.get(i-1).getTime()))){
                viewHolder.tvDate.setVisibility(View.GONE);
            } else {
                viewHolder.tvDate.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.tvDate.setVisibility(View.VISIBLE);
        }

        if (Tools.getDateFromMillis(chat.getTime()).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
            viewHolder.tvDate.setText(context.getString(R.string.hari_ini));
        } else if (Tools.getDateFromMillis(chat.getTime()+ TimeUnit.DAYS.toMillis(1)).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
            viewHolder.tvDate.setText(context.getString(R.string.kemarin));
        } else {
            viewHolder.tvDate.setText(Tools.getDateFromMillis(chat.getTime()));
        }

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(chat));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvMessageReceive)
        TextView tvReceive;
        @BindView(R.id.tvMessageSend)
        TextView tvSend;
        @BindView(R.id.tvTimeReceive)
        TextView tvTimeReceive;
        @BindView(R.id.tvTimeSend)
        TextView tvTimeSend;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.llReceive)
        LinearLayout llReceive;
        @BindView(R.id.llSend)
        LinearLayout llSend;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.img_receiver)
        ImageView imgReceive;
        @BindView(R.id.img_send)
        ImageView imgSend;
        @BindView(R.id.tvMessageReceiveSticker)
        TextView tvMessageReceiveSticker;
        @BindView(R.id.tvMessageSendSticker)
        TextView tvMessageSendSticker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Chat chat);
    }
}
