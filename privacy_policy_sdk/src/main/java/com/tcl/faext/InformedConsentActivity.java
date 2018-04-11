package com.tcl.faext;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 用户条款和隐私协议展示页
 *
 * Created by shaohua.li on 12/7/15.
 */
public class InformedConsentActivity extends Activity{
    private final String TAG = "InformedConsentActivity";
    private ActionBar mActionBar;
    private TextView informedDescription;
//    private SwitchBar mSwitchBar;
    public final static String KEY_SWITHC_COLLECT = "hawkeye_enable";
    public final static String KEY_REFUSE_TIMES = "KEY_REFUSE_TIMES";
    public final static String KEY_LAST_TURN_OFF_TIME = "KEY_LAST_TURN_OFF_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.parseColor("#6A000000"));
        decorViewGroup.addView(statusBarView);
        initGobalView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        boolean collectDataOn = FAExt.getFACollectionEnabled(this);
//        mSwitchBar.setChecked(collectDataOn);
    }

    private void initGobalView() {
        setContentView(R.layout.disgnosticlib_activity_second);
        mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.about);
        }

//        TextView tvContentTitle = (TextView) findViewById(R.id.informed_consent_title);
//        tvContentTitle.setText(FAExt.getContentMessage(this));

        informedDescription = (TextView) findViewById(R.id.informed_consent_description);
        Resources res = this.getResources();
        String msg1 = res.getString(R.string.informed_content_new) + " ";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(msg1);
        String termsAndCondition = res.getString(R.string.terms_and_condition);

        SpannableString msg2 = new SpannableString(termsAndCondition);
        msg2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicySDK.getInstance().openTermsAndCondition(InformedConsentActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 0, msg2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(msg2);
        spannableStringBuilder.append(res.getString(R.string.and));
        String privacyPolicy = res.getString(R.string.privacy_policy);
        SpannableString msg3 = new SpannableString(privacyPolicy);
        msg3.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicySDK.getInstance().openPrivacyPolicy(InformedConsentActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 0, msg3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(msg3);
        informedDescription.setText(spannableStringBuilder);
        informedDescription.setMovementMethod(LinkMovementMethod.getInstance());
//        mSwitchBar = (SwitchBar) findViewById(R.id.switch_bar);
//        if (Build.VERSION.SDK_INT >= 21) {
//            mSwitchBar.setBackgroundResource(R.drawable.disgnosticlib_switchbar_background);
//        }
//        mSwitchBar.show();
//        mSwitchBar.addOnSwitchChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onSwitchChanged(Switch switchView, boolean isChecked) {
//        FAExt.setFACollectionEnabled(this, isChecked);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if (isChecked) {
//            FAExt.logEventForConsentPage(StatEvent.CONSENT_PAGE_ACCEPTED);
//            sharedPreferences.edit().putInt(FAExt.KEY_REFUSE_TIMES, 0).apply();
//        } else {
//            sharedPreferences.edit().putLong(FAExt.KEY_LAST_TURN_OFF_TIME, System.currentTimeMillis()).apply();
//            FAExt.logEventForConsentPage(StatEvent.CONSENT_PAGE_REFUSED);
//        }
//    }

    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkCollectOn(this);
    }

    /**
     * @hide
     */
    public static void checkCollectOn(Context context) {
        if (BuildConfig.ENABLE_CU || "com.jrdcom.elabel.old".equals(context.getPackageName()) || "com.jrdcom.Elabel".equals(context.getPackageName()) || !isCollectChecked(context)) { // MODIFIED by rongjun.zheng, 2017-05-25,BUG-4869550,4869355
            return;
        }
        boolean collectDataOn = getFACollectionEnabled(context);
        if (collectDataOn) {
            return;
        }
        long now = System.currentTimeMillis();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int refuseTime = sharedPreferences.getInt(KEY_REFUSE_TIMES, -1);
        Log.d("FAExt", "checkCollectOn==================refuseTime=============" + refuseTime);
        if (refuseTime > 2) {
            return;
        }
        long lastDay = sharedPreferences.getLong(KEY_LAST_TURN_OFF_TIME, now);
        if (lastDay == now) {
            sharedPreferences.edit().putLong(KEY_LAST_TURN_OFF_TIME, now).apply();
            //return ;
        }
        long day = (now - lastDay) / 24 / 3600 / 1000;
        Log.d("FAExt", "checkCollectOn==================day=============" + day);
        if (refuseTime == -1 || refuseTime == 0 && day >= 14
                || refuseTime == 1 && day >= 30
                || refuseTime == 2 && day >= 90) {
//            showConsentDialog(context, false);
            sharedPreferences.edit().putInt(KEY_REFUSE_TIMES, refuseTime + 1).apply();
            sharedPreferences.edit().putLong(KEY_LAST_TURN_OFF_TIME, now).apply();
        }
        //        showConsentDialog(context, false);
    }

    public static boolean getFACollectionEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_SWITHC_COLLECT, context.getResources().getBoolean(R.bool.def_fa_collect_on));
    }

    private static boolean isCollectChecked(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean checked = sharedPreferences.getBoolean("checkCollect", false);
        return checked;
    }

}
