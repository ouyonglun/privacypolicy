package com.tcl.faext;

import android.content.Context;
import android.content.Intent;

/**
 * 隐私政策和服务条款SDK
 * Created by lunou on 18-4-11.
 */

public class PrivacyPolicySDK {

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
     * @param context
     */
    public void openTermsAndCondition(Context context) {
        try {
            Intent intent = new Intent(context, TermsAndConditionActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
