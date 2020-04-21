package com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.adapter.MessageAdapter;
import com.midooabdaim.midooabdaimchat.data.model.BlockOrChatList;
import com.midooabdaim.midooabdaimchat.data.model.Chat;
import com.midooabdaim.midooabdaimchat.data.model.Data;
import com.midooabdaim.midooabdaimchat.data.model.MyResponse;
import com.midooabdaim.midooabdaimchat.data.model.Sender;
import com.midooabdaim.midooabdaimchat.data.model.Token;
import com.midooabdaim.midooabdaimchat.data.model.User;
import com.midooabdaim.midooabdaimchat.ui.activity.MainActivity;
import com.midooabdaim.midooabdaimchat.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.midooabdaim.midooabdaimchat.data.api.RetrofitClient.getClient;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Block_List;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Chat_list;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Chats_Data;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Photo_Data;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Request_Code;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Tokens;
import static com.midooabdaim.midooabdaimchat.helper.Constant.Users_Data;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.closeFragment;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.customToast;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.decrypt;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.dismissProgressDialog;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.encrypt;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.getFileExtension;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.onLoadImageFromUrl;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.showProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fragment_message_img_user)
    CircleImageView fragmentMessageImgUser;
    @BindView(R.id.fragment_message_txt_user_name)
    TextView fragmentMessageTxtUserName;
    @BindView(R.id.fragment_message_recycler_view_messages)
    RecyclerView fragmentMessageRecyclerViewMessages;
    @BindView(R.id.fragment_message_txt_blockTV)
    TextView fragmentMessageTxtBlockTV;
    @BindView(R.id.fragment_message_et_type_message)
    EditText fragmentMessageEtTypeMessage;
    @BindView(R.id.fragment_message_message_panel)
    LinearLayout fragmentMessageMessagePanel;
    private FirebaseUser firebaseUser;
    public String userId;
    private LinearLayoutManager linearLayoutManager;
    private boolean isBlock;
    private boolean isUnBlock;
    private StorageReference storageReferencePhoto;
    private DatabaseReference reference;
    private List<Chat> chatList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private ValueEventListener isSeen;
    private Uri imageUri;
    private StorageTask uploadtask;


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intialFragment();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initView();

        userBlockList();

        reference = FirebaseDatabase.getInstance().getReference(Users_Data).child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                fragmentMessageTxtUserName.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    fragmentMessageImgUser.setImageResource(R.drawable.ic_imgprofile);
                } else {
                    onLoadImageFromUrl(fragmentMessageImgUser, user.getImageURL(), getActivity());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
        readMessages(firebaseUser.getUid(), userId);
        isSeenMessage(userId);

        return view;
    }

    private void initView() {
        storageReferencePhoto = FirebaseStorage.getInstance().getReference(Photo_Data);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        // linearLayoutManager.setReverseLayout(true);
        fragmentMessageRecyclerViewMessages.setLayoutManager(linearLayoutManager);
        fragmentMessageRecyclerViewMessages.setHasFixedSize(true);
       /* messageAdapter = new MessageAdapter(getActivity(), chatList);
        fragmentMessageRecyclerViewMessages.setAdapter(messageAdapter);*/
        isBlock = true;
        isUnBlock = false;
    }

    private void readMessages(String myid, String userid) {

        reference = FirebaseDatabase.getInstance().getReference(Chats_Data);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) {

                        chatList.add(chat);

                    } else if (chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {

                        chatList.add(chat);

                    }


                }

                messageAdapter = new MessageAdapter(getActivity(), chatList);
                fragmentMessageRecyclerViewMessages.setAdapter(messageAdapter);
                fragmentMessageRecyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    private void isSeenMessage(String userid) {
        reference = FirebaseDatabase.getInstance().getReference(Chats_Data);

        isSeen = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("isSeen", true);

                        snapshot.getRef().updateChildren(hashMap);

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }

    private void userBlockList() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Block_List).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    BlockOrChatList blockeduser = snapshot.getValue(BlockOrChatList.class);


                    assert blockeduser != null;

                    if (blockeduser.getId().equals(userId)) {

                        fragmentMessageMessagePanel.setVisibility(View.GONE);
                        fragmentMessageTxtBlockTV.setVisibility(View.VISIBLE);

                        isBlock = false;
                        isUnBlock = true;

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(Block_List).child(userId);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    BlockOrChatList blockeduser = snapshot.getValue(BlockOrChatList.class);

                    assert blockeduser != null;

                    if (blockeduser.getId().equals(firebaseUser.getUid())) {
                        fragmentMessageMessagePanel.setVisibility(View.GONE);
                        fragmentMessageTxtBlockTV.setVisibility(View.VISIBLE);

                        isBlock = false;
                        isUnBlock = false;


                    }

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
    public void onPause() {
        super.onPause();
        reference.removeEventListener(isSeen);
    }

    @Override
    public void onStop() {
        unbinder.unbind();
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.message_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.message_menu_block).setEnabled(isBlock);
        menu.findItem(R.id.message_menu_unblock).setEnabled(isUnBlock);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message_menu_block:
                BlockOrChatList blockList = new BlockOrChatList(userId);
                FirebaseDatabase.getInstance().getReference(Block_List)
                        .child(firebaseUser.getUid())
                        .child(userId).setValue(blockList);

                fragmentMessageMessagePanel.setVisibility(View.GONE);
                fragmentMessageTxtBlockTV.setVisibility(View.VISIBLE);

                isBlock = false;
                isUnBlock = true;

                return true;
            case R.id.message_menu_unblock:
                FirebaseDatabase.getInstance().getReference(Block_List)
                        .child(firebaseUser.getUid())
                        .child(userId).removeValue();

                fragmentMessageMessagePanel.setVisibility(View.VISIBLE);
                fragmentMessageTxtBlockTV.setVisibility(View.GONE);

                isBlock = true;
                isUnBlock = false;

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void BackPressed() {
        // closeFragment(getActivity().getSupportFragmentManager(), MessageFragment.this);
        super.BackPressed();
    }

    @OnClick({R.id.fragment_message_btn_send_photo, R.id.fragment_message_btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_message_btn_send_photo:
                openImage();
                break;
            case R.id.fragment_message_btn_send:
                send();
                break;
        }
    }

    private void openImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Request_Code);
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
                                sendMessage(firebaseUser.getUid(), userId, aUri);
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

    private void send() {

        String message = fragmentMessageEtTypeMessage.getText().toString().trim();

        if (!message.equals("")) {

            try {
                sendMessage(firebaseUser.getUid(), userId, encrypt(message));
            } catch (Exception e) {
                e.printStackTrace();
            }


            fragmentMessageEtTypeMessage.setText("");

        }


    }

    private void sendMessage(String sender, String receiver, String message) {
        String currentDateandTime;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                currentDateandTime = new SimpleDateFormat("EEE  MMM d, yyyy h:mm a").format(new Date());
            } else {
                android.text.format.DateFormat df = new android.text.format.DateFormat();
                currentDateandTime = (String) df.format("EEE  MMM d, yyyy h:mm a", new Date());
            }
        } catch (Exception e) {
            currentDateandTime = "un known";
            e.printStackTrace();
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("time", currentDateandTime);
        hashMap.put("isSeen", false);

        reference.child(Chats_Data).push().setValue(hashMap);

        BlockOrChatList chatlist = new BlockOrChatList(receiver);
        FirebaseDatabase.getInstance().getReference(Chat_list)
                .child(sender)
                .child(chatlist.getId()).setValue(chatlist);

        BlockOrChatList chatlist2 = new BlockOrChatList(sender);
        FirebaseDatabase.getInstance().getReference(Chat_list)
                .child(receiver)
                .child(chatlist2.getId()).setValue(chatlist2);


        reference = FirebaseDatabase.getInstance().getReference(Users_Data).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                sendNotification(receiver, user.getUsername(), message);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(String receiver, final String username, final String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Tokens);

        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher
                            , decrypt(msg), username, userId);

                    Sender sender = new Sender(data, token.getToken());
                    getClient().sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200) {

                                        if (response.body().success != 1) {
                                            customToast(getActivity(), getString(R.string.faild), true);
                                        }


                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
