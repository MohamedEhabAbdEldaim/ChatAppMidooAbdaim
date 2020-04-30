package com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.adapter.ViewPagerWithFragmentAdapter;
import com.midooabdaim.midooabdaimchat.adapter.ViewPagerWithFragmentAdapter1;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.MainActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Default_Image;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.customToast;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.replaceFragment;
import static com.midooabdaim.midooabdaimchat.helper.InternetState.isActive;

/**
 * A simple {@link Fragment} subclass.
 */
public class continerFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_container_img_user)
    CircleImageView fragmentContainerImgUser;
    @BindView(R.id.fragment_container_txt_user_name)
    TextView fragmentContainerTxtUserName;
    @BindView(R.id.fragment_container_tab_layout_id)
    TabLayout fragmentContainerTabLayoutId;
    @BindView(R.id.fragment_container_view_pager)
    ViewPager2 fragmentContainerViewPager;

    private ViewPagerWithFragmentAdapter1 viewPagerWithFragmentAdapter;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    public continerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intialFragment();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_container, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();

        viewPagerWithFragmentAdapter = new ViewPagerWithFragmentAdapter1(this);

        viewPagerWithFragmentAdapter.addPager(new ChatsFragment(), getString(R.string.chats));
        viewPagerWithFragmentAdapter.addPager(new UsersFragment(), getString(R.string.users));
        viewPagerWithFragmentAdapter.addPager(new GroupsFragment(), getString(R.string.groups));

        fragmentContainerViewPager.setAdapter(viewPagerWithFragmentAdapter);

        // Returns the page title for the top indicator
        new TabLayoutMediator(fragmentContainerTabLayoutId, fragmentContainerViewPager,
                (tab, position) -> tab.setText(viewPagerWithFragmentAdapter.titles.get(position))
        ).attach();

        return view;
    }

    private void initView() {
        if (!isActive(getActivity())) {
            customToast(getActivity(), getString(R.string.nointernet), true);
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(Users_Data).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                fragmentContainerTxtUserName.setText(user.getUsername());

                if (user.getImageURL().equals(Default_Image)) {
                    fragmentContainerImgUser.setImageResource(R.drawable.ic_imgprofile);
                } else {
                    onLoadImageFromUrl(fragmentContainerImgUser, user.getImageURL(), getActivity());
                }


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

    @OnClick(R.id.fragment_container_img_user)
    public void onViewClicked() {
        replaceFragment(getActivity().getSupportFragmentManager(), R.id.home_activity_fl_id, new ProfileFragment());
    }
}
