package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class TeamMonitoringDetails extends AppCompatActivity {
    TextView task, format, dsub, dass, gdrive, back, status;
    Button edit, delete;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_project_status);
        progressDialog= new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.show();

        task = findViewById(R.id.task_name);
        format = findViewById(R.id.file_form);
        dsub = findViewById(R.id.date_sub);
        dass = findViewById(R.id.date_ass);
        gdrive = findViewById(R.id.g_link);
        edit = findViewById(R.id.view_project_edit_button);
        delete = findViewById(R.id.view_project_delete_button);
        back = findViewById(R.id.view_project_back);
        status =findViewById(R.id.status);



        Intent i = getIntent();
        String taskName = i.getStringExtra("task_name");
        String formatType = i.getStringExtra("format");
        String date_assigned = i.getStringExtra("date_assigned");
        String date_submitted = i.getStringExtra("date_submitted");
        String google_drive = i.getStringExtra("gdrive");
        String mstatus = i.getStringExtra("status");
        int projectID = i.getIntExtra("projectID", 0);

        task.setText(taskName);
        format.setText(formatType);
        dsub.setText(date_submitted);
        dass.setText(date_assigned);
        gdrive.setText(google_drive);
        status.setText(mstatus);

        if(mstatus.equalsIgnoreCase("Already checked")){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
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
                Intent intent = new Intent(v.getContext(), EditProject.class);
                intent.putExtra("task_name", taskName);
                intent.putExtra("projectID", projectID);
                intent.putExtra("format", formatType);
                intent.putExtra("date_assigned", date_assigned);
                intent.putExtra("date_submitted", date_submitted);
                intent.putExtra("gdrive", google_drive);
                v.getContext().startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setTitle("Deleting Project");
                                progressDialog.setMessage("Please wait, we are processing your data.");
                                progressDialog.show();
                                deleteProject(projectID);
                                dialog.dismiss();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TeamMonitoringDetails.this);
                builder.setMessage("Are you sure you want to delete this project?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


    }

    private void deleteProject(int proj_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_DELETE_PROJECT,
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
                params.put("project_status_ID", String.valueOf(proj_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
