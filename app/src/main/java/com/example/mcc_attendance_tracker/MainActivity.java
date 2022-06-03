package com.example.mcc_attendance_tracker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    BottomAppBar bottomAppBar;
    Menu navMenu;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);
        progressDialog = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        if(calendar.getTime().compareTo(new Date()) < 0){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(MainActivity.this, TimeAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        if(alarmManager!=null){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }





        bottomNavigationView = findViewById(R.id.navbar_bottom_navigation_view);
        bottomAppBar = findViewById(R.id.navbar_bottom_appbar_layout);

        fab = findViewById(R.id.navbar_fab);


        fab.setVisibility(View.GONE);
        bottomNavigationView.getMenu().findItem(R.id.nav_menu_placeholder).setVisible(false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(navigationSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.navbar_fragment_container, new Dashboard(MainActivity.this, progressDialog)).commit();
    }

    private BottomNavigationView.OnItemSelectedListener navigationSelectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_menu_dashboard:
                    selectedFragment = new Dashboard(MainActivity.this, progressDialog);
                    bottomNavigationView.getMenu().findItem(R.id.nav_menu_placeholder).setVisible(false);
                    fab.setVisibility(View.GONE);
                    bottomAppBar.setFabCradleMargin(0);
                    bottomAppBar.setFabCradleRoundedCornerRadius(0);
                    bottomAppBar.setCradleVerticalOffset(0);




                    break;
                case R.id.nav_menu_profile:
                    selectedFragment = new ViewProfile(progressDialog);
                    bottomNavigationView.getMenu().findItem(R.id.nav_menu_placeholder).setVisible(false);
                    fab.setVisibility(View.GONE);
                    bottomAppBar.setFabCradleMargin(0);
                    bottomAppBar.setFabCradleRoundedCornerRadius(0);
                    bottomAppBar.setCradleVerticalOffset(0);

                    break;
                case R.id.nav_menu_project:
                    selectedFragment = new TeamAndProjects(progressDialog);
                    bottomNavigationView.getMenu().findItem(R.id.nav_menu_placeholder).setVisible(false);
                    fab.setVisibility(View.GONE);
                    bottomAppBar.setFabCradleMargin(0);
                    bottomAppBar.setFabCradleRoundedCornerRadius(0);
                    bottomAppBar.setCradleVerticalOffset(0);

                    break;
                case R.id.nav_menu_report:
                    selectedFragment = new ReportPage(progressDialog);
                    bottomNavigationView.getMenu().findItem(R.id.nav_menu_placeholder).setVisible(true);
                    bottomNavigationView.getMenu().findItem(R.id.nav_menu_placeholder).setEnabled(false);
                    fab.setVisibility(View.VISIBLE);
                    bottomAppBar.setFabCradleMargin(20f);
                    bottomAppBar.setFabCradleRoundedCornerRadius(20f);
                    bottomAppBar.setCradleVerticalOffset(20f);

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(MainActivity.this, SubmitReport.class);
                            startActivity(intent);
                        }
                    });
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.navbar_fragment_container, selectedFragment).commit();
            return true;
        }
    };

}
