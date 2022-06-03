package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditReport extends AppCompatActivity {

    EditText details, title, files;
    Button save, cancel;

    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor1;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.show();
        sharedPreferences1 = getApplicationContext().getSharedPreferences("DetailsSharedPref", MODE_PRIVATE);
        editor1 =sharedPreferences1.edit();

        title = findViewById(R.id.edit_report_subject_textbox);
        details = findViewById(R.id.edit_report_details_textbox);
        files = findViewById(R.id.edit_report_files_textbox);
        save = findViewById(R.id.edit_report_save_button);
        cancel = findViewById(R.id.edit_report_cancel_button);

        Intent i = getIntent();
        String reportTitle = i.getStringExtra("report_title");
        String reportDetails = i.getStringExtra("report_details");
        String reportDocuments = i.getStringExtra("report_documents");
        int reportID = i.getIntExtra("reportID", 0);

        title.setText(reportTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            details.setText(Html.fromHtml(reportDetails, Html.FROM_HTML_MODE_COMPACT));
        } else {
            details.setText(Html.fromHtml(reportDetails));
        }
        files.setText(reportDocuments);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences1.contains("details")){
                    editor1.putString("details", sharedPreferences1.getString("details", ""));
                    editor1.commit();
                }else{
                    editor1.putString("details",reportDetails);
                    editor1.commit();
                }
                editor1.putString("subject", title.getText().toString().trim());
                editor1.putString("files", files.getText().toString().trim());
                editor1.commit();
                Intent intent = new Intent(EditReport.this, TextDetails.class);
                startActivity(intent);
            }
        });

        if(sharedPreferences1.contains("details")){
            if(!sharedPreferences1.getString("details", "").equals("<p><br></p>")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    details.setText(Html.fromHtml(sharedPreferences1.getString("details", ""), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    details.setText(Html.fromHtml(sharedPreferences1.getString("details", "")));
                }
            }else{
                details.getText().clear();
            }
        }

        if(sharedPreferences1.contains("subject")){
            title.setText(sharedPreferences1.getString("subject", ""));
        }
        if(sharedPreferences1.contains("files")){
            files.setText(sharedPreferences1.getString("files", ""));
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Updating Report Form");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait, we are processing your data.");
                progressDialog.show();
                editReport(reportID);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.clear();
                editor1.commit();
                finish();
            }
        });


    }

    private void editReport(int report_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_EDIT_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                editor1.clear();
                                editor1.commit();
                            }
                            String message = obj.getString("message");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();
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
                params.put("report_ID", String.valueOf(report_id));
                params.put("report_title", title.getText().toString().trim());
                params.put("report_details", sharedPreferences1.getString("details", ""));
                params.put("report_documents", files.getText().toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
