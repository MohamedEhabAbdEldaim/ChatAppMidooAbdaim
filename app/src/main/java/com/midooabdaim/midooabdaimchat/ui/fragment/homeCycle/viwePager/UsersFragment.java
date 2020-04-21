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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.adapter.UsersAdapter;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.BaseActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    Unbinder unbinder;
    @BindView(R.id.fragment_users_chat_group_recycler_view)
    RecyclerView fragmentUsersChatGroupRecyclerView;
    @BindView(R.id.fragment_group_fbt_add_group)
    FloatingActionButton fragmentGroupFbtAddGroup;
    @BindView(R.id.fragment_users_chat_group_tv_no_data)
    TextView fragmentUsersChatGroupTvNoData;
    private LinearLayoutManager linearLayoutManager;
    private List<User> users;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private UsersAdapter usersAdapter;
    private List<User> getUsersSearched;

    public UsersFragment() {
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
        return view;
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        users = new ArrayList<>();
        getUsersSearched=new ArrayList<>();
        fragmentGroupFbtAddGroup.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentUsersChatGroupRecyclerView.setLayoutManager(linearLayoutManager);
        readUsers();
    }

    private void readUsers() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(Users_Data);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;

                    if (!user.getId().equals(firebaseUser.getUid())) {
                        users.add(user);
                    }

                }

                if (users.size() == 0) {
                    fragmentUsersChatGroupTvNoData.setVisibility(View.VISIBLE);
                    fragmentUsersChatGroupTvNoData.setText(getString(R.string.nodatausers));
                } else {
                    fragmentUsersChatGroupTvNoData.setVisibility(View.GONE);
                    usersAdapter = new UsersAdapter(getActivity(), users);
                    fragmentUsersChatGroupRecyclerView.setAdapter(usersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        searchView.setOnQueryTextListener(UsersFragment.this);
        super.onCreateOptionsMenu(menu, inflater);
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
        String finalQuery = query;
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(finalQuery)) {
                getUsersSearched.add(user);
            }
        }
        usersAdapter = new UsersAdapter(getActivity(), getUsersSearched);
        fragmentUsersChatGroupRecyclerView.setAdapter(usersAdapter);
    }
}
