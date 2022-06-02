package com.example.mcc_attendance_tracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(sharedPreferences.getBoolean("isLoggedIn", false)){
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(SplashScreen.this, LoginPage.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 3000);
    }
}
