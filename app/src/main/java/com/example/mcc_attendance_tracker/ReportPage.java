package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
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
import androidx.fragment.app.Fragment;
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

import static android.content.Context.MODE_PRIVATE;

public class ReportPage extends Fragment {
    private ArrayList<ReportPageModel> reportPageModelArrayList;
    private ReportPageAdapter reportPageAdapter;
    RecyclerView recyclerView;
    EditText search;
    TextView filter, refresh;

    LinearLayout nodatalayout;

    String userEmail;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;

    public ReportPage(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        recyclerView = view.findViewById(R.id.report_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search = view.findViewById(R.id.report_search);
        filter = view.findViewById(R.id.report_filter);
        refresh = view.findViewById(R.id.report_refresh);
        nodatalayout = view.findViewById(R.id.noDataLayout);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait, we are loading your data.");
                progressDialog.show();
                getReports(userEmail);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter.getText().toString().equalsIgnoreCase("\uf0b0") ||
                        filter.getText().toString().equalsIgnoreCase("\uf160")){
                    filter.setText("\uf161");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Please wait, we are loading your data.");
                    progressDialog.show();
                    getReports(userEmail);
                }else if(filter.getText().toString().equalsIgnoreCase("\uf161")){
                    filter.setText("\uf160");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Please wait, we are loading your data.");
                    progressDialog.show();
                    getReports(userEmail);
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
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait, we are loading your data.");
                progressDialog.show();
                getReports(userEmail);
            }
        });

        getReports(userEmail);



        return view;
    }

    @Override
    public void onResume() {
        getReports(userEmail);
        super.onResume();
    }

    private void getReports(String email){
        final String userSearch = search.getText().toString().trim();
        String filterBy = "NA";
        if(filter.getText().toString().trim().equalsIgnoreCase("\uf160")){
            filterBy = "ASC";
        }else{
            filterBy = "DESC";
        }
        final String userFilter = filterBy;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REPORTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray reports = new JSONArray(response);
                            reportPageModelArrayList = new ArrayList<>();

                            if(reports.length() == 0){
                                recyclerView.setVisibility(View.GONE);
                                nodatalayout.setVisibility(View.VISIBLE);
                            }else{
                                recyclerView.setVisibility(View.VISIBLE);
                                nodatalayout.setVisibility(View.GONE);

                                for(int i = 0; i< reports.length(); i++){
                                    JSONObject reportsObject = reports.getJSONObject(i);
                                    String report_title = reportsObject.getString("report_title");
                                    int report_id = reportsObject.getInt("report_id");
                                    String report_details = reportsObject.getString("report_details");
                                    String report_documents = reportsObject.getString("gdrive_link");
                                    String date_submitted = reportsObject.getString("dateReport");
                                    String report_status = reportsObject.getString("report_status");
                                    String ticket_number = reportsObject.getString("ticket_no");

                                    ReportPageModel reportPageModel = new ReportPageModel(report_title, report_details, report_documents, date_submitted, report_status, ticket_number, report_id);
                                    reportPageModelArrayList.add(reportPageModel);

                                }
                                reportPageAdapter = new ReportPageAdapter(getContext(), reportPageModelArrayList);
                                recyclerView.setAdapter(reportPageAdapter);
                                reportPageAdapter.notifyDataSetChanged();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
