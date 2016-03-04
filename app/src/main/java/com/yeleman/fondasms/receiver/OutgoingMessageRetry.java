
package com.yeleman.fondasms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yeleman.fondasms.App;
import com.yeleman.fondasms.OutgoingMessage;

public class OutgoingMessageRetry extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        App app = (App) context.getApplicationContext();
        if (!app.isEnabled())
        {
            return;
        }

        OutgoingMessage message = app.outbox.getMessage(intent.getData());
        if (message == null)
        {
            return;
        }

        app.outbox.enqueueMessage(message);
    }
}
