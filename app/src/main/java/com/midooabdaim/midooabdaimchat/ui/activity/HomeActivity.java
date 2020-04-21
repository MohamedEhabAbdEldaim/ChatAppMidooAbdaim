package com.midooabdaim.midooabdaimchat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.MessageFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.continerFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.userCycle.LoginFragment;

import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.replaceFragment;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        boolean message = intent.getBooleanExtra("MessageFragment", false);
        boolean messageGroup = intent.getBooleanExtra("GroupMessageFragment", false);


        if (message) {
            MessageFragment messageFragment = new MessageFragment();
            messageFragment.userId = intent.getStringExtra("userId");
            replaceFragment(getSupportFragmentManager(), R.id.home_activity_fl_id, messageFragment);
        } else if (messageGroup) {

        } else {
            replaceFragment(getSupportFragmentManager(), R.id.home_activity_fl_id, new continerFragment());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
