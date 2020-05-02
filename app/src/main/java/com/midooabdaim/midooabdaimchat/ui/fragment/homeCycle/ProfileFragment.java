package com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.helper.DialogForChange;
import com.midooabdaim.midooabdaimchat.ui.activity.MainActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.midooabdaim.midooabdaimchat.data.local.SharedPrefrance.USER_PASSWORD;
import static com.midooabdaim.midooabdaimchat.data.local.SharedPrefrance.cleanShard;
import static com.midooabdaim.midooabdaimchat.data.local.SharedPrefrance.loadDataString;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Default_Image;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Photo_Data;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Request_Code;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.cleanError;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.customToast;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.dismissProgressDialog;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.getFileExtension;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.showProgressDialog;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.validationTextInputLayoutListEmpty;
import static com.midooabdaim.midooabdaimchat.helper.InternetState.isActive;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_profile_image_profile)
    CircleImageView fragmentProfileImageProfile;
    @BindView(R.id.fragment_profile_txt_user_name)
    TextView fragmentProfileTxtUserName;
    private Uri imageUri;
    private StorageReference storageReferencePhoto;
    private StorageTask uploadtask;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private DatabaseReference reference1;
    private Dialog dialog;
    private TextInputLayout dialogName;
    private TextInputLayout dialogPassword;
    private TextInputLayout dialogConfirmPassword;
    private Button buttonChange;
    private TextView textView;
    private List<TextInputLayout> textInputLayoutsList = new ArrayList<>();


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intialFragment();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        storageReferencePhoto = FirebaseStorage.getInstance().getReference(Photo_Data);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(Users_Data).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                fragmentProfileTxtUserName.setText(user.getUsername());

                if (user.getImageURL().equals(Default_Image)) {
                    fragmentProfileImageProfile.setImageResource(R.drawable.ic_imgprofile);
                } else {
                    onLoadImageFromUrl(fragmentProfileImageProfile, user.getImageURL(), getActivity());
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
        super.BackPressed();
    }

    @OnClick({R.id.fragment_profile_image_profile, R.id.fragment_profile_txt_change_user_name, R.id.fragment_profile_txt_change_password, R.id.fragment_profile_txt_log_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_profile_image_profile:
                openImage();
                break;
            case R.id.fragment_profile_txt_change_user_name:
                changeUserName();
                break;
            case R.id.fragment_profile_txt_change_password:
                changePassword();
                break;
            case R.id.fragment_profile_txt_log_out:
                FirebaseAuth.getInstance().signOut();
                cleanShard(getActivity());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    private void changeUserName() {
        DialogForChange dialogForChange = new DialogForChange();
        dialog = dialogForChange.setupDialog(getActivity());
        dialogName = (TextInputLayout) dialog.findViewById(R.id.dialog_add_txt_input_name);
        dialogPassword = (TextInputLayout) dialog.findViewById(R.id.dialog_add_txt_input_password);
        dialogConfirmPassword = (TextInputLayout) dialog.findViewById(R.id.dialog_add_txt_input_confirm_password);
        buttonChange = (Button) dialog.findViewById(R.id.dialog_add_btn_add);
        textView = (TextView) dialog.findViewById(R.id.dialog_add_txt_ask_to_add);
        dialogName.getEditText().setHint(getString(R.string.username));
        dialogPassword.setVisibility(View.GONE);
        dialogConfirmPassword.setVisibility(View.GONE);
        textView.setText(getString(R.string.addusername));

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogName.getEditText().getText().toString().trim().equals("")) {
                    dialogName.setError(getString(R.string.empty));
                } else {
                    reference1 = FirebaseDatabase.getInstance().getReference(Users_Data).child(firebaseUser.getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", dialogName.getEditText().getText().toString().trim());
                    reference1.updateChildren(map);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void changePassword() {

        DialogForChange dialogForChange = new DialogForChange();
        dialog = dialogForChange.setupDialog(getActivity());
        dialogName = (TextInputLayout) dialog.findViewById(R.id.dialog_add_txt_input_name);
        dialogPassword = (TextInputLayout) dialog.findViewById(R.id.dialog_add_txt_input_password);
        dialogConfirmPassword = (TextInputLayout) dialog.findViewById(R.id.dialog_add_txt_input_confirm_password);
        buttonChange = (Button) dialog.findViewById(R.id.dialog_add_btn_add);
        textView = (TextView) dialog.findViewById(R.id.dialog_add_txt_ask_to_add);
        dialogName.setPasswordVisibilityToggleEnabled(true);
        textInputLayoutsList.add(dialogName);
        textInputLayoutsList.add(dialogPassword);
        textInputLayoutsList.add(dialogConfirmPassword);
        dialogName.getEditText().setHint(getString(R.string.password));
        dialogPassword.getEditText().setHint(getString(R.string.newpassword));
        dialogConfirmPassword.getEditText().setHint(getString(R.string.confirmnewpassword));
        textView.setText(getString(R.string.changepassword));
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordInFirbase();
            }
        });
        dialog.show();
    }

    private void changePasswordInFirbase() {
        cleanError(textInputLayoutsList);
        String pass = loadDataString(getActivity(), USER_PASSWORD);
        String newPassword = dialogPassword.getEditText().getText().toString().trim();
        String passwordConfirm = dialogConfirmPassword.getEditText().getText().toString().trim();
        if (!validationTextInputLayoutListEmpty(textInputLayoutsList, getString(R.string.empty))) {
            return;
        }
        if (!dialogName.getEditText().getText().toString().trim().equals(pass)) {
            customToast(getActivity(), getString(R.string.passwordnotcorrect), true);
        }
        if (newPassword.length() < 6) {
            dialogPassword.setError(getString(R.string.week));
            return;
        }

        if (!newPassword.equals(passwordConfirm)) {
            dialogConfirmPassword.setError(getString(R.string.notmatch));
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "User password updated.");
                            customToast(getActivity(), getString(R.string.passwordupdated), false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customToast(getActivity(), e.getMessage(), true);
            }
        });

        dialog.dismiss();
    }

    private void openImage() {
        if (!isActive(getActivity())) {
            customToast(getActivity(), getString(R.string.nointernet), true);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, Request_Code);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                imageUri = data.getData();
                if (imageUri != null) {

                    final StorageReference fileReference = storageReferencePhoto.child(System.currentTimeMillis()
                            + "." + getFileExtension(imageUri, getActivity()));

                    uploadtask = fileReference.putFile(imageUri);

                    uploadtask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                            showProgressDialog(getActivity(), "Upload is " + progress + "% done");
                        }
                    });
                    uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = (Uri) task.getResult();
                                String aUri = downloadUri.toString();
                                reference1 = FirebaseDatabase.getInstance().getReference(Users_Data).child(firebaseUser.getUid());
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("imageURL", aUri);
                                reference1.updateChildren(map);
                                dismissProgressDialog();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            customToast(getActivity(), e.getMessage(), true);
                            dismissProgressDialog();

                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            customToast(getActivity(), getString(R.string.canceled), true);
                            dismissProgressDialog();
                        }
                    });

                } else {
                    customToast(getActivity(), getString(R.string.noImage), true);
                    dismissProgressDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
