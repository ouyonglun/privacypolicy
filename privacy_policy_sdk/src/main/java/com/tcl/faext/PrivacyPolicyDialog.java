package com.tcl.faext;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lunou on 18-4-19.
 */

public class PrivacyPolicyDialog extends Dialog {

    public PrivacyPolicyDialog(final Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.privacy_proicy_dialog_layout);

        TextView titleView = (TextView) findViewById(R.id.dialog_title);
        TextView contentView = (TextView) findViewById(R.id.dialog_content);
        titleView.setText(context.getString(R.string.privacy_policy_dialog_informed_title));

        Resources res = context.getResources();
        String privacyText = res.getString(R.string.privacy);
        String serviceText = res.getString(R.string.service_terms);
        String contentText = res.getString(R.string.privacy_policy_dialog_informed_content);
        String contentStr = String.format(contentText, privacyText, serviceText);
        String[] split = contentStr.split(privacyText, 2);
        String msg1 = split[0];
        String msg2 = split[1];
        split = msg2.split(serviceText, 2);
        msg2 = split[0];
        String msg3 = split[1];
        final int linkTextColor = res.getColor(R.color.color_privacy_dialog_ok);
        SpannableString spanPrivacy = new SpannableString(privacyText);
        spanPrivacy.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicySDK.getInstance().openTermsAndCondition(context);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(linkTextColor);
            }
        }, 0, spanPrivacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spanService = new SpannableString(serviceText);
        spanService.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicySDK.getInstance().openPrivacyPolicy(context);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(linkTextColor);
            }
        }, 0, spanService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(msg1);
        spannableStringBuilder.append(spanPrivacy);
        spannableStringBuilder.append(msg2);
        spannableStringBuilder.append(spanService);
        spannableStringBuilder.append(msg3);
        contentView.setText(spannableStringBuilder);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
}
