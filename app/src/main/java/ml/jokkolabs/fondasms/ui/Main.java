package ml.jokkolabs.fondasms.ui;

import ml.jokkolabs.fondasms.ui.Prefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ml.jokkolabs.fondasms.App;
import ml.jokkolabs.fondasms.ui.LogView;

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