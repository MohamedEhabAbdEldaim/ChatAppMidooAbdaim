package com.midooabdaim.midooabdaimchat.ui.fragment.userCycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.HomeActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Default_Image;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.cleanError;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.customToast;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.disappearKeypad;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.dismissProgressDialog;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.replaceFragment;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.showProgressDialog;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.validationTextInputLayoutListEmpty;
import static com.midooabdaim.midooabdaimchat.helper.InternetState.isActive;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_register_txt_input_user_name)
    TextInputLayout fragmentRegisterTxtInputUserName;
    @BindView(R.id.fragment_register_txt_input_email)
    TextInputLayout fragmentRegisterTxtInputEmail;
    @BindView(R.id.fragment_register_txt_input_password)
    TextInputLayout fragmentRegisterTxtInputPassword;
    @BindView(R.id.fragment_register_txt_input_confirm_password)
    TextInputLayout fragmentRegisterTxtInputConfirmPassword;
    private List<TextInputLayout> textInputLayoutsList = new ArrayList<>();
    private FirebaseAuth auth;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intialFragment();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        auth = FirebaseAuth.getInstance();
        textInputLayoutsList.add(fragmentRegisterTxtInputUserName);
        textInputLayoutsList.add(fragmentRegisterTxtInputEmail);
        textInputLayoutsList.add(fragmentRegisterTxtInputPassword);
        textInputLayoutsList.add(fragmentRegisterTxtInputConfirmPassword);
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
        super.BackPressed();
    }


    @OnClick({R.id.fragment_register_btn_sign})
    public void onViewClicked(View view) {
        disappearKeypad(getActivity(), view);
        switch (view.getId()) {
            case R.id.fragment_register_btn_sign:
                register();
                break;
        }
    }

    private void register() {

        try {
            if (!isActive(getActivity())) {
                customToast(getActivity(), getString(R.string.nointernet), true);
            }
            cleanError(textInputLayoutsList);
            String username = fragmentRegisterTxtInputUserName.getEditText().getText().toString().trim();
            String email = fragmentRegisterTxtInputEmail.getEditText().getText().toString().trim();
            String password = fragmentRegisterTxtInputPassword.getEditText().getText().toString().trim();
            String passwordconfirm = fragmentRegisterTxtInputConfirmPassword.getEditText().getText().toString().trim();
            if (!validationTextInputLayoutListEmpty(textInputLayoutsList, getString(R.string.empty))) {
                return;
            }
            if (isEmailExists(email)) {
                fragmentRegisterTxtInputEmail.setError(getString(R.string.exist));
            }
            if (password.length() < 6) {
                fragmentRegisterTxtInputPassword.setError(getString(R.string.week));
                return;
            }

            if (password.equals(passwordconfirm)) {
                fragmentRegisterTxtInputConfirmPassword.setError(getString(R.string.notmatch));
            }

            addToDataBase(username, email, password);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean isEmailExists(final String email) {
        final boolean[] emailExists = {false};


        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Users_Data);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        User user = snapshot.getValue(User.class);

                        assert user != null;

                        if (user.getEmail().trim().equals(email.trim())) {
                            emailExists[0] = true;
                            break;
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emailExists[0];

    }

    private void addToDataBase(String username, String email, String password) {
        try {
            showProgressDialog(getActivity(), getString(R.string.wait));
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userid = firebaseUser.getUid();
                                User user = new User(username, email, userid, Default_Image);
                                FirebaseDatabase.getInstance().getReference(Users_Data)
                                        .child(userid)
                                        .setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dismissProgressDialog();
                                                    customToast(getActivity(), getString(R.string.createdaccount), false);
                                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                } else {
                                                    dismissProgressDialog();
                                                    customToast(getActivity(), task.getException().getMessage(), true);
                                                }
                                            }
                                        });

                            } else {
                                dismissProgressDialog();
                                customToast(getActivity(), task.getException().getMessage(), true);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
