package com.tcl.faext;

/**
 * Created by hui.zhu on 2017/12/14.
 */

public interface StatEvent {
    String CONSENT_PAGE_OPEN = "consent_page_open";
    String CONSENT_PAGE_ACCEPTED = "consent_page_accepted";
    String CONSENT_PAGE_REFUSED = "consent_page_refused";

    interface UserProperty {
        String LANGUAGE = "language";
        String CHANNEL = "channel";
        String SDK_VERSION = "sdk_version";
    }

}
