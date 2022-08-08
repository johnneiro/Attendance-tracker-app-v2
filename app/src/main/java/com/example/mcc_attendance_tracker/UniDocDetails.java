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

public class UniDocDetails extends AppCompatActivity {
    TextView task, format, dsub, dass, gdrive, back, status, cor_name, cor_email;
    Button edit, delete;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_unidoc_status);
        progressDialog= new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.show();

        task = findViewById(R.id.file_name);
        cor_name = findViewById(R.id.ojt_coordinator_name);
        cor_email = findViewById(R.id.ojt_coordinator_email);
        format = findViewById(R.id.file_type);
        dsub = findViewById(R.id.uni_date_sub);
        dass = findViewById(R.id.uni_date_ass);
        gdrive = findViewById(R.id.uni_g_link);
        edit = findViewById(R.id.view_unidoc_edit_button);
        delete = findViewById(R.id.view_unidoc_delete_button);
        back = findViewById(R.id.view_unidoc_back);
        status =findViewById(R.id.unidoc_status);



        Intent i = getIntent();
        String taskName = i.getStringExtra("document_title");
        String corName = i.getStringExtra("coordinator_name");
        String corEmail = i.getStringExtra("coordinator_email");
        String formatType = i.getStringExtra("file_format");
        String date_submitted = i.getStringExtra("date_submitted");
        String deadLine = i.getStringExtra("deadline");
        String google_drive = i.getStringExtra("gdrive_link");
        String mstatus = i.getStringExtra("status");
        int documentID = i.getIntExtra("document_id", 0);

        task.setText(taskName);
        cor_name.setText(corName);
        cor_email.setText(corEmail);
        format.setText(formatType);
        dsub.setText(date_submitted);
        dass.setText(deadLine);
        gdrive.setText(google_drive);
        status.setText(mstatus);

        if(mstatus.equalsIgnoreCase("Signed")){
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
                intent.putExtra("document_title", taskName);
                intent.putExtra("document_id", documentID);
                intent.putExtra("coordinator_name", corName);
                intent.putExtra("coordinator_email", corEmail);
                intent.putExtra("file_format", formatType);
                intent.putExtra("date_submitted", date_submitted);
                intent.putExtra("deadline", deadLine);
                intent.putExtra("gdrive_link", google_drive);
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
                                progressDialog.setTitle("Deleting Document");
                                progressDialog.setMessage("Please wait, we are processing your data.");
                                progressDialog.show();
                                deleteDocument(documentID);
                                dialog.dismiss();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(UniDocDetails.this);
                builder.setMessage("Are you sure you want to delete this document?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


    }

    private void deleteDocument(int unidoc_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_DELETE_DOCUMENT,
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
                params.put("document_id", String.valueOf(unidoc_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
