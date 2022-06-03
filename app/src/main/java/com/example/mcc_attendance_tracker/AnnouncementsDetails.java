package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnnouncementsDetails extends AppCompatActivity {
    TextView backbtn, title, details, link, meetingDate, meetingTime, speaker, regFee, linktext;
    LinearLayout linkLayout;
    Button joinWebinar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.show();



        backbtn = findViewById(R.id.backbtn);
        title = findViewById(R.id.webinarTitle);
        details = findViewById(R.id.webinarDetails);
        link = findViewById(R.id.webinarLink);
        meetingDate = findViewById(R.id.webinarDate);
        meetingTime = findViewById(R.id.webinarTime);
        speaker = findViewById(R.id.webinarSpeaker);
        regFee = findViewById(R.id.webinarFee);
        linkLayout = findViewById(R.id.linkLayout);
        joinWebinar = findViewById(R.id.joinWebinar);
        linktext = findViewById(R.id.linkText);


        Intent i = getIntent();
        boolean hasParticipated = i.getBooleanExtra("hasParticipated", false);
        String wtitle = i.getStringExtra("title");
        String wdate = i.getStringExtra("meeting_date");
        String wtime = i.getStringExtra("meeting_time");
        String wdetails = i.getStringExtra("details");
        String wspeaker = i.getStringExtra("speaker");
        String wstatus = i.getStringExtra("status");
        String wlink = i.getStringExtra("meeting_link");
        String wfee = i.getStringExtra("register_fee");
        int webID = i.getIntExtra("webinarID", 0);

        title.setText(wtitle);
        meetingDate.setText(wdate);
        meetingTime.setText(wtime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            details.setText(Html.fromHtml(wdetails, Html.FROM_HTML_MODE_COMPACT));
        } else {
            details.setText(Html.fromHtml(wdetails));
        }
        speaker.setText(wspeaker);
        link.setText(wlink);
        regFee.setText(wfee);

        if(wstatus.equalsIgnoreCase("open")){
            linkLayout.setVisibility(View.VISIBLE);
        }else{
            linkLayout.setVisibility(View.GONE);
        }

        if(hasParticipated){
            joinWebinar.setText("Already Registered");
            joinWebinar.setEnabled(false);
            link.setVisibility(View.VISIBLE);
            linktext.setText("Here is the link for the webinar:");
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        joinWebinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDialog paymentDialog = new PaymentDialog(AnnouncementsDetails.this, webID, joinWebinar, link, linktext);
                paymentDialog.show();
            }
        });

    }


}
