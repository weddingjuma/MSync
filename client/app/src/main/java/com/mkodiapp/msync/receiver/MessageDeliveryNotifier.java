package com.mkodiapp.msync.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.mkodiapp.msync.App;

public class MessageDeliveryNotifier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        App app = (App) context.getApplicationContext();
        Uri uri = intent.getData();

        Bundle extras = intent.getExtras();
        int index = extras.getInt(App.STATUS_EXTRA_INDEX);
        int numParts = extras.getInt(App.STATUS_EXTRA_NUM_PARTS);
        String serverID = extras.getString(App.STATUS_EXTRA_SERVER_ID);

        app.log("Message " + serverID + " //" + uri + " part "+index + "/" + numParts + " delivered");

        // OutgoingMessage sms = app.outbox.getMessage(uri);

        // if (sms == null) {
        //     return;
        // }

        // if (index != 0)
        // {
        //     // TODO: process message status for parts other than the first one
        //     return;
        // }

        // int resultCode = getResultCode();

        // if (resultCode == Activity.RESULT_OK)
        // {
        //     app.outbox.messageSent(sms);
        // }
        // else
        // {
        //     app.outbox.messageFailed(sms, getErrorMessage(resultCode));
        // }

    }
}
