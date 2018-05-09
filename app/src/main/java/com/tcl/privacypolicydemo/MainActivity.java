package com.tcl.privacypolicydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tcl.faext.OnFetchListener;
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
                PrivacyPolicySDK.getInstance().fetchDialogSwitch(MainActivity.this, "460", new OnFetchListener() {
                    @Override
                    public void onCompleted(boolean on) {
                        Log.i(TAG, "onCompleted: result = " + on);
                        if (on) {
                            PrivacyPolicySDK.getInstance().openPolicyDialog(MainActivity.this);
                        }
                    }
                });

            }
        });

    }
}
