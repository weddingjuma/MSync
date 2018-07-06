
package com.mkodiapp.msync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mkodiapp.msync.App;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final App app = (App) context.getApplicationContext();

        if (!app.isEnabled())
        {
            return;
        }

        app.onConnectivityChanged();
    }
}
