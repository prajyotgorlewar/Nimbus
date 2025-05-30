package com.example.nimbus;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        if (isCallInvitation(data)) {

            SharedPreferences prefs = getSharedPreferences("ZegoPrefs", MODE_PRIVATE);
            String userId = prefs.getString("userId", null);
            String userName = prefs.getString("userName", null);

            if (userId != null && userName != null) {
                long appId = Long.parseLong(getString(R.string.app_id));
                String appSign = getString(R.string.app_sign);
                ZegoCallManager.initialize(this, appId, appSign, userId, userName);
            }


            Intent intent = new Intent(this, calling.class);
            intent.putExtras(toBundle(data));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "your_call_channel_id")
                            .setSmallIcon(R.drawable.call)
                            .setContentTitle("Incoming call")
                            .setContentText("You have an incoming call")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(fullScreenPendingIntent, true)
                            .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(12345, notificationBuilder.build());
            }
        }
    }

    private boolean isCallInvitation(Map<String, String> data) {
        return data.containsKey("call_type") && "incoming".equals(data.get("call_type"));
    }

    private Bundle toBundle(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "Token: " + token);
        // Send this token to your server if needed
    }
}

