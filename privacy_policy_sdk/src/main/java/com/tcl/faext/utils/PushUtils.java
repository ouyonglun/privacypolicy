package com.tcl.faext.utils;

import android.webkit.WebView;

/**
 * the utils of getting network status
 * Created by haidong.zhuo on 7/2/17.
 */
public class PushUtils {


    public static void removeUnsafeJs(WebView webView) {

        try {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            webView.removeJavascriptInterface("accessibility");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            webView.removeJavascriptInterface("accessibilityaversal");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
