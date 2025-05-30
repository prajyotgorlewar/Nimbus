package com.example.nimbus;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

import java.util.List;


public class ZegoCallManager {
    private static boolean initialized = false;

    public static boolean isInitialized() {
        return initialized;
    }

    public static void initialize(Context context, long appId, String appSign, String userId, String userName) {
        if (!initialized) {
            ZegoUIKitPrebuiltCallInvitationConfig config = new ZegoUIKitPrebuiltCallInvitationConfig();
            ZegoUIKitPrebuiltCallService.init(
                    (Application) context.getApplicationContext(),
                    appId,
                    appSign,
                    userId,
                    userName,
                    config
            );
            initialized = true;
            Log.d("Zego", "Zego login complete. Ready to send invites.");

        }
    }

    public static void requestOverlayPermission(Activity activity) {
        PermissionX.init((FragmentActivity) activity)
                .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly.";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                         @NonNull List<String> deniedList) {
                        // Optional: Handle permission result
                    }
                });
    }

    public static void uninitialize() {
        if (initialized) {
            ZegoUIKitPrebuiltCallService.unInit();
            initialized = false;
        }
    }
}
