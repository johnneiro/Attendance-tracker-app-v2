package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaveStatus extends AppCompatActivity {
    private ArrayList<LeaveStatusModel> leaveStatusModelArrayList;
    private LeaveStatusAdapter leaveStatusAdapter;
    RecyclerView recyclerView;
    EditText search;
    TextView filter, refresh, back;
    TextView fileLeave;

    LinearLayout nodatalayout;

    String userEmail;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_status);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        recyclerView = findViewById(R.id.leave_Status_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        nodatalayout = findViewById(R.id.noDataLayout);

        search = findViewById(R.id.leave_status_search_textbox);
        filter = findViewById(R.id.leave_status_filter_text);
        refresh = findViewById(R.id.leave_status_refresh_text);
        fileLeave = findViewById(R.id.leave_status_submit_button);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please wait, we are loading your data.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                getLeave(userEmail);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter.getText().toString().equalsIgnoreCase("\uf0b0") ||
                        filter.getText().toString().equalsIgnoreCase("\uf160")){
                    filter.setText("\uf161");
                    progressDialog.setMessage("Please wait, we are loading your data.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    getLeave(userEmail);
                }else if(filter.getText().toString().equalsIgnoreCase("\uf161")){
                    filter.setText("\uf160");
                    progressDialog.setMessage("Please wait, we are loading your data.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    getLeave(userEmail);
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                progressDialog.setMessage("Please wait, we are loading your data.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                getLeave(userEmail);
            }
        });

        fileLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveStatus.this, SubmitLeave.class);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.leave_status_back_text);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        getLeave(userEmail);
    }

    private void getLeave(String email){
        final String userSearch = search.getText().toString().trim();
        String filterBy = "NA";
        if(filter.getText().toString().trim().equalsIgnoreCase("\uf160")){
            filterBy = "ASC";
        }else{
            filterBy = "DESC";
        }
        final String userFilter = filterBy;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_LEAVES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray leaves = new JSONArray(response);
                            leaveStatusModelArrayList = new ArrayList<>();

                            if(leaves.length() == 0){
                                recyclerView.setVisibility(View.GONE);
                                nodatalayout.setVisibility(View.VISIBLE);
                            }else{
                                recyclerView.setVisibility(View.VISIBLE);
                                nodatalayout.setVisibility(View.GONE);

                                for(int i = 0; i< leaves.length(); i++){
                                    JSONObject leaveObjects = leaves.getJSONObject(i);
                                    String leave_reason = leaveObjects.getString("leave_reason");
                                    int leave_report_id = leaveObjects.getInt("leave_report_id");
                                    String leave_reason_details = leaveObjects.getString("leave_reason_details");
                                    String date_start = leaveObjects.getString("date_start");
                                    String date_end = leaveObjects.getString("date_end");
                                    String leave_status = leaveObjects.getString("leave_status");

                                    LeaveStatusModel leaveStatusModel = new LeaveStatusModel(leave_reason, leave_reason_details, date_start, date_end, leave_status, leave_report_id);
                                    leaveStatusModelArrayList.add(leaveStatusModel);

                                }
                                leaveStatusAdapter = new LeaveStatusAdapter(LeaveStatus.this, leaveStatusModelArrayList);
                                recyclerView.setAdapter(leaveStatusAdapter);
                                leaveStatusAdapter.notifyDataSetChanged();
                            }
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 2000);



                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", email);
                params.put("search", userSearch);
                params.put("filter", userFilter);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        getLeave(userEmail);
        super.onResume();
    }
}
