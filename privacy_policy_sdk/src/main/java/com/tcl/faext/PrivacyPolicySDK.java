package com.tcl.faext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
    private static final String DEFAULT_MCC_WHITE_LIST = "208,262,222,204,206,270,234,235,238,272,202,214,268,232,240,244,280,216,230,248,247,246,278,260,231,293,284,226";//欧盟国家，白名单

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
     * @param activity 启动弹窗的activity
     * @param listener 弹窗点击事件监听
     */
    public void openPolicyDialog(final Activity activity, final PrivacyPolicyDialog.OnPrivacyDialogClickListener listener) {
        if (activity != null && !activity.isFinishing()) {
            new PrivacyPolicyDialog(activity, listener).show();
        }
    }

    /**
     * 判断是否弹框
     *
     * @param ctx
     * @param mcc
     * @param listener
     * @return
     */
    public void fetchDialogSwitch(final Context ctx, final String mcc, final OnFetchListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String value = NetworkUtils.requestDialogSwitch(ctx);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "run: value = " + value);
                }
                Handler handler = new Handler(ctx.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String whiteList = "";
                        if (value != null) {
                            try {
                                JSONObject o = new JSONObject(value);
                                whiteList = o.getString("whiteList");
                                if (BuildConfig.DEBUG) {
                                    Log.i(TAG, "run: whiteList = " + whiteList);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (TextUtils.isEmpty(whiteList)) {
                            listener.onCompleted(DEFAULT_MCC_WHITE_LIST.contains(mcc));
                        } else {
                            listener.onCompleted(whiteList.contains(mcc));
                        }
                    }
                });
            }
        }).start();
    }
}
