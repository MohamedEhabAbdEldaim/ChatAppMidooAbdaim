package com.midooabdaim.midooabdaimchat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.ui.fragment.userCycle.LoginFragment;

import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.customToast;
import static com.midooabdaim.midooabdaimchat.helper.HelperMethod.replaceFragment;
import static com.midooabdaim.midooabdaimchat.helper.InternetState.isActive;

public class MainActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

// fragmentOrederTabLayoutId.setupWithViewPager(fragmentOrederViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in
            replaceFragment(getSupportFragmentManager(), R.id.main_activity_fl_id, new LoginFragment());
        } else {
            if (!isActive(this)) {
                customToast(this, getString(R.string.nointernet), true);
            }
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }


}
