package com.tcl.faext.net;

import com.tcl.faext.BuildConfig;

/**
 * A constant class to all http api.
 * <p>
 * Created by zhigang.zeng on 12/8/16.
 */
public class HttpApi {

    static {
        if (BuildConfig.DEBUG) {
            DOMAIN_APP_SERVER = "http://pushplatform-test.tclclouds.com";
        } else {
            DOMAIN_APP_SERVER = "http://pushplatform.tclclouds.com";
        }
    }

    public static final String DOMAIN_APP_SERVER;
    public static final String PATH_FCM_TOKEN_REGISTER_V1 = "/api/v1/push/registration/savenew";
    public static final String PATH_FIREBASE_CONFIG_V1 = "/api/v1/app/config";
    public static final String PATH_AGREEMENT_URL_V1 = "/api/v1/app/privacypolicy";
    public static final String PATH_ALL_URL_V1_TEST = "http://platform-test.tclclouds.com/api/v1/config/keys";
    public static final String PATH_ALL_URL_V1_FORMAL = "http://platform.tclclouds.com/api/v1/config/keys";
    public static final String PATH_ALL_URL_V1 = BuildConfig.DEBUG ? PATH_ALL_URL_V1_TEST : PATH_ALL_URL_V1_FORMAL;
}
