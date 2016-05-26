package ftn.eventfinder.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
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

public class Splash extends AppCompatActivity {

    public static final int SPLASH_TIMEOUT = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Timer().schedule(new TimerTask() {
             @Override
             public void run() {
                 startActivity(new Intent(Splash.this, MainActivity.class));
                 finish();
                 }
             }, SPLASH_TIMEOUT);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
