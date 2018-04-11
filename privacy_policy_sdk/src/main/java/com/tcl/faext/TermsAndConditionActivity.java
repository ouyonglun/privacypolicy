/* Copyright (C) 2017 Tcl Corporation Limited */
package com.tcl.faext;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * TermsAndCondition
 *
 * Created by shaohua.li on 3/21/16.
 */
public class TermsAndConditionActivity extends Activity {
    private ActionBar mActionBar;
    private TextView termsAndCondtionContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT<=19){
            setTheme(R.style.consentStyle);
        }
        mActionBar = getActionBar();
        setContentView(R.layout.disgnosticlib_activity_terms_and_conditions);
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.terms_and_condition);
        }
        termsAndCondtionContent = (TextView) findViewById(R.id.tv_terms_and_conditions);
        Resources res = getResources();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString title = new SpannableString(res.getString(R.string.terms_and_conditions_of_use));
        StyleSpan titleSpan = new StyleSpan(Typeface.BOLD);
        title.setSpan(titleSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(title);
        spannableStringBuilder.append("\n\n");
        spannableStringBuilder.append(res.getText(R.string.terms_and_conditions_of_use_content_one));
        spannableStringBuilder.append(res.getText(R.string.terms_and_conditions_of_use_content_two));
        spannableStringBuilder.append(res.getText(R.string.terms_and_conditions_of_use_content_three));
        spannableStringBuilder.append("\n\n");
        spannableStringBuilder.append(res.getString(R.string.more_info));
        SpannableString privacyPolicy = new SpannableString(res.getString(R.string.privacy_policy));
        privacyPolicy.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showPrivacyPolicy(TermsAndConditionActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //ds.setColor(getResources().getColor(R.color.text_color));
                ds.setUnderlineText(false);
            }
        }, 0, privacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(privacyPolicy);
        spannableStringBuilder.append(".");
        termsAndCondtionContent.setText(spannableStringBuilder);
        termsAndCondtionContent.setMovementMethod(LinkMovementMethod.getInstance());
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

    /**
     * 显示用户隐私
     *
     * @param context
     */
    public static void showPrivacyPolicy(Context context) {
        try {
            openGDPRPrivacyPolicy(context);
        } catch (Exception e) {
            Intent intent = new Intent(context, PrivacyPolicyActivity.class);
            context.startActivity(intent);
        }
    }

    private static void openGDPRPrivacyPolicy(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.tct.gdpr", "com.tct.gdpr.PrivacyPolicyActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
