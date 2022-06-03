package com.example.mcc_attendance_tracker;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
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


public class ProjectStatus extends AppCompatActivity {
    private ArrayList<ProjectStatusModel> projectStatusModelArrayList;
    private ProjectStatusAdapter projectStatusAdapter;
    RecyclerView recyclerView;
    EditText search;
    TextView filter, refresh, add, back;

    LinearLayout nodatalayout;

    String userEmail;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_status);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        recyclerView = findViewById(R.id.project_status_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        search = findViewById(R.id.project_status_search);
        filter = findViewById(R.id.project_status_filter);
        refresh = findViewById(R.id.project_status_refresh);
        add = findViewById(R.id.project_status_add);
        back = findViewById(R.id.my_project_back_text);
        nodatalayout = findViewById(R.id.noDataLayout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ProjectStatus.this, SubmitProject.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProjects(userEmail);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter.getText().toString().equalsIgnoreCase("\uf0b0") ||
                        filter.getText().toString().equalsIgnoreCase("\uf160")){
                    filter.setText("\uf161");
                    getProjects(userEmail);
                }else if(filter.getText().toString().equalsIgnoreCase("\uf161")){
                    filter.setText("\uf160");
                    getProjects(userEmail);
                }
            }
        });

        Handler handler1 = new Handler();
        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Please wait, we are loading your data.");
                    progressDialog.show();
                    getProjects(userEmail);
                }
            }
        };

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler1.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                last_text_edit = System.currentTimeMillis();
                handler1.postDelayed(input_finish_checker, delay);
            }
        });



        getProjects(userEmail);


    }


    private void getProjects(String email){
        final String userSearch = search.getText().toString().trim();
        String filterBy = "NA";
        if(filter.getText().toString().trim().equalsIgnoreCase("\uf160")){
            filterBy = "ASC";
        }else{
            filterBy = "DESC";
        }
        final String userFilter = filterBy;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_PROJECT_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray projects = new JSONArray(response);
                            projectStatusModelArrayList = new ArrayList<>();

                            if(projects.length() == 0){
                                recyclerView.setVisibility(View.GONE);
                                nodatalayout.setVisibility(View.VISIBLE);
                            }else{
                                recyclerView.setVisibility(View.VISIBLE);
                                nodatalayout.setVisibility(View.GONE);

                                for(int i = 0; i< projects.length(); i++){
                                    JSONObject projectObjects = projects.getJSONObject(i);
                                    String taskname = projectObjects.getString("project_name");
                                    int project_id = projectObjects.getInt("project_id");
                                    String file_format = projectObjects.getString("file_format");
                                    String date_assigned = projectObjects.getString("date_assigned");
                                    String date_submitted = projectObjects.getString("date_submitted");
                                    String google_drive_link = projectObjects.getString("google_drive_link");
                                    String status_text = projectObjects.getString("status");

                                    ProjectStatusModel projectStatusModel = new ProjectStatusModel(taskname, date_submitted, file_format, project_id, google_drive_link, date_assigned, status_text);
                                    projectStatusModelArrayList.add(projectStatusModel);

                                }
                                projectStatusAdapter = new ProjectStatusAdapter(ProjectStatus.this, projectStatusModelArrayList);
                                recyclerView.setAdapter(projectStatusAdapter);
                                projectStatusAdapter.notifyDataSetChanged();
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
        getProjects(userEmail);
        super.onResume();
    }
}
