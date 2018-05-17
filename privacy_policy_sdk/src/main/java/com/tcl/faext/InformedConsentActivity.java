package com.tcl.faext;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * 用户条款和隐私协议展示页
 * <p>
 * Created by shaohua.li on 12/7/15.
 */
public class InformedConsentActivity extends BaseActivity {
    private final String TAG = "InformedConsentActivity";
    private TextView informedDescription;
    //    private SwitchBar mSwitchBar;
    public final static String KEY_SWITHC_COLLECT = "hawkeye_enable";
    public final static String KEY_REFUSE_TIMES = "KEY_REFUSE_TIMES";
    public final static String KEY_LAST_TURN_OFF_TIME = "KEY_LAST_TURN_OFF_TIME";
    private int mDefaultColor = Color.parseColor("#04000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mDefaultColor = getIntent().getIntExtra("color", Color.parseColor("#04000000"));
        findViewById(R.id.ll_title).setBackgroundColor(mDefaultColor);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.about);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                PrivacyPolicySDK.getInstance().openTermsAndCondition(InformedConsentActivity.this, mDefaultColor);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#4FA2FE"));
            }
        }, 0, msg2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(msg2);
        spannableStringBuilder.append(res.getString(R.string.and));
        String privacyPolicy = res.getString(R.string.privacy_policy);
        SpannableString msg3 = new SpannableString(privacyPolicy);
        msg3.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicySDK.getInstance().openPrivacyPolicy(InformedConsentActivity.this, mDefaultColor);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#4FA2FE"));
            }
        }, 0, msg3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(msg3);
        spannableStringBuilder.append(".");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
