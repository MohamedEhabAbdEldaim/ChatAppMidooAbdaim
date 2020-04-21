package com.midooabdaim.midooabdaimchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.HomeActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.MessageFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.continerFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Default_Image;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.replaceFragment;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<User> userList = new ArrayList<>();

    public UsersAdapter(Activity activity, List<User> userList) {
        this.context = activity;
        this.activity = activity;
        this.userList = userList;
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
        if (user.getImageURL().equals(Default_Image)) {
            holder.itemUserChatImgUser.setImageResource(R.drawable.ic_imgprofile);
        } else {
            onLoadImageFromUrl(holder.itemUserChatImgUser, user.getImageURL(), context);
        }
        holder.itemUserChatTxtUserName.setText(user.getUsername());
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

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}
