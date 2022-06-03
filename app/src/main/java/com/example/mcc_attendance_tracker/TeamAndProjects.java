package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TeamAndProjects extends Fragment implements View.OnTouchListener{
    LinearLayout myprojectLayout, teamprojectLayout, weeklyreportLayout, universityLayout, teammonitoringLayout;
    TextView myprojectTitle, teamprojectTitle, weeklyreportTitle, universityTitle, teammonitoringTitle;
    TextView myprojectImage, teamprojectImage, weeklyreportImage, universityImage, teammonitoringImage;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;

    public TeamAndProjects(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    int position;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        sharedPreferences = this.getActivity().getSharedPreferences("UserDataSharedPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        position = Integer.valueOf(sharedPreferences.getString("position", ""));


        myprojectLayout = view.findViewById(R.id.myProjectsLayout);
        teammonitoringLayout = view.findViewById(R.id.teamMonitoringLayout);
        teamprojectLayout = view.findViewById(R.id.teamProjectsLayout);
        weeklyreportLayout = view.findViewById(R.id.weeklyReportLayout);
        universityLayout = view.findViewById(R.id.universityDocumentsLayout);

        myprojectTitle = view.findViewById(R.id.myProjectsTitle);
        teamprojectTitle = view.findViewById(R.id.teamProjectsTitle);
        weeklyreportTitle = view.findViewById(R.id.weeklyReportTitle);
        universityTitle = view.findViewById(R.id.universityDocumentsTitle);
        teammonitoringTitle = view.findViewById(R.id.teamMonitoringTitle);

        myprojectImage = view.findViewById(R.id.myProjectsImage);
        teamprojectImage = view.findViewById(R.id.teamProjectsImage);
        weeklyreportImage = view.findViewById(R.id.weeklyReportImage);
        universityImage = view.findViewById(R.id.universityDocumentsImage);
        teammonitoringImage = view.findViewById(R.id.teamMonitoringImage);

        myprojectLayout.setOnTouchListener(this);
        teammonitoringLayout.setOnTouchListener(this);
        weeklyreportLayout.setOnTouchListener(this);
        teamprojectLayout.setOnTouchListener(this);
        universityLayout.setOnTouchListener(this);

        if(!(position == 1 || position == 2)){
            teamprojectLayout.setVisibility(View.GONE);
            weeklyreportLayout.setVisibility(View.GONE);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);


        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(v==myprojectLayout || v==myprojectImage || v==myprojectTitle){
                myprojectImage.setTextColor(getActivity().getResources().getColor(R.color.blue));
                myprojectTitle.setTextColor(getActivity().getResources().getColor(R.color.blue));
                Intent intent = new Intent(getActivity(), ProjectStatus.class);
                startActivity(intent);
            }

            if(v==teamprojectLayout || v==teamprojectImage || v== teamprojectTitle){
                teamprojectImage.setTextColor(getActivity().getResources().getColor(R.color.blue));
                teamprojectTitle.setTextColor(getActivity().getResources().getColor(R.color.blue));

                Format f = new SimpleDateFormat("EEEE");
                String day = f.format(new Date());

                if(!day.equalsIgnoreCase("friday")){
                    Toast.makeText(getContext(), "You can only submit team projects during Friday.", Toast.LENGTH_LONG).show();
                }
            }

            if(v==weeklyreportLayout || v==weeklyreportImage || v==weeklyreportTitle){
                weeklyreportImage.setTextColor(getActivity().getResources().getColor(R.color.blue));
                weeklyreportTitle.setTextColor(getActivity().getResources().getColor(R.color.blue));


                Format f = new SimpleDateFormat("EEEE");
                String day = f.format(new Date());

                if(!day.equalsIgnoreCase("friday")){
                    Toast.makeText(getContext(), "You can only submit team projects during Friday.", Toast.LENGTH_LONG).show();
                }
            }

            if(v==universityLayout || v==universityImage || v==universityTitle){
                universityImage.setTextColor(getActivity().getResources().getColor(R.color.blue));
                universityTitle.setTextColor(getActivity().getResources().getColor(R.color.blue));

                Toast.makeText(getContext(), "This feature is not available yet.", Toast.LENGTH_LONG).show();

            }

            if(v==teammonitoringLayout || v==teammonitoringImage || v==teammonitoringTitle){
                teammonitoringImage.setTextColor(getActivity().getResources().getColor(R.color.black));
                teammonitoringTitle.setTextColor(getActivity().getResources().getColor(R.color.black));

                if(position == 0){
                    Toast.makeText(getContext(), "You don't have a team yet. Please contact a staff to get you into a team.", Toast.LENGTH_LONG).show();
                }
            }


        }else{
            if(v==myprojectLayout || v==myprojectImage || v==myprojectTitle){
                myprojectImage.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
                myprojectTitle.setTextColor(getActivity().getResources().getColor(R.color.darkGray));


            }

            if(v==teamprojectLayout || v==teamprojectImage || v== teamprojectTitle){
                teamprojectImage.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
                teamprojectTitle.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
            }

            if(v==weeklyreportLayout || v==weeklyreportImage || v==weeklyreportTitle){
                weeklyreportImage.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
                weeklyreportTitle.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
            }

            if(v==universityLayout || v==universityImage || v==universityTitle){
                universityImage.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
                universityTitle.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
            }

            if(v==teammonitoringLayout || v==teammonitoringImage || v==teammonitoringTitle){
                teammonitoringImage.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
                teammonitoringTitle.setTextColor(getActivity().getResources().getColor(R.color.darkGray));
            }


        }
        return true;
    }
}
