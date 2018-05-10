package com.tcl.faext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * 隐私政策和服务条款SDK
 * Created by lunou on 18-4-11.
 */

public class PrivacyPolicySDK {

    private static final String TAG = "dan";
    public static final String[] MUTE_MCC_LIST = {"302", "310"};//302:加拿大；310：美国

    private static PrivacyPolicySDK sInstance;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static PrivacyPolicySDK getInstance() {
        if (sInstance == null) {
            sInstance = new PrivacyPolicySDK();
        }
        return sInstance;
    }

    private PrivacyPolicySDK() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(false)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    /**
     * 打开关于界面
     *
     * @param context
     */
    public void openAbout(Context context) {
        Intent intent = new Intent(context, InformedConsentActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开隐私政策
     */
    public void openPrivacyPolicy(Context context) {
        TermsAndConditionActivity.showPrivacyPolicy(context);
    }

    /**
     * 打开服务条款
     *
     * @param context
     */
    public void openTermsAndCondition(Context context) {
        try {
//            Intent intent = new Intent(context, TermsAndConditionActivity.class);
            Intent intent = new Intent(context, TermsActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     * 打开隐私政策弹窗
     *
     * @param activity
     */
    public void openPolicyDialog(Activity activity) {
        new PrivacyPolicyDialog(activity).show();
    }

    /**
     * 判断是否弹框(firebase云控)
     *
     * @param activity
     * @param listener
     * @return
     */
    public void fetchDialogSwitch(Activity activity, final String mcc, final OnFetchListener listener) {
        long cacheExpiration = 60;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                            boolean r = mFirebaseRemoteConfig.getBoolean("shouldOpen");
                            Log.i(TAG, "onComplete: successful = " + r);
                            listener.onCompleted(r);
                        } else {
                            boolean r = mFirebaseRemoteConfig.getBoolean("shouldOpen");
                            Log.i(TAG, "onComplete: failed = " + r);
                            if (contains(mcc)) {
                                listener.onCompleted(false);
                            } else {
                                listener.onCompleted(true);
                            }
                        }
                    }
                });
    }

    private boolean contains(String mcc) {
        for (String s : MUTE_MCC_LIST) {
            if (s.equals(mcc)) {
                return true;
            }
        }
        return false;
    }
}
