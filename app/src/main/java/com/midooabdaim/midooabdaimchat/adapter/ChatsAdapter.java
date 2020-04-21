package com.midooabdaim.midooabdaimchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.data.model.Chat;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.HomeActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.MessageFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Chats_Data;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Default_Image;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.replaceFragment;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private FirebaseUser firebaseUser;
    private Context context;
    private Activity activity;
    private List<User> userList = new ArrayList<>();
    private DatabaseReference reference;

    public ChatsAdapter(Activity activity, List<User> userList) {
        this.context = activity;
        this.activity = activity;
        this.userList = userList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_chat,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.itemUserChatLlMessage.setVisibility(View.VISIBLE);
        if (user.getImageURL().equals(Default_Image)) {
            holder.itemUserChatImgUser.setImageResource(R.drawable.ic_imgprofile);
        } else {
            onLoadImageFromUrl(holder.itemUserChatImgUser, user.getImageURL(), context);
        }
        holder.itemUserChatTxtUserName.setText(user.getUsername());
        reference = FirebaseDatabase.getInstance().getReference(Chats_Data);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;


                    //////////////////////////////////////////////////////
                    ///////////////////////////////////////////////
                    /////////not complete

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAction(ViewHolder holder, int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) activity;
                MessageFragment messageFragment = new MessageFragment();
                messageFragment.userId = userList.get(position).getId();
                replaceFragment(homeActivity.getSupportFragmentManager(), R.id.home_activity_fl_id, messageFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        @BindView(R.id.item_user_chat_img_user)
        CircleImageView itemUserChatImgUser;
        @BindView(R.id.item_user_chat_txt_user_name)
        TextView itemUserChatTxtUserName;
        @BindView(R.id.item_user_chat_have_message)
        ImageView itemUserChatHaveMessage;
        @BindView(R.id.item_user_chat_sender_reciever)
        TextView itemUserChatSenderReciever;
        @BindView(R.id.item_user_chat_last_message)
        TextView itemUserChatLastMessage;
        @BindView(R.id.item_user_chat_last_message_time)
        TextView itemUserChatLastMessageTime;
        @BindView(R.id.item_user_chat_ll_message)
        LinearLayout itemUserChatLlMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}
