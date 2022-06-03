package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.util.HashMap;
import java.util.Map;

public class LeaveDetails extends AppCompatActivity {
    TextView status, type, date, details, back;
    Button withdraw, edit;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_leave_status);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        back = findViewById(R.id.view_leave_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        status = findViewById(R.id.view_leave_status);
        type = findViewById(R.id.view_leave_type);
        date = findViewById(R.id.view_leave_date);
        details = findViewById(R.id.view_leave_details);

        withdraw = findViewById(R.id.view_leave_withdraw_leave_button);
        edit = findViewById(R.id.view_leave_edit_button);

        Intent i = getIntent();
        String leave_status = i.getStringExtra("leave_status");
        String leave_reason = i.getStringExtra("leave_reason");
        String leave_details = i.getStringExtra("leave_details");
        String date_start = i.getStringExtra("date_start");
        String date_end = i.getStringExtra("date_end");
        int leaveID = i.getIntExtra("leaveID", 0);

        type.setText(leave_reason);
        date.setText(date_start + " - " + date_end);
        status.setText(leave_status);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            details.setText(Html.fromHtml(leave_details, Html.FROM_HTML_MODE_COMPACT));
        } else {
            details.setText(Html.fromHtml(leave_details));
        }

        if (!leave_status.equalsIgnoreCase("pending")){
            edit.setVisibility(View.GONE);
            withdraw.setVisibility(View.GONE);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditLeave.class);
                intent.putExtra("leaveID", leaveID);
                intent.putExtra("leave_reason", leave_reason);
                intent.putExtra("leave_details", leave_details);
                intent.putExtra("date_start", date_start);
                intent.putExtra("date_end", date_end);
                intent.putExtra("leave_status", leave_status);
                v.getContext().startActivity(intent);
                finish();
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                progressDialog.setTitle("Withdrawing Leave Request");
                                progressDialog.setMessage("Please wait, we are processing your data.");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                withdrawLeave(leaveID);

                                dialog.dismiss();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(LeaveDetails.this);
                builder.setMessage("Are you sure you want to delete this project?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


    }

    private void withdrawLeave(int leave_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_DELETE_LEAVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("message");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

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
                params.put("leave_ID", String.valueOf(leave_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
