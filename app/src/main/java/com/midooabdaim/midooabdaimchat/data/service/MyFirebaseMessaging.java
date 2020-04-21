package com.midooabdaim.midooabdaimchat.data.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.data.model.Token;
import com.midooabdaim.midooabdaimchat.ui.activity.HomeActivity;

import java.util.Date;

import static com.midooabdaim.midooabdaimchat.helper.Constant.Tokens;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert sented != null;

        if(firebaseUser != null && sented.equals(firebaseUser.getUid())){

            sendNotification(remoteMessage);

        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");

        String body = remoteMessage.getData().get("body");

        String icon = remoteMessage.getData().get("icon");

        String title = remoteMessage.getData().get("title");

        String group = remoteMessage.getData().get("group");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));


        Intent intent = new Intent(this, HomeActivity.class);


        Bundle bundle;

        if (group != null) {

            bundle = new Bundle();
            bundle.putBoolean("GroupMessageFragment", true);
            bundle.putString("groupId", group);

        } else {

            bundle = new Bundle();
            bundle.putBoolean("MessageFragment", true);
            bundle.putString("userId", user);

        }


        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(Integer.parseInt(icon))
//                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
//                        R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), "hoba", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(defaultSound, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            noti.createNotificationChannel(channel);

        }


        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        noti.notify(m, builder.build());


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            UpdateToken(s);
        }
    }

    private void UpdateToken(String tokenRefresh) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Tokens);

        Token token = new Token(tokenRefresh);

        ref.child(user.getUid()).setValue(token);

    }

}
