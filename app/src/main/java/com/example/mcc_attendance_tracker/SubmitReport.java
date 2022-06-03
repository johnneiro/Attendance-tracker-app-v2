package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubmitReport extends AppCompatActivity {
    TextView back;
    EditText subject, details, files, ticket;
    Button submit;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor1;
    String userEmail;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_report);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferences1 = getApplicationContext().getSharedPreferences("DetailsSharedPref", MODE_PRIVATE);
        editor1 =sharedPreferences1.edit();

        userEmail = sharedPreferences.getString("email", "");

        subject = findViewById(R.id.submit_report_subject_textbox);
        details = findViewById(R.id.submit_report_details_textbox);
        files = findViewById(R.id.submit_report_files_textbox);
        submit = findViewById(R.id.submit_report_submit_button);
        back = findViewById(R.id.submit_report_back_text);
        ticket = findViewById(R.id.submit_report_ticket_id);

        String ticketNo = String.valueOf(year) + randomStringGenerator(10);
        ticket.setText(ticketNo);
        ticket.setTextColor(getResources().getColor(R.color.black));

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("ticket", ticket.getText().toString().trim());
                editor1.putString("subject", subject.getText().toString().trim());
                editor1.putString("files", files.getText().toString().trim());
                editor1.commit();
                Intent intent = new Intent(SubmitReport.this, TextDetails.class);
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
            subject.setText(sharedPreferences1.getString("subject", ""));
        }
        if(sharedPreferences1.contains("files")){
            files.setText(sharedPreferences1.getString("files", ""));
        }
        if(sharedPreferences1.contains("ticket")){
            ticket.setText(sharedPreferences1.getString("ticket", ""));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.clear();
                editor1.commit();
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("Submitting Report");
                progressDialog.setMessage("Please wait, we are processing your data.");
                progressDialog.show();
                insertData(userEmail);
            }
        });


    }

    private void insertData(String email){
        final String reportTitle = subject.getText().toString().trim();
        final String reportDetails = details.getText().toString().trim();
        final String reportDocuments = files.getText().toString().trim();
        final String reportTicket = ticket.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_SUBMIT_REPORT,
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
                params.put("ticket_no", reportTicket);
                params.put("email", email);
                params.put("report_title", reportTitle);
                params.put("report_details", sharedPreferences1.getString("details", ""));
                params.put("gdrive_link", reportDocuments);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private String randomStringGenerator(int len){
        String chars= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i< len; i++){
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
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
