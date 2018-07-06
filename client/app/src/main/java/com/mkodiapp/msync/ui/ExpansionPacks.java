package com.mkodiapp.msync.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.mkodiapp.msync.App;
import com.mkodiapp.msync.R;

public class ExpansionPacks extends PreferenceActivity  {

    private App app;

    private BroadcastReceiver installReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateInstallStatus();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.expansion_packs);

        app = (App) getApplication();

        IntentFilter installReceiverFilter = new IntentFilter();
        installReceiverFilter.addAction(App.EXPANSION_PACKS_CHANGED_INTENT);
        registerReceiver(installReceiver, installReceiverFilter);

        updateInstallStatus();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(installReceiver);

        super.onDestroy();
    }

    public void updateInstallStatus()
    {
        PreferenceScreen screen = this.getPreferenceScreen();
        int numPrefs = screen.getPreferenceCount();

        String basePackageName = app.getPackageName();

        this.setTitle(getText(R.string.expansion_packs_title)+" ("+app.getOutgoingMessageLimit()+")");

        for(int i=0; i < numPrefs;i++)
        {
            Preference p = screen.getPreference(i);

            String packageNum = p.getKey();

            String packageName = basePackageName + "." + packageNum;

            if (app.isSmsExpansionPackInstalled(packageName))
            {
                p.setSummary("Installed.");
            }
            else
            {
                p.setSummary("Not installed.\nInstall to increase limit by 30 SMS/30mn...");
            }
        }
    }
}