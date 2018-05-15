package com.tcl.faext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.faext.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 隐私政策和服务条款SDK
 * Created by lunou on 18-4-11.
 */

public class PrivacyPolicySDK {

    private static final String TAG = "dan";
    public static final String[] MUTE_MCC_LIST = {"302", "310"};//302:加拿大；310：美国

    private static PrivacyPolicySDK sInstance;

    public static PrivacyPolicySDK getInstance() {
        if (sInstance == null) {
            sInstance = new PrivacyPolicySDK();
        }
        return sInstance;
    }

    private PrivacyPolicySDK() {
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
     * @param mcc
     */
    public void openPolicyDialog(final Activity activity, String mcc, final PrivacyPolicyDialog.OnPrivacyDialogClickListener listener) {
        PrivacyPolicySDK.getInstance().fetchDialogSwitch(activity, mcc, new OnFetchListener() {
            @Override
            public void onCompleted(boolean on) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onCompleted: [on]" + on);
                }
                if (on) {
                    if (activity != null && !activity.isFinishing()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new PrivacyPolicyDialog(activity, listener).show();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 判断是否弹框(firebase云控)
     *
     * @param activity
     * @param listener
     * @return
     */
    public void fetchDialogSwitch(final Activity activity, final String mcc, final OnFetchListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String value = NetworkUtils.requestDialogSwitch(activity);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "run: value = " + value);
                }
                String muteList = "";
                try {
                    JSONObject o = new JSONObject(value);
                    muteList = o.getString("muteList");
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "run: muteList = " + muteList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(muteList)) {
                    listener.onCompleted(!contains(mcc));
                } else {
                    listener.onCompleted(!containsNotEmpty(muteList, mcc));
                }
            }
        }).start();
    }

    private boolean containsNotEmpty(String muteList, String mcc) {
        if (TextUtils.isEmpty(mcc)) {
            return false;
        }
        return muteList.contains(mcc);
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
