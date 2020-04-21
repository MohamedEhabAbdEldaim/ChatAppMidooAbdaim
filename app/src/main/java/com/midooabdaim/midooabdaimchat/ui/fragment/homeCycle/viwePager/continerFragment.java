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

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.adapter.ViewPagerWithFragmentAdapter;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.MainActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Default_Image;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.customToast;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;
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
    ViewPager fragmentContainerViewPager;

    private ViewPagerWithFragmentAdapter viewPagerWithFragmentAdapter;
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
        setHasOptionsMenu(true);

        viewPagerWithFragmentAdapter = new ViewPagerWithFragmentAdapter(getChildFragmentManager(), 1, getActivity());
        fragmentContainerViewPager.setAdapter(viewPagerWithFragmentAdapter);
        fragmentContainerTabLayoutId.setupWithViewPager(fragmentContainerViewPager);

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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout_menu_logout:
               /* if (!isActive(getActivity())) {
                    customToast(getActivity(), getString(R.string.nointernet), true);
                } else {*/
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                //   }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

}