package com.deadangroup.msync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.deadangroup.msync.App;

public class DequeueOutgoingMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        App app = (App) context.getApplicationContext();

        if (!app.isEnabled())
        {
            return;
        }

        app.outbox.maybeDequeueMessage();
    }
}
