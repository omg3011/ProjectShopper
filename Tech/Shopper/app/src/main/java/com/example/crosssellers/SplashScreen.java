package com.example.crosssellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    //-- Cache Reference(s)
    private ImageView IV_logo;
    private TextView TV_name;

    //-- Timer for splash screen total duration before going to next Activity
    private static int splashTimeOut = 4000;

    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //-- Init Views
        IV_logo = (ImageView) findViewById(R.id.ss_icon_IV);
        TV_name = (TextView) findViewById(R.id.ss_name_TV);

        //-- Setup timer for splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, SelectMallActivity.class);
                startActivity(i);
                finish();
            }
        }, splashTimeOut);

        //-- Setup Animation
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.splashscreen_anim);
        IV_logo.startAnimation(myanim);
        TV_name.startAnimation(myanim);
    }
}
