package com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.adapter.ChatsAdapter;
import com.midooabdaim.midooabdaimchat.data.model.BlockOrChatList;
import com.midooabdaim.midooabdaimchat.data.model.Chat;
import com.midooabdaim.midooabdaimchat.data.model.Token;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.BaseActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Chat_list;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Chats_Data;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Tokens;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    Unbinder unbinder;
    @BindView(R.id.fragment_users_chat_group_recycler_view)
    RecyclerView fragmentUsersChatGroupRecyclerView;
    @BindView(R.id.fragment_users_chat_group_tv_no_data)
    TextView fragmentUsersChatGroupTvNoData;
    @BindView(R.id.fragment_group_fbt_add_group)
    FloatingActionButton fragmentGroupFbtAddGroup;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseUser firebaseUser;
    private List<User> users, usersSortedMessage, usersSortedMessageUnDuplicated, getUsersSearched;
    private List<BlockOrChatList> chatListId;
    private DatabaseReference reference;
    private ChatsAdapter chatAdaptar;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intialFragment();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_chat_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initView();
        reference = FirebaseDatabase.getInstance().getReference(Chat_list).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListId.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    BlockOrChatList chatList = snapshot.getValue(BlockOrChatList.class);
                    chatListId.add(chatList);

                }
                if (chatListId.size() == 0) {
                    fragmentUsersChatGroupTvNoData.setVisibility(View.VISIBLE);
                    fragmentUsersChatGroupTvNoData.setText(getString(R.string.nodatausers));
                } else {
                    fragmentUsersChatGroupTvNoData.setVisibility(View.GONE);
                    readUsers();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                UpdateToken(deviceToken);
            }
        });
        return view;
    }

    private void UpdateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Tokens);
        Token token1 = new Token(token);
        ref.child(firebaseUser.getUid()).setValue(token1);
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        users = new ArrayList<>();
        usersSortedMessage = new ArrayList<>();
        usersSortedMessageUnDuplicated = new ArrayList<>();
        chatListId = new ArrayList<>();
        getUsersSearched = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fragmentGroupFbtAddGroup.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentUsersChatGroupRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Users_Data);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    for (BlockOrChatList chatList : chatListId) {

                        if (user.getId().equals(chatList.getId())) {
                            users.add(user);
                            break;
                        }

                    }


                }

                sortUserUpdateChat();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sortUserUpdateChat() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Chats_Data);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersSortedMessage.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    assert firebaseUser != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) || chat.getSender()
                            .equals(firebaseUser.getUid())) {

                        for (User user : users) {
                            if (chat.getReceiver().equals(user.getId()) || chat.getSender()
                                    .equals(user.getId())) {
                                usersSortedMessage.add(0, user);
                                break;
                            }
                        }


                    }


                }

                //remove duplicate
                usersSortedMessageUnDuplicated.clear();
                for (User user : usersSortedMessage) {
                    if (!usersSortedMessageUnDuplicated.contains(user)) {
                        usersSortedMessageUnDuplicated.add(user);
                    }
                }

                chatAdaptar = new ChatsAdapter(getActivity(), usersSortedMessageUnDuplicated);
                fragmentUsersChatGroupRecyclerView.setAdapter(chatAdaptar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        intialFragment();
        super.onStart();
    }

    @Override
    public void onStop() {
        unbinder.unbind();
        super.onStop();
    }

    @Override
    public void BackPressed() {
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        SearchManager searchManager =
                (SearchManager) ((BaseActivity) getActivity()).getSystemService(((BaseActivity) getActivity()).SEARCH_SERVICE);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.search_menu_action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(((BaseActivity) getActivity()).getComponentName()));
        searchView.setOnQueryTextListener(ChatsFragment.this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    private void search(String query) {
        getUsersSearched.clear();
        query = query.toLowerCase();
        //  DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Users_Data);
        String finalQuery = query;

       /* reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getUsersSearched.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    for (BlockOrChatList chatList : chatListId) {

                        if (user.getId().equals(chatList.getId()) && user.getUsername().toLowerCase().contains(finalQuery)) {

                            getUsersSearched.add(user);

                        }


                    }


                }


                chatAdaptar = new ChatsAdapter(getActivity(), getUsersSearched);
                fragmentUsersChatGroupRecyclerView.setAdapter(chatAdaptar);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        for (User user : usersSortedMessageUnDuplicated) {
            if (user.getUsername().toLowerCase().contains(finalQuery)) {
                getUsersSearched.add(user);
            }
        }
        chatAdaptar = new ChatsAdapter(getActivity(), getUsersSearched);
        fragmentUsersChatGroupRecyclerView.setAdapter(chatAdaptar);

    }

}
