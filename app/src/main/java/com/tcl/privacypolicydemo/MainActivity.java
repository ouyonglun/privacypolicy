package com.tcl.privacypolicydemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.tcl.faext.PrivacyPolicySDK;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "dan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyPolicySDK.getInstance().openAbout(MainActivity.this);
            }
        });

        findViewById(R.id.dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imsi = getSubscriberId(MainActivity.this);
                String mcc = "";
                if (imsi != null && imsi.length() > 3) {
                    mcc = imsi.substring(0, 3);
                }
                Log.i(TAG, "onClick: mcc = " + mcc);
                PrivacyPolicySDK.getInstance().openPolicyDialog(MainActivity.this, mcc);
            }
        });

    }

    public static String getSubscriberId(Context context) {
        String id = null;
        boolean ignore = false;

        int perms = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE);
        if (perms != PackageManager.PERMISSION_GRANTED) {
            ignore = true;
        }

        if (!ignore) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            id = tm.getSubscriberId();
        }
        return id;
    }
}
