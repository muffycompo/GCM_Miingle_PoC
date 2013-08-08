package com.maomuffy.gcmpoc;

import android.content.Context;
import android.content.Intent;

public final class GCMPoCUtilities {
	
    static final String DISCUSSION_SERVER_URL = "http://10.0.2.2/gcm_poc/public/discussions"; 
    static final String SENDER_ID = "842566924491"; 
    static final String TAG = "GCM PoC";
    static final String DISPLAY_MESSAGE_ACTION = "com.maomuffy.gcmpoc.DISPLAY_MESSAGE";
    static final String EXTRA_MESSAGE = "message";
    static final String SHAREDPREF_LOCATION = "com.maomuffy.gcmpoc.pref";

    
    static void showMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
