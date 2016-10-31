package com.hammersmith.tinhluoklan;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by NgocTri on 4/9/2016.
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService {
    /**
     * When token refresh, start service to get new token
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startService(intent);
    }
}
