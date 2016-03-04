package com.yeleman.fondasms.ui;

import com.yeleman.fondasms.ui.Prefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yeleman.fondasms.App;
import com.yeleman.fondasms.ui.LogView;

public class Main extends Activity {
    private App app;

    public final String TAG = String.format(app.TAG, "Main");

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (App)getApplication();
        Log.d(TAG, "onCreate");
        startActivity(new Intent(this, LogView.class));
    }
}
