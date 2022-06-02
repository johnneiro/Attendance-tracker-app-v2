package com.example.mcc_attendance_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class ReportDetails extends AppCompatActivity {
    TextView status, title, date, link, details,back, ticket;
    Button edit, delete;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_report);

        ticket = findViewById(R.id.view_report_ticket);
        status = findViewById(R.id.view_report_status);
        title = findViewById(R.id.view_report_title);
        date = findViewById(R.id.view_report_date);
        link = findViewById(R.id.view_report_link);
        details = findViewById(R.id.view_report_details);
        back = findViewById(R.id.view_report_back_text);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit = findViewById(R.id.view_report_edit_button);
        delete = findViewById(R.id.view_report_delete_button);

        Intent i = getIntent();
        String reportStatus = i.getStringExtra("report_status");
        String reportTitle = i.getStringExtra("report_title");
        String dateSubmitted = i.getStringExtra("date_submitted");
        String reportDetails = i.getStringExtra("report_details");
        String reportDocuments = i.getStringExtra("report_documents");
        String ticketNo = i.getStringExtra("ticket_no");
        int reportID = i.getIntExtra("reportID", 0);

        status.setText(reportStatus);
        title.setText(reportTitle);
        date.setText(dateSubmitted);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            details.setText(Html.fromHtml(reportDetails, Html.FROM_HTML_MODE_COMPACT));
        } else {
            details.setText(Html.fromHtml(reportDetails));
        }

        link.setText(reportDocuments);
        ticket.setText(ticketNo);

        if(!reportStatus.equalsIgnoreCase("pending")){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditReport.class);
                intent.putExtra("report_title", reportTitle);
                intent.putExtra("reportID", reportID);
                intent.putExtra("report_details", reportDetails);
                intent.putExtra("report_documents", reportDocuments);
                intent.putExtra("report_status", reportStatus);
                intent.putExtra("ticket_no", ticketNo);
                v.getContext().startActivity(intent);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteReport(reportID);
                                dialog.dismiss();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ReportDetails.this);
                builder.setMessage("Are you sure you want to delete this project?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    private void deleteReport(int report_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_DELETE_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();

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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
