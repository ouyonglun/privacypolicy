package com.tcl.faext;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.tcl.faext.utils.AppUtils;

/**
 * Created by lunou on 18-4-19.
 */

public class PrivacyPolicyDialog extends Dialog {
    private static final String TAG = "dan";

    public PrivacyPolicyDialog(final Context context, final OnPrivacyDialogClickListener listener) {
        super(context, R.style.toast_dialog);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.privacy_proicy_dialog_layout);

        TextView titleView = (TextView) findViewById(R.id.dialog_title);
        TextView contentView = (TextView) findViewById(R.id.dialog_content);
//        titleView.setText(Html.fromHtml(context.getString(R.string.privacy_policy_0_1)));
        titleView.setText(AppUtils.getAppName(context));

        init2(context, contentView);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmed();
                }
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onCancelled();
                }
            }
        });

        this.setCancelable(false);

//        this.setOnCancelListener(new OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                if (listener != null) {
//                    listener.onConfirmed();
//                }
//            }
//        });
    }

    private void init(final Context context, TextView contentView) {
        Resources res = context.getResources();
        String privacyText = res.getString(R.string.privacy);
//        String serviceText = res.getString(R.string.service_terms);
        String contentText = res.getString(R.string.content_privacy_dialog);
        String contentStr = String.format(contentText, privacyText);
        String[] split = contentStr.split(privacyText, 2);
        String msg1 = split[0];
        String msg2 = split[1];
//        split = msg2.split(serviceText, 2);
//        msg2 = split[0];
//        String msg3 = split[1];
        final int linkTextColor = res.getColor(R.color.color_privacy_dialog_ok);
        SpannableString spanPrivacy = new SpannableString(privacyText);
        spanPrivacy.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicySDK.getInstance().openPrivacyPolicy(context);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(linkTextColor);
            }
        }, 0, spanPrivacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        SpannableString spanService = new SpannableString(serviceText);
//        spanService.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                PrivacyPolicySDK.getInstance().openPrivacyPolicy(context);
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(false);
//                ds.setColor(linkTextColor);
//            }
//        }, 0, spanService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(msg1);
        spannableStringBuilder.append(spanPrivacy);
        spannableStringBuilder.append(msg2);
//        spannableStringBuilder.append(spanService);
//        spannableStringBuilder.append(msg3);
        contentView.setText(spannableStringBuilder);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void init2(final Context context, TextView contentView) {
        Resources res = context.getResources();
        String str1 = res.getString(R.string.privacy_policy_0_1);
        String str2 = res.getString(R.string.privacy_policy_0_2);
        String str3 = res.getString(R.string.privacy_policy_0_3);
        String str4 = res.getString(R.string.privacy_policy_1_1);
        String str5 = res.getString(R.string.privacy_policy_2_1);
        String str6 = res.getString(R.string.privacy_policy_3_1);
        String str7 = res.getString(R.string.privacy_policy_4_1);
        String str8 = res.getString(R.string.privacy_policy_4_2);
        String str9 = res.getString(R.string.privacy_policy_5_1);
        String str10 = res.getString(R.string.privacy_policy_6_1);
        String str11 = res.getString(R.string.privacy_policy_7_1);
        String str12 = res.getString(R.string.privacy_policy_8_1);
        String str13 = res.getString(R.string.privacy_policy_9_1);
        String str14 = res.getString(R.string.privacy_policy_10_1);
        String str15 = res.getString(R.string.privacy_policy_10_2);
        String str16 = res.getString(R.string.privacy_policy_10_3);
        String str17 = res.getString(R.string.privacy_policy_10_4);
        String str18 = res.getString(R.string.privacy_policy_10_5);
        String str19 = res.getString(R.string.privacy_policy_11_1);
        String str20 = res.getString(R.string.privacy_policy_12_1);
        String str21 = res.getString(R.string.privacy_policy_13_1);
        String str22 = res.getString(R.string.privacy_policy_13_2);
        String str23 = res.getString(R.string.privacy_policy_13_3);
        Spanned spanned = Html.fromHtml(str1 + str2 + str3 + str4 + str5 + str6 + str7 +
                str8 + str9 + str10 + str11 + str12 + str13 + str14 + str15 +
                str16 + str17 + str18 + str19 + str20 + str21 + str22 + str23);
        contentView.setText(spanned);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public interface OnPrivacyDialogClickListener {
        void onConfirmed();

        void onCancelled();
    }
}
