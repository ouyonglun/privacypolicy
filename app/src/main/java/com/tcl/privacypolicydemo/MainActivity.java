package com.tcl.privacypolicydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tcl.faext.InformedConsentActivity;
import com.tcl.faext.PrivacyPolicySDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrivacyPolicySDK.getInstance().openAbout(this);
        finish();
    }
}
