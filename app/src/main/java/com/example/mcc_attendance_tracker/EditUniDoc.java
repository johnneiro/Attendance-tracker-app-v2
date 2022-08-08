package com.example.mcc_attendance_tracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditUniDoc extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    EditText doc_name, req_date, dead_Line, gdrive, ojt_cor_name, ojt_cor_email;
    Spinner format;
    String date_subs;
    Button save, cancel;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_unidoc);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.show();

        doc_name = findViewById(R.id.edit_uni_docs_name_textbox);
        ojt_cor_name = findViewById(R.id.edit_ojt_coordinator_name);
        ojt_cor_email = findViewById(R.id.edit_ojt_coordinator_email);
        req_date = findViewById(R.id.edit_unidoc_req_date);
        dead_Line = findViewById(R.id.edit_unidoc_deadline);
        gdrive = findViewById(R.id.edit_uni_docs_gdrive_textbox);
        format = findViewById(R.id.edit_uni_docs_file_format_spinner);
        save = findViewById(R.id.save_uni_docs_button);
        cancel = findViewById(R.id.cancel_uni_docs_button);

        Intent intent = getIntent();
        String documentTitle = intent.getStringExtra("document_title");
        String ojtCorName = intent.getStringExtra("coordinator_name");
        String ojtCorEmail = intent.getStringExtra("coordinator_email");
        String formatType = intent.getStringExtra("file_format");
        String reqDate = intent.getStringExtra("date_submitted");
        String deadLine = intent.getStringExtra("deadline");
        String google_drive = intent.getStringExtra("gdrive_link");
        int documentID = intent.getIntExtra("document_id", 0);

        date_subs = reqDate;

        String[] formatTypeList = {"IMAGE","DOCUMENT","PPTX","XLS","VIDEO","AUDIO","OTHERS"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, formatTypeList);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        format.setAdapter(aa);

        for(int i = 0; i< format.getAdapter().getCount(); i++){
            if(formatType.equalsIgnoreCase(format.getItemAtPosition(i).toString())){
                format.setSelection(i);
            }
        }

        doc_name.setText(documentTitle);
        ojt_cor_name.setText(ojtCorName);
        ojt_cor_email.setText(ojtCorEmail);
        req_date.setText(reqDate);
        dead_Line.setText(deadLine);
        gdrive.setText(google_drive);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);

        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        req_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditUniDoc.this, date1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("Updating University Document");
                progressDialog.setMessage("Please wait, we are processing your data.");
                progressDialog.show();
                editDocument(documentID);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void editDocument(int doc_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_EDIT_DOCUMENT,
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
                params.put("document_ID", String.valueOf(doc_id));
                params.put("document_title", doc_name.getText().toString().trim());
                params.put("coordinator_name", ojt_cor_name.getText().toString().trim());
                params.put("coordinator_email", ojt_cor_email.getText().toString().trim());
                params.put("file_format", format.getSelectedItem().toString().trim());
                params.put("date_submitted", req_date.getText().toString().trim());
                params.put("deadline", dead_Line.getText().toString().trim());
                params.put("gdrive_link", gdrive.getText().toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void updateLabel(){
        String myFormat="yyyy/MM/dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        req_date.setText(dateFormat.format(myCalendar.getTime()));
    }

}
