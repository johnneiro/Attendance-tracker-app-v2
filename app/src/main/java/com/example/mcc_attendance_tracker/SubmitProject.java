package com.example.mcc_attendance_tracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubmitProject extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    TextView back;
    EditText taskname, dateAssigned, gdrive;
    Spinner format;
    Button submit;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userEmail;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_project_status);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        back = findViewById(R.id.submit_project_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        taskname = findViewById(R.id.submit_project_task_name_textbox);
        dateAssigned = findViewById(R.id.submit_project_date_assigned);
        gdrive = findViewById(R.id.submit_project_gdrive_textbox);
        format = findViewById(R.id.submit_project_file_format_spinner);
        submit = findViewById(R.id.submit_project_button);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        dateAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SubmitProject.this, date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        String[] formatTypeList = {"IMAGE","DOCUMENT","PPTX","XLS","VIDEO","AUDIO","OTHERS"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, formatTypeList);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        format.setAdapter(aa);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("Submitting Project");
                progressDialog.setMessage("Please wait, we are processing your data.");
                insertData(userEmail);
            }
        });

    }
    private void updateLabel(){
        String myFormat="yyyy/MM/dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateAssigned.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void insertData(String email){
        final String taskName = taskname.getText().toString().trim();
        final String fileFormat = format.getSelectedItem().toString();
        final String date = dateAssigned.getText().toString().trim();
        final String gDrive = gdrive.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_SUBMIT_PROJECT,
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
                params.put("email", email);
                params.put("task_name", taskName);
                params.put("file_format", fileFormat);
                params.put("date_assigned", date);
                params.put("gdrive_link", gDrive);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
