package com.example.mcc_attendance_tracker;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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


public class UniDocStatus extends AppCompatActivity {
    private ArrayList<UniDocStatusModel> unidocStatusModelArrayList;
    private UniDocStatusAdapter unidocStatusAdapter;
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
        setContentView(R.layout.activity_unidoc_status);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        recyclerView = findViewById(R.id.unidoc_status_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        search = findViewById(R.id.unidoc_status_search);
        filter = findViewById(R.id.unidoc_status_filter);
        refresh = findViewById(R.id.unidoc_status_refresh);
        add = findViewById(R.id.unidoc_status_add);
        back = findViewById(R.id.unidoc_status_back_text);
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
                Intent intent= new Intent(UniDocStatus.this, SubmitUniDoc.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUniDoc(userEmail);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter.getText().toString().equalsIgnoreCase("\uf0b0") ||
                        filter.getText().toString().equalsIgnoreCase("\uf160")){
                    filter.setText("\uf161");
                    getUniDoc(userEmail);
                }else if(filter.getText().toString().equalsIgnoreCase("\uf161")){
                    filter.setText("\uf160");
                    getUniDoc(userEmail);
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
                    getUniDoc(userEmail);
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



        getUniDoc(userEmail);


    }


    private void getUniDoc(String email){
        final String userSearch = search.getText().toString().trim();
        String filterBy = "NA";
        if(filter.getText().toString().trim().equalsIgnoreCase("\uf160")){
            filterBy = "ASC";
        }else{
            filterBy = "DESC";
        }
        final String userFilter = filterBy;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UNIVERSITY_DOCUMENTS_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray documents = new JSONArray(response);
                            unidocStatusModelArrayList = new ArrayList<>();

                            if(documents.length() == 0){
                                recyclerView.setVisibility(View.GONE);
                                nodatalayout.setVisibility(View.VISIBLE);
                            }else{
                                recyclerView.setVisibility(View.VISIBLE);
                                nodatalayout.setVisibility(View.GONE);

                                for(int i = 0; i< documents.length(); i++){
                                    JSONObject documentObjects = documents.getJSONObject(i);
                                    String documentTitle = documentObjects.getString("document_title");
                                    int document_id = documentObjects.getInt("document_id");
                                    String corName = documentObjects.getString("coordinator_name");
                                    String corEmail = documentObjects.getString("coordinator_email");
                                    String file_format = documentObjects.getString("file_format");
                                    String date_submitted = documentObjects.getString("date_submitted");
                                    String deadLine = documentObjects.getString("deadline");
                                    String google_drive_link = documentObjects.getString("gdrive_link");
                                    String status_text = documentObjects.getString("status");

                                    UniDocStatusModel unidocStatusModel = new UniDocStatusModel(documentTitle, corName, corEmail, date_submitted, file_format, document_id, google_drive_link, deadLine, status_text);
                                    unidocStatusModelArrayList.add(unidocStatusModel);

                                }
                                unidocStatusAdapter = new UniDocStatusAdapter(UniDocStatus.this, unidocStatusModelArrayList);
                                recyclerView.setAdapter(unidocStatusAdapter);
                                unidocStatusAdapter.notifyDataSetChanged();
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
        getUniDoc(userEmail);
        super.onResume();
    }
}
