package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import com.android.volley.Request;
//import com.android.volley.toolbox.StringRequest;

public class Dashboard extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private ArrayList<AnnouncementsModel> announcementsArrayList;

    LinearLayout nodatalayout;

    TextView day, date, time;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SharedPreferences alarmSharedPreference;
    SharedPreferences.Editor alarmSharedPrefEditor;

    Button btnTime, btnAttendanceLog;

    Context context;
    String mess = "time in";
    int buttonStatus = 0;
    ProgressDialog progressDialog;

    public Dashboard(Context context, ProgressDialog progressDialog) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = this.getActivity().getSharedPreferences("UserDataSharedPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        alarmSharedPreference = this.getActivity().getSharedPreferences("AlarmSharedPref", Context.MODE_PRIVATE);
        alarmSharedPrefEditor = alarmSharedPreference.edit();

        time = (TextClock) view.findViewById(R.id.dashboard_time_text);
        date = (TextView)view.findViewById(R.id.dashboard_date_text);
        day = (TextView)view.findViewById(R.id.dashboard_day_text);
        btnTime = (Button)view.findViewById(R.id.dashboard_timein_button);
        btnAttendanceLog = view.findViewById(R.id.dashboard_attendance_log_button);
        btnTime.setOnClickListener(this);
        btnAttendanceLog.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.dashboard_announcement_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        nodatalayout = view.findViewById(R.id.noDataLayout);


        checkUserTimeLog();
        getDate();
        viewAnnouncements();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);
        return view;

    }

    private void checkUserTimeLog(){
        final String userEmail = sharedPreferences.getString("email", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_CHECK_TIME_LOGS,
                (response) ->{

                    try{
                        JSONObject obj = new JSONObject(response);
                        if(!obj.getBoolean("error")){
                           editor.putInt("button_status", obj.getInt("btn_status"));
                           editor.putInt("last_id", obj.getInt("last_id"));
                           editor.commit();

                            buttonStatus = sharedPreferences.getInt("button_status", 0);
                            if (buttonStatus == 0) {
                                btnTime.setText("TIME-IN");
                                btnTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            } else if (buttonStatus == 1) {
                                btnTime.setText("TIME-OUT");
                                btnTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                            } else {
                                btnTime.setText("ATTENDANCE COMPLETED");
                                btnTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                            }
                        }


                    }catch(JSONException e){
                        e.printStackTrace();
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", userEmail);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void viewAnnouncements(){
        final String userEmail = sharedPreferences.getString("email", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST , Constants.URL_ANNOUNCEMENTS,(response) ->{

            try{
                JSONArray announcements = new JSONArray(response);
                announcementsArrayList = new ArrayList<>();

                if (announcements.length() == 0){
                    nodatalayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    nodatalayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    for(int i=0; i<announcements.length(); i++){
                        JSONObject announcementsObject = announcements.getJSONObject(i);
                        int webId = announcementsObject.getInt("webinarID");
                        boolean hasParticipated = announcementsObject.getBoolean("participated");
                        String title = announcementsObject.getString("title");
                        String date = announcementsObject.getString("meeting_date");
                        String time = announcementsObject.getString("meeting_time");
                        String details = announcementsObject.getString("details");
                        String speaker = announcementsObject.getString("speaker");
                        String status = announcementsObject.getString("webinar_status");
                        String link = announcementsObject.getString("meeting_link");
                        String fee = announcementsObject.getString("registration_fee");


                        AnnouncementsModel announcementsModel = new AnnouncementsModel(webId, title, date, time, details, speaker, status,fee,link, hasParticipated);
                        announcementsArrayList.add(announcementsModel);


                    }
                    adapter = new AnnouncementAdapter(getContext(), announcementsArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e){

                e.printStackTrace();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", userEmail);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void onClick (View v){
            if(v==btnTime){
                if(!btnTime.getText().toString().equalsIgnoreCase("attendance completed")){
                    progressDialog.setMessage("Please wait, we are processing your data.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    message();
                }else{
                    btnTime.setClickable(false);
                }
            }

            if(v==btnAttendanceLog){
                Intent i = new Intent(getContext(), AttendanceLog.class);
                getContext().startActivity(i);
            }

    }

    public void getDate(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET , Constants.URL_SERVER_DATE,(response) ->{

            try{
                JSONObject obj = new JSONObject(response);

                String date1 = obj.getString("date");
                String day1 = obj.getString("day");

                date.setText(date1);
                day.setText(day1);


            } catch (JSONException e){

                e.printStackTrace();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void message (){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        internLogged();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity() );
        builder.setMessage("Are you sure you want to "+ mess + "? \n (" + time.getText().toString() +")").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    private void internLogged(){
        final String userEmail = sharedPreferences.getString("email", "");
        final String btnName = btnTime.getText().toString().toUpperCase().trim();
        final String start_shift = sharedPreferences.getString("start_shift", "");
        final String end_shift = sharedPreferences.getString("end_shift","");
        final String required_hrs = sharedPreferences.getString("required_hours", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_TIME_LOG,
                (response) ->{

                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){

                                btnTime.setText(obj.getString("btnText"));
                                if(obj.getString("btnText").equalsIgnoreCase("time-in")){
                                    editor.putInt("last_id", obj.getInt("last_id"));
                                    editor.commit();
                                }else{
                                    editor.putInt("last_id", 0);
                                    editor.commit();
                                }

                                if(btnTime.getText().toString().equalsIgnoreCase("time-in")){
                                    btnTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    editor.putInt("button_status", 0);
                                    editor.commit();
                                }else if(btnTime.getText().toString().equalsIgnoreCase("time-out")){
                                    btnTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                                    editor.putInt("button_status", 1);
                                    editor.commit();
                                }else{
                                    btnTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                                    editor.putInt("button_status", 2);
                                    editor.commit();

                                }
                            }
                            String message = obj.getString("message");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }, 2000);

                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", userEmail);
                params.put("btnName", btnName);
                params.put("last_id", String.valueOf(sharedPreferences.getInt("last_id", 0)));
                params.put("start_time", start_shift);
                params.put("end_time", end_shift);
                params.put("company", sharedPreferences.getString("company", ""));
                params.put("required_hours", required_hrs);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewAnnouncements();
    }
}