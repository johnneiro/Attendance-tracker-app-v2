package com.example.mcc_attendance_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class EditProject extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    EditText task, date, gdrive;
    Spinner format;
    String date_subs;
    Button save, cancel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        task = findViewById(R.id.edit_project_task_name_textbox);
        date = findViewById(R.id.edit_project_date_assigned_textbox);
        gdrive = findViewById(R.id.edit_project_gdrive_textbox);
        format = findViewById(R.id.edit_project_file_format_spinner);
        save = findViewById(R.id.edit_project_save_button);
        cancel = findViewById(R.id.edit_project_cancel_button);

        Intent intent = getIntent();
        String taskName = intent.getStringExtra("task_name");
        String formatType = intent.getStringExtra("format");
        String date_assigned = intent.getStringExtra("date_assigned");
        String date_submitted = intent.getStringExtra("date_submitted");
        String google_drive = intent.getStringExtra("gdrive");
        int projectID = intent.getIntExtra("projectID", 0);

        date_subs = date_submitted;

        String[] formatTypeList = {"IMAGE","DOCUMENT","PPTX","XLS","VIDEO","AUDIO","OTHERS"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, formatTypeList);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        format.setAdapter(aa);

        for(int i = 0; i< format.getAdapter().getCount(); i++){
            if(formatType.equalsIgnoreCase(format.getItemAtPosition(i).toString())){
                format.setSelection(i);
            }
        }

        task.setText(taskName);

        date.setText(date_assigned);
        gdrive.setText(google_drive);

        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditProject.this, date1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProject(projectID);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void editProject(int proj_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_EDIT_PROJECT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            finish();

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
                params.put("task_name", task.getText().toString().trim());
                params.put("file_format", format.getSelectedItem().toString().trim());
                params.put("date_assigned", date.getText().toString().trim());
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
        date.setText(dateFormat.format(myCalendar.getTime()));
    }

}
