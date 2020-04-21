package com.midooabdaim.midooabdaimchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.data.model.Chat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.decrypt;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private Context context;
    private List<Chat> chatList = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private int message_right = 1;
    private int message_left = 0;

    public MessageAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == message_right) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right,
                    parent, false);

            return new ViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left,
                    parent, false);

            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {

        holder.itemChatMessageTime.setText(chatList.get(position).getTime());
        if (chatList.get(position).getMessage().contains("https://firebasestorage.googleapis.com")) {
            holder.itemChatShowMessage.setVisibility(View.GONE);
            holder.itemChatSentPhoto.setVisibility(View.VISIBLE);
            onLoadImageFromUrl(holder.itemChatSentPhoto, chatList.get(position).getMessage(), context);
        } else {
            holder.itemChatShowMessage.setVisibility(View.VISIBLE);
            holder.itemChatSentPhoto.setVisibility(View.GONE);
            holder.itemChatShowMessage.setText(decrypt(chatList.get(position).getMessage()));

        }
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            if (chatList.get(position).isSeen()) {
                holder.itemChatImgSeen.setImageResource(R.drawable.ic_seen);
            } else {
                holder.itemChatImgSeen.setImageResource(R.drawable.ic_unseen);
            }
        }
    }

    private void setAction(ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {

        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {

            return message_right;

        } else {

            return message_left;

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_chat_message_time)
        TextView itemChatMessageTime;
        @BindView(R.id.item_chat_show_message)
        TextView itemChatShowMessage;
        @BindView(R.id.item_chat_sent_photo)
        ImageView itemChatSentPhoto;
        @Nullable
        @BindView(R.id.item_chat_img_seen)
        ImageView itemChatImgSeen;
        private View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}
