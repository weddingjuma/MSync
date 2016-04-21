package com.yeleman.fondasms.service;

import android.app.IntentService;
import android.content.Intent;

import com.yeleman.fondasms.AmqpConsumer;
import com.yeleman.fondasms.App;

public class AmqpHeartbeatService extends IntentService {

    private App app;

    public AmqpHeartbeatService(String name)
    {
        super(name);
    }

    public AmqpHeartbeatService()
    {
        this("AmqpHeartbeatService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App)this.getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        AmqpConsumer consumer = app.getAmqpConsumer();
        consumer.sendHeartbeatBlocking();
    }
}
