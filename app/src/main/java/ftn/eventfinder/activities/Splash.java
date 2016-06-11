package ftn.eventfinder.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

import ftn.eventfinder.MainActivity;
import ftn.eventfinder.R;
import ftn.eventfinder.sync.ClearDbService;

public class Splash extends AppCompatActivity {

    public static final int SPLASH_TIMEOUT = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startService(new Intent(this, ClearDbService.class));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
       // Boolean splash = ;
        if(sharedPreferences.getBoolean("pref_splash", true)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
                }
            }, SPLASH_TIMEOUT);
        }else{
            startActivity(new Intent(Splash.this, MainActivity.class));
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
