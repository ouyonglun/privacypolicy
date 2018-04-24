package com.tcl.privacypolicydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tcl.faext.InformedConsentActivity;
import com.tcl.faext.PrivacyPolicyDialog;
import com.tcl.faext.PrivacyPolicySDK;

public class MainActivity extends AppCompatActivity {

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
                PrivacyPolicySDK.getInstance().openPolicyDialog(MainActivity.this);
            }
        });

    }
}
